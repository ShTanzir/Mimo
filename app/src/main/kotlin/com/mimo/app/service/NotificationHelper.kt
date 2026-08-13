package com.mimo.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.mimo.app.MainActivity
import com.mimo.app.R

object NotificationHelper {

    const val CHANNEL_COUNTDOWN = "mimo_countdown"
    const val CHANNEL_GENERAL = "mimo_general"
    const val NOTIF_ID_COUNTDOWN = 1001
    const val NOTIF_ID_GENERAL = 1002

    fun ensureChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java)

        val countdown = NotificationChannel(
            CHANNEL_COUNTDOWN,
            context.getString(R.string.notification_channel_countdown),
            NotificationManager.IMPORTANCE_LOW
        ).apply { setShowBadge(false) }

        val general = NotificationChannel(
            CHANNEL_GENERAL,
            context.getString(R.string.notification_channel_general),
            NotificationManager.IMPORTANCE_DEFAULT
        )

        manager.createNotificationChannel(countdown)
        manager.createNotificationChannel(general)
    }

    fun buildCountdownNotification(
        context: Context,
        appLabel: String,
        remainingText: String,
        progressPercent: Int,
        allowSnooze: Boolean = false
    ): android.app.Notification {
        val openAppIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val builder = NotificationCompat.Builder(context, CHANNEL_COUNTDOWN)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notification_countdown_title))
            .setContentText(
                context.getString(R.string.notification_countdown_text, appLabel, remainingText)
            )
            .setProgress(100, progressPercent, false)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        if (allowSnooze) {
            val snoozeIntent = Intent(SnoozeReceiver.ACTION_SNOOZE).setPackage(context.packageName)
            val snoozePendingIntent = PendingIntent.getBroadcast(
                context, 1, snoozeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.addAction(0, "Snooze +1 min", snoozePendingIntent)
        }

        return builder.build()
    }

    fun notifyClosed(context: Context, appLabel: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_GENERAL)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("MIMO closed $appLabel")
            .setContentText("Your set time was up, so MIMO stepped in.")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(NOTIF_ID_GENERAL, notification)
    }
}
