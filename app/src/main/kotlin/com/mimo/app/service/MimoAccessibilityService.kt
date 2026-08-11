package com.mimo.app.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import com.mimo.app.data.ClosureLog
import com.mimo.app.repository.MimoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * MIMO's core engine.
 *
 * MIMO only reads the *package name* of the app that just came to the
 * foreground (via [AccessibilityEvent.getPackageName]) to decide whether a
 * rule applies. It never inspects screen content, text, or any personal
 * data from other apps.
 */
class MimoAccessibilityService : AccessibilityService() {

    private lateinit var repository: MimoRepository
    private val handler = Handler(Looper.getMainLooper())
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())

    private var currentTrackedPackage: String? = null
    private var closeRunnable: Runnable? = null
    private var currentSessionStart: Long = 0L

    override fun onServiceConnected() {
        super.onServiceConnected()
        repository = MimoRepository.getInstance(applicationContext)
        NotificationHelper.ensureChannels(applicationContext)

        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.DEFAULT
            notificationTimeout = 100
        }
        serviceInfo = info
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val packageName = event?.packageName?.toString() ?: return
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        if (packageName == applicationContext.packageName) return

        if (packageName == currentTrackedPackage) return // same app still foreground

        // A different app (or home screen) is now foreground: cancel any pending close.
        cancelPendingClose()
        currentTrackedPackage = packageName

        serviceScope.launch {
            val rule = repository.getRule(packageName)
            if (rule == null || !rule.enabled) return@launch
            currentSessionStart = System.currentTimeMillis()
            scheduleClose(packageName, rule.appLabel, rule.delayMillis)
        }
    }

    private fun scheduleClose(packageName: String, appLabel: String, delayMillis: Long) {
        if (delayMillis <= 0) {
            performClose(packageName, appLabel, delayMillis)
            return
        }

        CountdownForegroundService.start(applicationContext, appLabel, delayMillis)

        val runnable = Runnable {
            // Only close if the tracked app is still the one in front.
            if (currentTrackedPackage == packageName) {
                performClose(packageName, appLabel, delayMillis)
            }
        }
        closeRunnable = runnable
        handler.postDelayed(runnable, delayMillis)
    }

    private fun performClose(packageName: String, appLabel: String, allowedDuration: Long) {
        performGlobalAction(GLOBAL_ACTION_HOME)
        CountdownForegroundService.stop(applicationContext)
        NotificationHelper.notifyClosed(applicationContext, appLabel)

        serviceScope.launch {
            repository.logClosure(
                ClosureLog(
                    packageName = packageName,
                    appLabel = appLabel,
                    allowedDurationMillis = allowedDuration
                )
            )
        }
        currentTrackedPackage = null
        closeRunnable = null
    }

    private fun cancelPendingClose() {
        closeRunnable?.let { handler.removeCallbacks(it) }
        closeRunnable = null
        CountdownForegroundService.stop(applicationContext)
    }

    override fun onInterrupt() {
        cancelPendingClose()
    }

    override fun onDestroy() {
        cancelPendingClose()
        super.onDestroy()
    }
}
