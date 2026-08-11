package com.mimo.app

import android.app.Application
import com.mimo.app.service.NotificationHelper

class MimoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.ensureChannels(this)
    }
}
