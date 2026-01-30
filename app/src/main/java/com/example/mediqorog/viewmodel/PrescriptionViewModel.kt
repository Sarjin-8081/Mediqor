package com.example.mediqorog.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediqorog.model.MedicineReminder
import com.example.mediqorog.model.Prescription
import com.example.mediqorog.repository.PrescriptionRepository
import com.example.mediqorog.repository.PrescriptionRepositoryImpl
import com.example.mediqorog.utils.ReminderScheduler
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PrescriptionUiState(
    val isLoading: Boolean = false,
    val isUploading: Boolean = false,
    val uploadSuccess: Boolean = false,
    val prescriptions: List<Prescription> = emptyList(),
    val error: String? = null
)

class PrescriptionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PrescriptionRepository = PrescriptionRepositoryImpl()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(PrescriptionUiState())
    val uiState: StateFlow<PrescriptionUiState> = _uiState.asStateFlow()

    private val context = application.applicationContext

    init {
        loadPrescriptions()
    }

    fun loadPrescriptions() {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repository.getUserPrescriptions(userId)
                .onSuccess { prescriptions ->
                    _uiState.update {
                        it.copy(
                            prescriptions = prescriptions,
                            isLoading = false
                        )
                    }

                    // Reschedule all active reminders
                    ReminderScheduler.rescheduleAllReminders(context, prescriptions)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            error = error.message ?: "Failed to load prescriptions",
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun uploadPrescription(patientName: String, imageUri: Uri, notes: String) {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isUploading = true) }

            repository.uploadPrescription(userId, patientName, imageUri, notes)
                .onSuccess { prescriptionId ->
                    _uiState.update {
                        it.copy(
                            isUploading = false,
                            uploadSuccess = true
                        )
                    }
                    loadPrescriptions()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            error = error.message ?: "Failed to upload prescription",
                            isUploading = false
                        )
                    }
                }
        }
    }

    fun addReminderToPrescription(prescriptionId: String, reminder: MedicineReminder) {
        viewModelScope.launch {
            val prescriptions = _uiState.value.prescriptions.toMutableList()
            val prescriptionIndex = prescriptions.indexOfFirst { it.id == prescriptionId }

            if (prescriptionIndex != -1) {
                val prescription = prescriptions[prescriptionIndex]
                val updatedReminders = prescription.reminders + reminder
                val updatedPrescription = prescription.copy(reminders = updatedReminders)

                prescriptions[prescriptionIndex] = updatedPrescription
                _uiState.update { it.copy(prescriptions = prescriptions) }

                // Schedule the new reminder
                if (reminder.enabled) {
                    ReminderScheduler.scheduleReminder(context, reminder, prescriptionId)
                }

                // Update in Firebase
                repository.updatePrescriptionReminders(prescriptionId, updatedReminders)
            }
        }
    }

    fun deletePrescription(prescriptionId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Cancel all reminders for this prescription
            val prescription = _uiState.value.prescriptions.find { it.id == prescriptionId }
            prescription?.reminders?.let { reminders ->
                ReminderScheduler.cancelReminder(context, prescriptionId, reminders.size)
            }

            repository.deletePrescription(prescriptionId)
                .onSuccess {
                    val updatedPrescriptions = _uiState.value.prescriptions.filter { it.id != prescriptionId }
                    _uiState.update {
                        it.copy(
                            prescriptions = updatedPrescriptions,
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            error = error.message ?: "Failed to delete prescription",
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun toggleReminder(prescriptionId: String, reminderId: String) {
        viewModelScope.launch {
            val prescriptions = _uiState.value.prescriptions.toMutableList()
            val prescriptionIndex = prescriptions.indexOfFirst { it.id == prescriptionId }

            if (prescriptionIndex != -1) {
                val prescription = prescriptions[prescriptionIndex]
                val updatedReminders = prescription.reminders.map { reminder ->
                    if (reminder.medicineName == reminderId) {
                        val updatedReminder = reminder.copy(enabled = !reminder.enabled)

                        // Schedule or cancel alarm based on new state
                        if (updatedReminder.enabled) {
                            ReminderScheduler.scheduleReminder(context, updatedReminder, prescriptionId)
                        } else {
                            ReminderScheduler.cancelReminder(context, prescriptionId, prescription.reminders.size)
                        }

                        updatedReminder
                    } else {
                        reminder
                    }
                }

                prescriptions[prescriptionIndex] = prescription.copy(reminders = updatedReminders)
                _uiState.update { it.copy(prescriptions = prescriptions) }

                // Update in Firebase
                repository.updatePrescriptionReminders(prescriptionId, updatedReminders)
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearUploadSuccess() {
        _uiState.update { it.copy(uploadSuccess = false) }
    }
}