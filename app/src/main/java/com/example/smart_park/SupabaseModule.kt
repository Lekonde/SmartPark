package com.example.smart_park

import io.github.jan_tennert.supabase.createSupabaseClient
import io.github.jan_tennert.supabase.postgrest.Postgrest
import io.github.jan_tennert.supabase.storage.Storage

object SupabaseConfig {
    const val SUPABASE_URL = "https://emzsboickvccxweofwqa.supabase.co"
    const val SUPABASE_KEY = "sb_publishable_3jWh4ii_SBRF1C3Wwajk1A_nB7A07f1"
    const val BUCKET_VEHICLES = "vehicle_photos"
}

val supabase = createSupabaseClient(
    supabaseUrl = SupabaseConfig.SUPABASE_URL,
    supabaseKey = SupabaseConfig.SUPABASE_KEY
) {
    install(Postgrest)
    install(Storage)
}
