package com.mimo.app.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import com.mimo.app.data.AppRule
import com.mimo.app.data.ClosureLog
import com.mimo.app.repository.MimoRepository
import com.mimo.app.util.AppInfoProvider
import com.mimo.app.util.FocusSessionManager
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
    private var currentRule: AppRule? = null
    private var isFocusSessionClose: Boolean = false
    private var closeRunnable: Runnable? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        repository = MimoRepository.getInstance(applicationContext)
        NotificationHelper.ensureChannels(applicationContext)
        instance = this

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

        // Focus Session ("Pomodoro mode"): every app is treated as guarded
        // with an immediate close, except a small exempt set.
        if (FocusSessionManager.isActive(applicationContext)) {
            val exempt = FocusSessionManager.exemptPackages(applicationContext)
            if (packageName !in exempt) {
                isFocusSessionClose = true
                val label = AppInfoProvider.getAppLabel(applicationContext, packageName)
                performClose(packageName, label, 0L)
                return
            }
        }
        isFocusSessionClose = false

        serviceScope.launch {
            val rule = repository.getRule(packageName)
            if (rule == null || !rule.enabled) return@launch
            currentRule = rule
            scheduleClose(packageName, rule)
        }
    }

    private fun scheduleClose(packageName: String, rule: AppRule) {
        if (rule.delayMillis <= 0) {
            performClose(packageName, rule.appLabel, rule.delayMillis)
            return
        }

        CountdownForegroundService.start(
            context = applicationContext,
            appLabel = rule.appLabel,
            totalMillis = rule.delayMillis,
            closeMessage = rule.closeMessage,
            allowSnooze = rule.allowSnoozeOnce && !rule.snoozeUsedToday,
            vibrateOnWarning = rule.vibrateOnWarning
        )

        val runnable = Runnable {
            if (currentTrackedPackage == packageName) {
                performClose(packageName, rule.appLabel, rule.delayMillis)
            }
        }
        closeRunnable = runnable
        handler.postDelayed(runnable, rule.delayMillis)
    }

    /** Called when the person taps "Snooze +1 min" from the notification or the overlay. */
    fun performSnooze() {
        val packageName = currentTrackedPackage ?: return
        val rule = currentRule ?: return
        if (!rule.allowSnoozeOnce || rule.snoozeUsedToday) return

        closeRunnable?.let { handler.removeCallbacks(it) }
        closeRunnable = null
        OverlayWarningManager.hide(applicationContext)

        serviceScope.launch { repository.markSnoozeUsed(packageName) }
        val updatedRule = rule.copy(snoozeUsedToday = true)
        currentRule = updatedRule

        CountdownForegroundService.start(
            context = applicationContext,
            appLabel = updatedRule.appLabel,
            totalMillis = SNOOZE_DURATION_MILLIS,
            closeMessage = updatedRule.closeMessage,
            allowSnooze = false,
            vibrateOnWarning = updatedRule.vibrateOnWarning
        )
        val runnable = Runnable {
            if (currentTrackedPackage == packageName) {
                performClose(packageName, updatedRule.appLabel, updatedRule.delayMillis + SNOOZE_DURATION_MILLIS)
            }
        }
        closeRunnable = runnable
        handler.postDelayed(runnable, SNOOZE_DURATION_MILLIS)
    }

    private fun performClose(packageName: String, appLabel: String, allowedDuration: Long) {
        performGlobalAction(GLOBAL_ACTION_HOME)
        CountdownForegroundService.stop(applicationContext)
        OverlayWarningManager.hide(applicationContext)
        NotificationHelper.notifyClosed(applicationContext, appLabel)

        serviceScope.launch {
            repository.logClosure(
                ClosureLog(
                    packageName = packageName,
                    appLabel = if (isFocusSessionClose) "$appLabel (Focus Session)" else appLabel,
                    allowedDurationMillis = allowedDuration
                )
            )
        }
        currentTrackedPackage = null
        currentRule = null
        closeRunnable = null
    }

    private fun cancelPendingClose() {
        closeRunnable?.let { handler.removeCallbacks(it) }
        closeRunnable = null
        currentRule = null
        CountdownForegroundService.stop(applicationContext)
    }

    override fun onInterrupt() {
        cancelPendingClose()
    }

    override fun onDestroy() {
        cancelPendingClose()
        if (instance == this) instance = null
        super.onDestroy()
    }

    companion object {
        private var instance: MimoAccessibilityService? = null
        private const val SNOOZE_DURATION_MILLIS = 60_000L

        fun requestSnooze() {
            instance?.performSnooze()
        }
    }
}
