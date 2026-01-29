package com.example.mediqorog.viewmodel

import android.content.Context
import android.util.Log
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

    fun signIn(email: String, password: String, onResult: (Boolean, String, Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.signIn(email, password)
            result.onSuccess { user ->
                _currentUser.value = user
                val isAdmin = user.isAdmin()
                Log.d("UserViewModel", "Sign in success - User: ${user.email}, Admin: $isAdmin")
                onResult(true, "Login successful!", isAdmin)
            }
            result.onFailure { exception ->
                Log.e("UserViewModel", "Sign in failed: ${exception.message}")
                onResult(false, exception.message ?: "Login failed", false)
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

    fun signInWithGoogle(account: GoogleSignInAccount, onResult: (Boolean, String, Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                Log.d("UserViewModel", "Starting Google sign-in with account: ${account.email}")
                val result = repository.signInWithGoogle(account)
                result.onSuccess { user ->
                    _currentUser.value = user
                    val isAdmin = user.isAdmin()
                    Log.d("UserViewModel", "Google sign-in success - User: ${user.email}, Admin: $isAdmin")
                    onResult(true, "Google sign-in successful!", isAdmin)
                }
                result.onFailure { exception ->
                    Log.e("UserViewModel", "Google sign-in failed: ${exception.message}")
                    onResult(false, exception.message ?: "Google sign-in failed", false)
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Exception in signInWithGoogle: ${e.message}")
                onResult(false, "Error: ${e.message}", false)
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

    fun checkIfUserIsAdmin(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = _currentUser.value
            if (user != null) {
                val result = repository.checkIfAdmin(user.uid)
                result.onSuccess { isAdmin ->
                    onResult(isAdmin)
                }
                result.onFailure {
                    onResult(false)
                }
            } else {
                onResult(false)
            }
        }
    }

    // ✅ NEW METHOD - Updates all users with correct roles
    fun updateAllUsersWithRole(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.updateAllUsersWithRole()
            result.onSuccess { message ->
                Log.d("UserViewModel", message)
                onResult(true, message)
            }
            result.onFailure { exception ->
                Log.e("UserViewModel", "Failed: ${exception.message}")
                onResult(false, exception.message ?: "Update failed")
            }
        }
    }

    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        return try {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            Log.d("UserViewModel", "Creating GoogleSignInClient with Firebase config")
            GoogleSignIn.getClient(context, gso)
        } catch (e: Exception) {
            Log.w("UserViewModel", "Firebase config not found, using basic Google Sign-In")
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

            GoogleSignIn.getClient(context, gso)
        }
    }
}