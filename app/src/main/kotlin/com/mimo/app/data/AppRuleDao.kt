package com.mimo.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppRuleDao {

    @Query("SELECT * FROM app_rules ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<AppRule>>

    @Query("SELECT * FROM app_rules ORDER BY createdAt DESC")
    suspend fun getAll(): List<AppRule>

    @Query("SELECT * FROM app_rules WHERE enabled = 1")
    suspend fun getEnabledRules(): List<AppRule>

    @Query("SELECT * FROM app_rules WHERE packageName = :packageName LIMIT 1")
    suspend fun getRule(packageName: String): AppRule?

    @Query("SELECT * FROM app_rules WHERE packageName = :packageName LIMIT 1")
    fun observeRule(packageName: String): Flow<AppRule?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(rule: AppRule)

    @Update
    suspend fun update(rule: AppRule)

    @Delete
    suspend fun delete(rule: AppRule)

    @Query("DELETE FROM app_rules WHERE packageName = :packageName")
    suspend fun deleteByPackage(packageName: String)

    @Query("UPDATE app_rules SET enabled = :enabled WHERE packageName = :packageName")
    suspend fun setEnabled(packageName: String, enabled: Boolean)

    @Query("UPDATE app_rules SET snoozeUsedToday = :used WHERE packageName = :packageName")
    suspend fun setSnoozeUsed(packageName: String, used: Boolean)

    @Query("UPDATE app_rules SET snoozeUsedToday = 0")
    suspend fun resetAllSnoozes()

    @Query("SELECT COUNT(*) FROM app_rules WHERE enabled = 1")
    fun observeEnabledCount(): Flow<Int>
}
