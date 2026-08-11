package com.mimo.app.repository

import android.content.Context
import com.mimo.app.data.AppOpenCount
import com.mimo.app.data.AppRule
import com.mimo.app.data.ClosureLog
import com.mimo.app.data.MimoDatabase
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth bridging Room storage with the rest of the app.
 * Also used directly from the AccessibilityService (non-Compose context).
 */
class MimoRepository private constructor(context: Context) {

    private val db = MimoDatabase.getInstance(context)
    private val ruleDao = db.appRuleDao()
    private val logDao = db.closureLogDao()

    fun observeRules(): Flow<List<AppRule>> = ruleDao.observeAll()

    fun observeRule(packageName: String): Flow<AppRule?> = ruleDao.observeRule(packageName)

    fun observeEnabledRuleCount(): Flow<Int> = ruleDao.observeEnabledCount()

    suspend fun getRule(packageName: String): AppRule? = ruleDao.getRule(packageName)

    suspend fun getEnabledRules(): List<AppRule> = ruleDao.getEnabledRules()

    suspend fun saveRule(rule: AppRule) = ruleDao.upsert(rule)

    suspend fun deleteRule(packageName: String) = ruleDao.deleteByPackage(packageName)

    suspend fun setEnabled(packageName: String, enabled: Boolean) =
        ruleDao.setEnabled(packageName, enabled)

    suspend fun markSnoozeUsed(packageName: String) = ruleDao.setSnoozeUsed(packageName, true)

    suspend fun resetDailySnoozes() = ruleDao.resetAllSnoozes()

    suspend fun logClosure(log: ClosureLog) = logDao.insert(log)

    fun observeRecentLogs(): Flow<List<ClosureLog>> = logDao.observeRecent()

    fun observeClosuresToday(sinceMillis: Long): Flow<Int> = logDao.observeCountSince(sinceMillis)

    fun observeTimeGuardedSince(sinceMillis: Long): Flow<Long> = logDao.observeTimeGuardedSince(sinceMillis)

    fun observeTopApps(): Flow<List<AppOpenCount>> = logDao.observeTopApps()

    suspend fun clearLogs() = logDao.clearAll()

    companion object {
        @Volatile private var INSTANCE: MimoRepository? = null

        fun getInstance(context: Context): MimoRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: MimoRepository(context.applicationContext).also { INSTANCE = it }
            }
    }
}
