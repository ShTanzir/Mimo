package com.mimo.app.service

import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import com.mimo.app.util.TimeUtils

/**
 * Foreground service that owns the live countdown notification for whichever
 * guarded app is currently in the foreground. The actual "close" action is
 * performed by [MimoAccessibilityService], which starts/stops this service.
 */
class CountdownForegroundService : Service() {

    private var timer: CountDownTimer? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val appLabel = intent?.getStringExtra(EXTRA_APP_LABEL) ?: "App"
        val totalMillis = intent?.getLongExtra(EXTRA_TOTAL_MILLIS, 0L) ?: 0L

        NotificationHelper.ensureChannels(this)
        startForeground(
            NotificationHelper.NOTIF_ID_COUNTDOWN,
            NotificationHelper.buildCountdownNotification(this, appLabel, TimeUtils.formatShortTimer(totalMillis), 100)
        )

        timer?.cancel()
        if (totalMillis <= 0) {
            stopSelf()
            return START_NOT_STICKY
        }

        timer = object : CountDownTimer(totalMillis, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val percent = ((millisUntilFinished * 100) / totalMillis).toInt()
                val notification = NotificationHelper.buildCountdownNotification(
                    this@CountdownForegroundService,
                    appLabel,
                    TimeUtils.formatShortTimer(millisUntilFinished),
                    percent
                )
                val manager = getSystemService(android.app.NotificationManager::class.java)
                manager.notify(NotificationHelper.NOTIF_ID_COUNTDOWN, notification)
            }

            override fun onFinish() {
                stopSelf()
            }
        }.start()

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        timer?.cancel()
        super.onDestroy()
    }

    companion object {
        const val EXTRA_APP_LABEL = "extra_app_label"
        const val EXTRA_TOTAL_MILLIS = "extra_total_millis"

        fun start(context: android.content.Context, appLabel: String, totalMillis: Long) {
            val intent = Intent(context, CountdownForegroundService::class.java)
                .putExtra(EXTRA_APP_LABEL, appLabel)
                .putExtra(EXTRA_TOTAL_MILLIS, totalMillis)
            context.startForegroundService(intent)
        }

        fun stop(context: android.content.Context) {
            context.stopService(Intent(context, CountdownForegroundService::class.java))
        }
    }
}
