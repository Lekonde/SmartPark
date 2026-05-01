package com.example.smart_park.model

import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.Duration

@Serializable
enum class VehicleType(val hourlyRate: Double) {
    MOTO(1.0),
    VOITURE(2.0),
    CAMION(5.0)
}

@Serializable
data class ParkingSession(
    val id: Int? = null,
    val license_plate: String,
    val vehicle_type: VehicleType,
    val photo_url: String? = null,
    val entry_time: String, // Format ISO-8601
    val exit_time: String? = null,
    val total_amount: Double? = null,
    val is_active: Boolean = true
) {
    // Logique de calcul du montant en temps réel
    fun calculateCurrentAmount(): Double {
        val start = Instant.parse(entry_time)
        val end = if (exit_time != null) Instant.parse(exit_time) else Instant.now()
        val duration = Duration.between(start, end)
        val hours = duration.toMinutes() / 60.0 // On calcule à la minute pour plus de précision
        return hours * vehicle_type.hourlyRate
    }
}
