package com.example.smart_park.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.smart_park.repository.ParkingRepository
import java.time.Duration
import java.time.Instant

class ParkingWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val repository = ParkingRepository()

    override suspend fun doWork(): Result {
        return try {
            val activeSessions = repository.getActiveSessions()
            val now = Instant.now()

            activeSessions.forEach { session ->
                val entryTime = Instant.parse(session.entry_time)
                val duration = Duration.between(entryTime, now)

                // Si le véhicule est là depuis plus de 24h
                if (duration.toHours() >= 24) {
                    sendNotification(session.license_plate)
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun sendNotification(licensePlate: String) {
        val channelId = "parking_alerts"
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Alertes Parking", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Véhicule oublié ! ⚠️")
            .setContentText("Le véhicule $licensePlate est stationné depuis plus de 24h.")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(licensePlate.hashCode(), notification)
    }
}
