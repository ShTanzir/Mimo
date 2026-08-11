package com.mimo.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ClosureLogDao {

    @Insert
    suspend fun insert(log: ClosureLog)

    @Query("SELECT * FROM closure_logs ORDER BY timestamp DESC LIMIT 200")
    fun observeRecent(): Flow<List<ClosureLog>>

    @Query("SELECT COUNT(*) FROM closure_logs WHERE timestamp >= :sinceMillis")
    fun observeCountSince(sinceMillis: Long): Flow<Int>

    @Query("SELECT COALESCE(SUM(allowedDurationMillis), 0) FROM closure_logs WHERE timestamp >= :sinceMillis")
    fun observeTimeGuardedSince(sinceMillis: Long): Flow<Long>

    @Query("SELECT packageName, appLabel, COUNT(*) as opens FROM closure_logs GROUP BY packageName ORDER BY opens DESC LIMIT 10")
    fun observeTopApps(): Flow<List<AppOpenCount>>

    @Query("DELETE FROM closure_logs")
    suspend fun clearAll()
}

data class AppOpenCount(
    val packageName: String,
    val appLabel: String,
    val opens: Int
)
