package com.mimo.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/** A single record of MIMO auto-closing a guarded app. Powers the stats screen. */
@Entity(tableName = "closure_logs")
data class ClosureLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,
    val appLabel: String,
    val timestamp: Long = System.currentTimeMillis(),
    val allowedDurationMillis: Long,
    val wasSnoozed: Boolean = false
)
