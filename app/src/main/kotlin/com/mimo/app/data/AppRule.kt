package com.mimo.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A guarded app rule: which package is being watched, how long a user
 * is allowed to stay before MIMO closes it, and related preferences.
 */
@Entity(tableName = "app_rules")
data class AppRule(
    @PrimaryKey val packageName: String,
    val appLabel: String,
    val enabled: Boolean = true,
    val delayMillis: Long = 60_000L, // default 1 minute
    val delayPresetLabel: String = "1 minute",
    val closeMessage: String = "Time's up! MIMO is closing this app.",
    val allowSnoozeOnce: Boolean = true,
    val snoozeUsedToday: Boolean = false,
    val vibrateOnWarning: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
