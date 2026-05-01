package com.example.smart_park.repository

import com.example.smart_park.SupabaseConfig
import com.example.smart_park.model.ParkingSession
import com.example.smart_park.supabase
import io.github.jan_tennert.supabase.postgrest.from
import io.github.jan_tennert.supabase.postgrest.query.Order
import io.github.jan_tennert.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ParkingRepository {

    // 1. READ : Liste des véhicules
    suspend fun getActiveSessions(): List<ParkingSession> = withContext(Dispatchers.IO) {
        supabase.from("parking_sessions")
            .select {
                filter { eq("is_active", true) }
                order("entry_time", order = Order.DESCENDING)
            }
            .decodeList<ParkingSession>()
    }

    // 2. UPLOAD : Envoyer l'image vers Supabase Storage
    suspend fun uploadVehiclePhoto(fileName: String, byteArray: ByteArray): String = withContext(Dispatchers.IO) {
        val bucket = supabase.storage.from(SupabaseConfig.BUCKET_VEHICLES)
        bucket.upload(fileName, byteArray, upsert = true)
        return@withContext bucket.publicUrl(fileName)
    }

    // 3. CREATE : Enregistrer l'entrée
    suspend fun insertSession(session: ParkingSession) = withContext(Dispatchers.IO) {
        supabase.from("parking_sessions").insert(session)
    }

    // 4. UPDATE : Sortie du véhicule
    suspend fun updateSession(session: ParkingSession) = withContext(Dispatchers.IO) {
        session.id?.let { id ->
            supabase.from("parking_sessions").update(session) {
                filter { eq("id", id) }
            }
        }
    }
}
