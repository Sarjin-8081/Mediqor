package com.example.mediqorog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediqorog.model.AnalyticsModel
import com.example.mediqorog.repoimpl.AdminRepoImpl
import com.example.mediqorog.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val isLoading: Boolean = false,
    val analytics: AnalyticsModel = AnalyticsModel(),
    val error: String? = null
)

class AdminDashboardViewModel(
    private val repository: AdminRepository = AdminRepoImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadAnalytics()
    }

    fun loadAnalytics() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getAnalytics(
                onSuccess = { analytics ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        analytics = analytics
                    )
                },
                onError = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error
                    )
                }
            )
        }
    }

    fun refresh() {
        loadAnalytics()
    }
}