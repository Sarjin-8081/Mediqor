package com.example.mediqorog.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mediqorog.model.Prescription
import com.example.mediqorog.repository.PrescriptionRepository
import com.example.mediqorog.repository.PrescriptionRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PrescriptionUiState(
    val prescriptions: List<Prescription> = emptyList(),
    val isLoading: Boolean = false,
    val isUploading: Boolean = false,
    val error: String? = null,
    val uploadSuccess: Boolean = false
)

class PrescriptionViewModel(
    private val repository: PrescriptionRepository = PrescriptionRepositoryImpl()
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(PrescriptionUiState())
    val uiState: StateFlow<PrescriptionUiState> = _uiState.asStateFlow()

    init {
        loadPrescriptions()
    }

    fun loadPrescriptions() {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getUserPrescriptions(userId).fold(
                onSuccess = { prescriptions ->
                    _uiState.value = _uiState.value.copy(
                        prescriptions = prescriptions,
                        isLoading = false
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Failed to load prescriptions",
                        isLoading = false
                    )
                }
            )
        }
    }

    fun uploadPrescription(
        patientName: String,
        imageUri: Uri,
        notes: String
    ) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _uiState.value = _uiState.value.copy(error = "Please login to upload prescriptions")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUploading = true, error = null, uploadSuccess = false)

            repository.uploadPrescription(userId, patientName, imageUri, notes).fold(
                onSuccess = { prescriptionId ->
                    _uiState.value = _uiState.value.copy(
                        isUploading = false,
                        uploadSuccess = true
                    )
                    // Reload prescriptions to show the new one
                    loadPrescriptions()
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Failed to upload prescription",
                        isUploading = false,
                        uploadSuccess = false
                    )
                }
            )
        }
    }

    fun deletePrescription(prescriptionId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.deletePrescription(prescriptionId).fold(
                onSuccess = {
                    loadPrescriptions()
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Failed to delete prescription",
                        isLoading = false
                    )
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearUploadSuccess() {
        _uiState.value = _uiState.value.copy(uploadSuccess = false)
    }
}

class PrescriptionViewModelFactory(
    private val repository: PrescriptionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PrescriptionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PrescriptionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}