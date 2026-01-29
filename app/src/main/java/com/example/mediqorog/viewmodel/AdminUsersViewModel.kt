package com.example.mediqorog.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediqorog.model.User
import com.example.mediqorog.repository.AdminUserRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AdminUsersUiState(
    val users: List<User> = emptyList(),
    val filteredUsers: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val searchQuery: String = "",
    val selectedRole: String = "All"
)

class AdminUsersViewModel : ViewModel() {
    private val repository = AdminUserRepositoryImpl()
    private val db = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(AdminUsersUiState())
    val uiState: StateFlow<AdminUsersUiState> = _uiState.asStateFlow()

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            Log.d("AdminUsersVM", "Loading users...")

            repository.getAllUsers().fold(
                onSuccess = { users ->
                    Log.d("AdminUsersVM", "Loaded ${users.size} users from Firebase")

                    // ✅ Now using full User model directly - no data loss!
                    users.forEach {
                        Log.d("AdminUsersVM", "User: ${it.displayName} (${it.email}) - Status: ${it.accountStatus}")
                    }

                    _uiState.value = _uiState.value.copy(
                        users = users,
                        filteredUsers = users,
                        isLoading = false,
                        error = null
                    )
                },
                onFailure = { error ->
                    Log.e("AdminUsersVM", "Error loading users: ${error.message}", error)
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Failed to load users",
                        isLoading = false,
                        users = emptyList(),
                        filteredUsers = emptyList()
                    )
                }
            )
        }
    }

    fun searchUsers(query: String) {
        Log.d("AdminUsersVM", "Search query: '$query'")
        _uiState.value = _uiState.value.copy(searchQuery = query)
        filterUsers()
    }

    fun filterByRole(role: String) {
        Log.d("AdminUsersVM", "Filter by role: $role")
        _uiState.value = _uiState.value.copy(selectedRole = role)
        filterUsers()
    }

    private fun filterUsers() {
        val query = _uiState.value.searchQuery.lowercase().trim()
        val role = _uiState.value.selectedRole

        Log.d("AdminUsersVM", "Filtering - Query: '$query', Role: $role")
        Log.d("AdminUsersVM", "Total users to filter: ${_uiState.value.users.size}")

        val filtered = _uiState.value.users.filter { user ->
            // Match search query
            val matchesSearch = if (query.isEmpty()) {
                true
            } else {
                user.displayName.lowercase().contains(query) ||
                        user.email.lowercase().contains(query) ||
                        user.phoneNumber.contains(query)
            }

            // Match role filter
            val matchesRole = role == "All" || user.role == role.lowercase()

            val matches = matchesSearch && matchesRole

            if (matches) {
                Log.d("AdminUsersVM", "Match: ${user.displayName} - ${user.email}")
            }

            matches
        }

        Log.d("AdminUsersVM", "Filtered results: ${filtered.size} users")

        _uiState.value = _uiState.value.copy(filteredUsers = filtered)
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            Log.d("AdminUsersVM", "Deleting user: $userId")

            repository.deleteUser(userId).fold(
                onSuccess = {
                    Log.d("AdminUsersVM", "User deleted successfully")
                    _uiState.value = _uiState.value.copy(
                        successMessage = "User deleted successfully"
                    )
                    loadUsers()
                },
                onFailure = { error ->
                    Log.e("AdminUsersVM", "Error deleting user: ${error.message}", error)
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Failed to delete user"
                    )
                }
            )
        }
    }

    // ✅ NEW: Suspend user account
    fun suspendUser(userId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                Log.d("AdminUsersVM", "Suspending user: $userId")

                db.collection("users")
                    .document(userId)
                    .update("accountStatus", "suspended")
                    .await()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "User suspended successfully"
                )

                // Refresh the users list
                loadUsers()

            } catch (e: Exception) {
                Log.e("AdminUsersVM", "Error suspending user: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to suspend user: ${e.message}"
                )
            }
        }
    }

    // ✅ NEW: Reactivate suspended user
    fun reactivateUser(userId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                Log.d("AdminUsersVM", "Reactivating user: $userId")

                db.collection("users")
                    .document(userId)
                    .update("accountStatus", "active")
                    .await()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "User reactivated successfully"
                )

                // Refresh the users list
                loadUsers()

            } catch (e: Exception) {
                Log.e("AdminUsersVM", "Error reactivating user: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to reactivate user: ${e.message}"
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            error = null,
            successMessage = null
        )
    }

    // For debugging - call this to see what's in the database
    fun debugPrintUsers() {
        viewModelScope.launch {
            Log.d("AdminUsersVM", "=== DEBUG: Fetching users directly ===")
            repository.getAllUsers().fold(
                onSuccess = { users ->
                    Log.d("AdminUsersVM", "Total users in database: ${users.size}")
                    users.forEach { user ->
                        Log.d("AdminUsersVM", "User: ${user.displayName} | ${user.email} | ${user.role} | Status: ${user.accountStatus}")
                    }
                },
                onFailure = { error ->
                    Log.e("AdminUsersVM", "Debug fetch failed: ${error.message}")
                }
            )
        }
    }
}