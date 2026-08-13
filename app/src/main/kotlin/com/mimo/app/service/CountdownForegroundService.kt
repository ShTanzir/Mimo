package com.mimo.app.service

import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import com.mimo.app.util.HapticUtils
import com.mimo.app.util.TimeUtils

/**
 * Foreground service that owns the live countdown notification (and, in the
 * final seconds, the full-screen overlay warning + haptic pulses) for
 * whichever guarded app is currently in the foreground. The actual "close"
 * action is performed by [MimoAccessibilityService], which starts/stops
 * this service.
 */
class CountdownForegroundService : Service() {

    private var timer: CountDownTimer? = null
    private var warningWindowMillis: Long = 0L
    private var overlayShown = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val appLabel = intent?.getStringExtra(EXTRA_APP_LABEL) ?: "App"
        val closeMessage = intent?.getStringExtra(EXTRA_CLOSE_MESSAGE) ?: "Time's up!"
        val totalMillis = intent?.getLongExtra(EXTRA_TOTAL_MILLIS, 0L) ?: 0L
        val allowSnooze = intent?.getBooleanExtra(EXTRA_ALLOW_SNOOZE, false) ?: false
        val vibrateOnWarning = intent?.getBooleanExtra(EXTRA_VIBRATE, true) ?: true

        // Full-screen warning appears for the last 5s, or the last 40% of a
        // very short delay — whichever is smaller.
        warningWindowMillis = minOf(5000L, (totalMillis * 0.4).toLong()).coerceAtLeast(0L)
        overlayShown = false

        NotificationHelper.ensureChannels(this)
        startForeground(
            NotificationHelper.NOTIF_ID_COUNTDOWN,
            NotificationHelper.buildCountdownNotification(
                this, appLabel, TimeUtils.formatShortTimer(totalMillis), 100, allowSnooze
            )
        )

        timer?.cancel()
        OverlayWarningManager.hide(this)

        if (totalMillis <= 0) {
            stopSelf()
            return START_NOT_STICKY
        }

        timer = object : CountDownTimer(totalMillis, 250L) {
            override fun onTick(millisUntilFinished: Long) {
                val percent = ((millisUntilFinished * 100) / totalMillis).toInt()
                val manager = getSystemService(android.app.NotificationManager::class.java)
                manager.notify(
                    NotificationHelper.NOTIF_ID_COUNTDOWN,
                    NotificationHelper.buildCountdownNotification(
                        this@CountdownForegroundService, appLabel,
                        TimeUtils.formatShortTimer(millisUntilFinished), percent, allowSnooze
                    )
                )

                if (warningWindowMillis > 0 && millisUntilFinished <= warningWindowMillis) {
                    OverlayWarningManager.show(
                        context = this@CountdownForegroundService,
                        appLabel = appLabel,
                        closeMessage = closeMessage,
                        remainingMillis = millisUntilFinished,
                        allowSnooze = allowSnooze,
                        onSnoozeClick = {
                            sendBroadcast(
                                Intent(SnoozeReceiver.ACTION_SNOOZE).setPackage(packageName)
                            )
                        }
                    )
                    if (vibrateOnWarning) {
                        HapticUtils.progressivePulse(
                            this@CountdownForegroundService, millisUntilFinished, warningWindowMillis
                        )
                    }
                }
            }

            override fun onFinish() {
                OverlayWarningManager.hide(this@CountdownForegroundService)
                stopSelf()
            }
        }.start()

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        timer?.cancel()
        OverlayWarningManager.hide(this)
        super.onDestroy()
    }

    companion object {
        const val EXTRA_APP_LABEL = "extra_app_label"
        const val EXTRA_TOTAL_MILLIS = "extra_total_millis"
        const val EXTRA_CLOSE_MESSAGE = "extra_close_message"
        const val EXTRA_ALLOW_SNOOZE = "extra_allow_snooze"
        const val EXTRA_VIBRATE = "extra_vibrate"

        fun start(
            context: android.content.Context,
            appLabel: String,
            totalMillis: Long,
            closeMessage: String = "Time's up!",
            allowSnooze: Boolean = false,
            vibrateOnWarning: Boolean = true
        ) {
            val intent = Intent(context, CountdownForegroundService::class.java)
                .putExtra(EXTRA_APP_LABEL, appLabel)
                .putExtra(EXTRA_TOTAL_MILLIS, totalMillis)
                .putExtra(EXTRA_CLOSE_MESSAGE, closeMessage)
                .putExtra(EXTRA_ALLOW_SNOOZE, allowSnooze)
                .putExtra(EXTRA_VIBRATE, vibrateOnWarning)
            context.startForegroundService(intent)
        }

        fun stop(context: android.content.Context) {
            OverlayWarningManager.hide(context)
            context.stopService(Intent(context, CountdownForegroundService::class.java))
        }
    }
}
