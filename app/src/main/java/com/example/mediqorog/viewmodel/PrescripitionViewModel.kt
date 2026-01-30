package com.example.mediqorog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediqorog.model.PrescriptionData
import com.example.mediqorog.repository.PrescriptionRepo
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PrescriptionState(
    val prescriptions: List<PrescriptionData> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class PrescriptionViewModelSimple : ViewModel() {
    private val repository = PrescriptionRepo()
    private val auth = FirebaseAuth.getInstance()

    private val _state = MutableStateFlow(PrescriptionState())
    val state: StateFlow<PrescriptionState> = _state.asStateFlow()

    init {
        loadPrescriptions()
    }

    fun loadPrescriptions() {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            repository.getUserPrescriptions(userId)
                .onSuccess { prescriptions ->
                    _state.value = _state.value.copy(
                        prescriptions = prescriptions,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        error = error.message,
                        isLoading = false
                    )
                }
        }
    }

    fun addPrescription(prescription: PrescriptionData) {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val prescriptionWithUser = prescription.copy(userId = userId)

            repository.addPrescription(prescriptionWithUser)
                .onSuccess {
                    loadPrescriptions()
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        error = error.message,
                        isLoading = false
                    )
                }
        }
    }

    fun deletePrescription(prescriptionId: String) {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            repository.deletePrescription(prescriptionId, userId)
                .onSuccess {
                    loadPrescriptions()
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(error = error.message)
                }
        }
    }

    fun updateQuantity(prescriptionId: String, newQuantity: Int) {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            repository.updateQuantity(prescriptionId, userId, newQuantity)
                .onSuccess {
                    loadPrescriptions()
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(error = error.message)
                }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}