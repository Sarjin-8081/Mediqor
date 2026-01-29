package com.example.mediqorog.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SettingsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

class AdminSettingsViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun updateProfile(name: String, email: String, onSuccess: () -> Unit) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        val user = auth.currentUser
        if (user == null) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = "User not logged in"
            )
            return
        }

        // Update display name
        val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()

        user.updateProfile(profileUpdates)
            .addOnSuccessListener {
                // Update email if changed
                if (email != user.email) {
                    user.updateEmail(email)
                        .addOnSuccessListener {
                            updateFirestoreUser(user.uid, name, email)
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                successMessage = "Profile updated successfully"
                            )
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = e.message ?: "Failed to update email"
                            )
                        }
                } else {
                    updateFirestoreUser(user.uid, name, email)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Profile updated successfully"
                    )
                    onSuccess()
                }
            }
            .addOnFailureListener { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to update profile"
                )
            }
    }

    private fun updateFirestoreUser(userId: String, name: String, email: String) {
        db.collection("users").document(userId)
            .update(
                mapOf(
                    "displayName" to name,
                    "email" to email
                )
            )
    }

    fun changePassword(
        currentPassword: String,
        newPassword: String,
        onSuccess: () -> Unit
    ) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        val user = auth.currentUser
        if (user == null || user.email == null) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = "User not logged in"
            )
            return
        }

        // Re-authenticate user
        val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

        user.reauthenticate(credential)
            .addOnSuccessListener {
                // Update password
                user.updatePassword(newPassword)
                    .addOnSuccessListener {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            successMessage = "Password changed successfully"
                        )
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = e.message ?: "Failed to change password"
                        )
                    }
            }
            .addOnFailureListener {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Current password is incorrect"
                )
            }
    }

    fun logout(onSuccess: () -> Unit) {
        auth.signOut()
        onSuccess()
    }

    fun deleteAccount(password: String, onSuccess: () -> Unit) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        val user = auth.currentUser
        if (user == null || user.email == null) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = "User not logged in"
            )
            return
        }

        // Re-authenticate user
        val credential = EmailAuthProvider.getCredential(user.email!!, password)

        user.reauthenticate(credential)
            .addOnSuccessListener {
                // Delete from Firestore
                db.collection("users").document(user.uid).delete()
                    .addOnSuccessListener {
                        // Delete authentication account
                        user.delete()
                            .addOnSuccessListener {
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    successMessage = "Account deleted successfully"
                                )
                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    error = e.message ?: "Failed to delete account"
                                )
                            }
                    }
                    .addOnFailureListener { e ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = e.message ?: "Failed to delete user data"
                        )
                    }
            }
            .addOnFailureListener {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Password is incorrect"
                )
            }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            error = null,
            successMessage = null
        )
    }
}