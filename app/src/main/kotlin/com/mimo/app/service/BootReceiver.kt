package com.mimo.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.mimo.app.R
import com.mimo.app.util.PermissionUtils

/**
 * Android does not let apps auto-enable Accessibility Services, so after a
 * reboot we simply nudge the user with a notification if MIMO's guard
 * service isn't already running.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        NotificationHelper.ensureChannels(context)
        if (!PermissionUtils.isAccessibilityServiceEnabled(context)) {
            val notification = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_GENERAL)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("MIMO needs a quick re-check")
                .setContentText("Turn MIMO's Accessibility Service back on to keep guarding your apps.")
                .setAutoCancel(true)
                .build()
            val manager = context.getSystemService(android.app.NotificationManager::class.java)
            manager.notify(NotificationHelper.NOTIF_ID_GENERAL, notification)
        }
    }
}
