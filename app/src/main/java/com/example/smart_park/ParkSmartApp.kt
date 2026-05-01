package com.example.smart_park

import android.app.Application
import androidx.work.*
import com.example.smart_park.worker.ParkingWorker
import java.util.concurrent.TimeUnit

class ParkSmartApp : Application() {
    override fun onCreate() {
        super.onCreate()
        setupWorkManager()
    }

    private fun setupWorkManager() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<ParkingWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "ParkingAlerts",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
