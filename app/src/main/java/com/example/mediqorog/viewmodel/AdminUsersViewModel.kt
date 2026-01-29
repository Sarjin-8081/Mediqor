package com.example.mediqorog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediqorog.repository.AdminUserRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ✅ UserModel with photoUrl included
data class UserModel(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val phoneNumber: String = "",
    val photoUrl: String = "",
    val role: String = "customer",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun isAdmin(): Boolean = role == "admin"
}

data class AdminUsersUiState(
    val users: List<UserModel> = emptyList(),
    val filteredUsers: List<UserModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val searchQuery: String = "",
    val selectedRole: String = "All"
)

class AdminUsersViewModel : ViewModel() {
    // ✅ FIXED: Use AdminUserRepository instead of UserRepository
    private val repository = AdminUserRepositoryImpl()

    private val _uiState = MutableStateFlow(AdminUsersUiState())
    val uiState: StateFlow<AdminUsersUiState> = _uiState.asStateFlow()

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            repository.getAllUsers().fold(
                onSuccess = { users ->
                    val userModels = users.map { user ->
                        UserModel(
                            uid = user.uid,
                            email = user.email,
                            displayName = user.displayName,
                            phoneNumber = user.phoneNumber,
                            photoUrl = user.photoUrl,
                            role = user.role,
                            createdAt = user.createdAt
                        )
                    }
                    _uiState.value = _uiState.value.copy(
                        users = userModels,
                        filteredUsers = userModels,
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message,
                        isLoading = false
                    )
                }
            )
        }
    }

    fun searchUsers(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        filterUsers()
    }

    fun filterByRole(role: String) {
        _uiState.value = _uiState.value.copy(selectedRole = role)
        filterUsers()
    }

    private fun filterUsers() {
        val query = _uiState.value.searchQuery.lowercase()
        val role = _uiState.value.selectedRole

        val filtered = _uiState.value.users.filter { user ->
            val matchesSearch = user.displayName.lowercase().contains(query) ||
                    user.email.lowercase().contains(query)
            val matchesRole = role == "All" || user.role == role

            matchesSearch && matchesRole
        }

        _uiState.value = _uiState.value.copy(filteredUsers = filtered)
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            repository.deleteUser(userId).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        successMessage = "User deleted successfully"
                    )
                    loadUsers()
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message
                    )
                }
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