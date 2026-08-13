package com.mimo.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Handles the "Snooze" action tapped from either the countdown notification
 * or the full-screen overlay warning. Delegates to whichever
 * [MimoAccessibilityService] instance is currently running.
 */
class SnoozeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_SNOOZE) return
        MimoAccessibilityService.requestSnooze()
    }

    companion object {
        const val ACTION_SNOOZE = "com.mimo.app.action.SNOOZE"
    }
}
