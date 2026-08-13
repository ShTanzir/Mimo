package com.mimo.app.util

import android.content.Context
import android.telecom.TelecomManager
import android.provider.Telephony

/**
 * Focus Session ("Pomodoro mode"): while active, MIMO closes *any* app the
 * person opens — not just apps with a saved rule — except for a small
 * exempt set (phone dialer, default SMS app, MIMO itself). Backed by plain
 * SharedPreferences (not DataStore) so [MimoAccessibilityService] can read
 * it synchronously on every window-state event without needing coroutines
 * on the hot path.
 */
object FocusSessionManager {

    private const val PREFS_NAME = "mimo_focus"
    private const val KEY_END_TIME = "end_time"
    private const val KEY_DURATION = "duration_millis"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun start(context: Context, durationMillis: Long) {
        val endTime = System.currentTimeMillis() + durationMillis
        prefs(context).edit()
            .putLong(KEY_END_TIME, endTime)
            .putLong(KEY_DURATION, durationMillis)
            .apply()
    }

    fun stop(context: Context) {
        prefs(context).edit().putLong(KEY_END_TIME, 0L).apply()
    }

    fun isActive(context: Context): Boolean {
        val end = prefs(context).getLong(KEY_END_TIME, 0L)
        return end > System.currentTimeMillis()
    }

    fun endTime(context: Context): Long = prefs(context).getLong(KEY_END_TIME, 0L)

    fun remainingMillis(context: Context): Long =
        (endTime(context) - System.currentTimeMillis()).coerceAtLeast(0L)

    fun totalDurationMillis(context: Context): Long = prefs(context).getLong(KEY_DURATION, 0L)

    /** Packages MIMO never closes during a focus session, even though everything else is blocked. */
    fun exemptPackages(context: Context): Set<String> {
        val result = mutableSetOf(context.packageName)
        try {
            val telecom = context.getSystemService(Context.TELECOM_SERVICE) as? TelecomManager
            telecom?.defaultDialerPackage?.let { result.add(it) }
        } catch (e: Exception) { /* ignore */ }
        try {
            Telephony.Sms.getDefaultSmsPackage(context)?.let { result.add(it) }
        } catch (e: Exception) { /* ignore */ }
        return result
    }
}
