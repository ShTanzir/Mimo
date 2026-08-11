package com.mimo.app.util

import java.util.concurrent.TimeUnit

object TimeUtils {

    /** Preset delay options shown when the user sets a timer for a guarded app. */
    data class Preset(val label: String, val millis: Long)

    val presets = listOf(
        Preset("Immediate", 0L),
        Preset("10 seconds", TimeUnit.SECONDS.toMillis(10)),
        Preset("30 seconds", TimeUnit.SECONDS.toMillis(30)),
        Preset("1 minute", TimeUnit.MINUTES.toMillis(1)),
        Preset("5 minutes", TimeUnit.MINUTES.toMillis(5)),
        Preset("15 minutes", TimeUnit.MINUTES.toMillis(15)),
        Preset("Custom", -1L)
    )

    fun formatDuration(millis: Long): String {
        if (millis <= 0) return "Immediate"
        val totalSeconds = millis / 1000
        val h = totalSeconds / 3600
        val m = (totalSeconds % 3600) / 60
        val s = totalSeconds % 60
        return buildString {
            if (h > 0) append("${h}h ")
            if (m > 0) append("${m}m ")
            if (s > 0 || (h == 0L && m == 0L)) append("${s}s")
        }.trim()
    }

    fun formatShortTimer(millisRemaining: Long): String {
        val totalSeconds = (millisRemaining / 1000).coerceAtLeast(0)
        val m = totalSeconds / 60
        val s = totalSeconds % 60
        return if (m > 0) String.format("%d:%02d", m, s) else "${s}s"
    }

    fun formatClock(timestampMillis: Long): String {
        val formatter = java.text.SimpleDateFormat("MMM d, h:mm a", java.util.Locale.getDefault())
        return formatter.format(java.util.Date(timestampMillis))
    }
}
