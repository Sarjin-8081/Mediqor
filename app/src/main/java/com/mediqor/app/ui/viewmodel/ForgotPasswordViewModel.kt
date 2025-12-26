package com.mediqor.app.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.mediqor.app.ui.AuthState

class ForgotPasswordViewModel : ViewModel() {

    var authState = mutableStateOf<AuthState>(AuthState.Idle)
        private set

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun resetPassword(email: String) {
        authState.value = AuthState.Loading

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    authState.value = AuthState.Success("Reset link sent to $email")
                } else {
                    authState.value = AuthState.Error(task.exception?.message ?: "Failed to send reset link")
                }
            }
    }

    fun resetAuthState() {
        authState.value = AuthState.Idle
    }
}
