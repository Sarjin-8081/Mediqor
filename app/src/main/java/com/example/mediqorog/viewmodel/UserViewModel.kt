package com.example.mediqorog.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediqorog.R
import com.example.mediqorog.model.User
import com.example.mediqorog.repository.UserRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            _currentUser.value = repository.getCurrentUser()
        }
    }

    fun signIn(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.signIn(email, password)
            result.onSuccess { user ->
                _currentUser.value = user
                onResult(true, "Login successful!")
            }
            result.onFailure { exception ->
                onResult(false, exception.message ?: "Login failed")
            }
        }
    }

    fun signUp(email: String, password: String, displayName: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.signUp(email, password, displayName)
            result.onSuccess { user ->
                _currentUser.value = user
                onResult(true, "Registration successful!")
            }
            result.onFailure { exception ->
                onResult(false, exception.message ?: "Registration failed")
            }
        }
    }

    fun signInWithGoogle(account: GoogleSignInAccount, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.signInWithGoogle(account)
            result.onSuccess { user ->
                _currentUser.value = user
                onResult(true, "Google sign-in successful!")
            }
            result.onFailure { exception ->
                onResult(false, exception.message ?: "Google sign-in failed")
            }
        }
    }

    fun signOut(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.signOut()
            result.onSuccess {
                _currentUser.value = null
                onResult(true, "Signed out successfully")
            }
            result.onFailure { exception ->
                onResult(false, exception.message ?: "Sign out failed")
            }
        }
    }

    fun resetPassword(email: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.resetPassword(email)
            result.onSuccess {
                onResult(true, "Password reset email sent!")
            }
            result.onFailure { exception ->
                onResult(false, exception.message ?: "Failed to send reset email")
            }
        }
    }

    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, gso)
    }
}