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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    init {
        loadCurrentUser()
    }

    // ✅ CRITICAL: Made this function public so SettingsScreen can call it
    fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                val currentUser = repository.getCurrentUser()
                _user.value = currentUser
                Log.d("UserViewModel", "Loaded current user: ${currentUser?.email}, photoUrl: ${currentUser?.photoUrl}")
            } catch (e: Exception) {
                Log.e("UserViewModel", "Failed to load user: ${e.message}")
                _user.value = null
            }
        }
    }

    fun updateUser(updatedUser: User) {
        viewModelScope.launch {
            try {
                // Update in Firestore
                val updates = hashMapOf<String, Any>(
                    "displayName" to updatedUser.displayName,
                    "phoneNumber" to updatedUser.phoneNumber,
                    "photoUrl" to (updatedUser.photoUrl ?: ""), // ✅ Include photoUrl
                    "photoPublicId" to (updatedUser.photoPublicId ?: ""), // ✅ Include photoPublicId
                    "bloodGroup" to updatedUser.bloodGroup,
                    "dateOfBirth" to updatedUser.dateOfBirth,
                    "gender" to updatedUser.gender,
                    "address" to updatedUser.address,
                    "emergencyContact" to updatedUser.emergencyContact
                )

                com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(updatedUser.uid)
                    .update(updates)
                    .await()

                // Update local state
                _user.value = updatedUser
                Log.d("UserViewModel", "User updated successfully")
            } catch (e: Exception) {
                Log.e("UserViewModel", "Failed to update user: ${e.message}")
            }
        }
    }

    fun signUp(
        email: String,
        password: String,
        displayName: String,
        callback: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result = repository.signUp(email, password, displayName)
                if (result.isSuccess) {
                    val user = result.getOrNull()
                    _user.value = user
                    Log.d("UserViewModel", "Sign up successful for: ${user?.email}")
                    callback(true, "Account created successfully!")
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Sign up failed"
                    Log.e("UserViewModel", "Sign up failed: $error")
                    callback(false, error)
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Sign up exception: ${e.message}")
                callback(false, e.message ?: "Sign up failed")
            }
        }
    }

    fun signIn(
        email: String,
        password: String,
        callback: (Boolean, String, Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result = repository.signIn(email, password)
                if (result.isSuccess) {
                    val user = result.getOrNull()
                    _user.value = user
                    val isAdmin = user?.isAdmin() ?: false
                    Log.d("UserViewModel", "Sign in successful. IsAdmin: $isAdmin, LastLogin: ${user?.lastLoginAt}")
                    callback(true, "Welcome back!", isAdmin)
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Sign in failed"
                    Log.e("UserViewModel", "Sign in failed: $error")
                    callback(false, error, false)
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Sign in exception: ${e.message}")
                callback(false, e.message ?: "Sign in failed", false)
            }
        }
    }

    fun signInWithGoogle(
        account: GoogleSignInAccount,
        callback: (Boolean, String, Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result = repository.signInWithGoogle(account)
                if (result.isSuccess) {
                    val user = result.getOrNull()
                    _user.value = user
                    val isAdmin = user?.isAdmin() ?: false
                    Log.d("UserViewModel", "Google sign in successful. IsAdmin: $isAdmin")
                    callback(true, "Welcome!", isAdmin)
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Google sign in failed"
                    Log.e("UserViewModel", "Google sign in failed: $error")
                    callback(false, error, false)
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Google sign in exception: ${e.message}")
                callback(false, e.message ?: "Google sign in failed", false)
            }
        }
    }

    fun signOut(callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val result = repository.signOut()
                if (result.isSuccess) {
                    _user.value = null
                    callback(true, "Signed out successfully")
                } else {
                    callback(false, "Sign out failed")
                }
            } catch (e: Exception) {
                callback(false, e.message ?: "Sign out failed")
            }
        }
    }

    fun resetPassword(email: String, callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val result = repository.resetPassword(email)
                if (result.isSuccess) {
                    callback(true, "Password reset email sent")
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Failed to send reset email"
                    callback(false, error)
                }
            } catch (e: Exception) {
                callback(false, e.message ?: "Failed to send reset email")
            }
        }
    }

    fun getCurrentUser(callback: (User?) -> Unit) {
        viewModelScope.launch {
            try {
                val user = repository.getCurrentUser()
                _user.value = user
                callback(user)
            } catch (e: Exception) {
                Log.e("UserViewModel", "Get current user failed: ${e.message}")
                callback(null)
            }
        }
    }

    fun updateAllUsersWithRole(callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val result = repository.updateAllUsersWithRole()
                if (result.isSuccess) {
                    val message = result.getOrNull() ?: "Users updated"
                    Log.d("UserViewModel", message)
                    callback(true, message)
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Update failed"
                    Log.e("UserViewModel", "Update failed: $error")
                    callback(false, error)
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Update exception: ${e.message}")
                callback(false, e.message ?: "Update failed")
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