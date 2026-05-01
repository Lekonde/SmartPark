package com.example.smart_park.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smart_park.model.ParkingSession
import com.example.smart_park.repository.ParkingRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant

class ParkingViewModel(private val repository: ParkingRepository = ParkingRepository()) : ViewModel() {

    private val _sessions = MutableStateFlow<List<ParkingSession>>(emptyList())
    val sessions: StateFlow<List<ParkingSession>> = _sessions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        refreshSessions()
        startPriceTimer()
    }

    fun refreshSessions() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _sessions.value = repository.getActiveSessions()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun registerEntry(licensePlate: String, vehicleType: com.example.smart_park.model.VehicleType, photoBytes: ByteArray?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                var photoUrl: String? = null
                if (photoBytes != null) {
                    val fileName = "vehicle_${licensePlate}_${System.currentTimeMillis()}.jpg"
                    photoUrl = repository.uploadVehiclePhoto(fileName, photoBytes)
                }

                val newSession = ParkingSession(
                    license_plate = licensePlate,
                    vehicle_type = vehicleType,
                    entry_time = Instant.now().toString(),
                    photo_url = photoUrl,
                    is_active = true
                )

                repository.insertSession(newSession)
                refreshSessions()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun startPriceTimer() {
        viewModelScope.launch {
            while (true) {
                delay(10000) // 10 secondes pour que ça soit visible en démo !
                _sessions.value = _sessions.value.toList() 
            }
        }
    }

    fun checkoutVehicle(session: ParkingSession) {
        viewModelScope.launch {
            try {
                val finalAmount = session.calculateCurrentAmount()
                val updatedSession = session.copy(
                    exit_time = Instant.now().toString(),
                    total_amount = finalAmount,
                    is_active = false
                )
                repository.updateSession(updatedSession)
                refreshSessions()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun cancelSession(id: Int) {
        viewModelScope.launch {
            try {
                repository.deleteSession(id)
                refreshSessions()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
