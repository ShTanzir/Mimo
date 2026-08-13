package com.mimo.app

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.mimo.app.service.NotificationHelper
import com.mimo.app.service.SnoozeResetWorker
import java.util.concurrent.TimeUnit

class MimoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.ensureChannels(this)

        val resetWork = PeriodicWorkRequestBuilder<SnoozeResetWorker>(1, TimeUnit.DAYS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            SnoozeResetWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            resetWork
        )
    }
}
