package com.mimo.app.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mimo.app.repository.MimoRepository

/** Resets every rule's "snooze already used today" flag, roughly once a day. */
class SnoozeResetWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            MimoRepository.getInstance(applicationContext).resetDailySnoozes()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "mimo_snooze_reset"
    }
}
