package com.example.mediqorog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mediqorog.model.CartModel
import com.example.mediqorog.model.OrderModel
import com.example.mediqorog.repository.CheckoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class CheckoutUiState(
    val isLoading: Boolean = false,
    val fullName: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val colonyLandmark: String = "",
    val cartItems: List<CartModel> = emptyList(),
    val itemsTotal: Double = 0.0,
    val deliveryFee: Double = 83.0,
    val total: Double = 0.0,
    val error: String? = null,
    val successMessage: String? = null,
    val orderPlaced: Boolean = false,
    val orderId: String = ""
)

class CheckoutViewModel(
    private val repository: CheckoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    fun loadCartItems(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repository.getCartItems(userId).fold(
                onSuccess = { items ->
                    val itemsTotal = items.sumOf { it.price * it.quantity }
                    val total = itemsTotal + _uiState.value.deliveryFee

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            cartItems = items,
                            itemsTotal = itemsTotal,
                            total = total
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load cart items"
                        )
                    }
                }
            )
        }
    }

    fun updateFullName(name: String) {
        _uiState.update { it.copy(fullName = name) }
    }

    fun updatePhoneNumber(phone: String) {
        _uiState.update { it.copy(phoneNumber = phone) }
    }

    fun updateAddress(address: String) {
        _uiState.update { it.copy(address = address) }
    }

    fun updateColonyLandmark(colony: String) {
        _uiState.update { it.copy(colonyLandmark = colony) }
    }

    fun placeOrder(userId: String) {
        val state = _uiState.value

        // Validation
        if (state.fullName.isBlank()) {
            _uiState.update { it.copy(error = "Please enter your full name") }
            return
        }

        if (state.phoneNumber.isBlank()) {
            _uiState.update { it.copy(error = "Please enter your phone number") }
            return
        }

        if (state.phoneNumber.length < 10) {
            _uiState.update { it.copy(error = "Please enter a valid phone number") }
            return
        }

        if (state.address.isBlank()) {
            _uiState.update { it.copy(error = "Please enter your address") }
            return
        }

        if (state.cartItems.isEmpty()) {
            _uiState.update { it.copy(error = "Your cart is empty") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Calculate estimated delivery date (2-3 days from now)
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, 2)
            val startDate = SimpleDateFormat("d MMM", Locale.getDefault()).format(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val endDate = SimpleDateFormat("d MMM", Locale.getDefault()).format(calendar.time)
            val estimatedDelivery = "Get by $startDate-$endDate"

            val order = OrderModel(
                userId = userId,
                fullName = state.fullName,
                phoneNumber = state.phoneNumber,
                address = state.address,
                colonyLandmark = state.colonyLandmark,
                items = state.cartItems,
                itemsTotal = state.itemsTotal,
                deliveryFee = state.deliveryFee,
                discount = 0.0,
                total = state.total,
                orderStatus = "pending",
                orderDate = System.currentTimeMillis(),
                estimatedDelivery = estimatedDelivery
            )

            repository.placeOrder(order).fold(
                onSuccess = { orderId ->
                    // Clear cart after successful order
                    repository.clearCart(userId).fold(
                        onSuccess = {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    orderPlaced = true,
                                    orderId = orderId,
                                    successMessage = "Order placed successfully!"
                                )
                            }
                        },
                        onFailure = { error ->
                            // Order placed but cart not cleared
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    orderPlaced = true,
                                    orderId = orderId,
                                    successMessage = "Order placed successfully!"
                                )
                            }
                        }
                    )
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to place order"
                        )
                    }
                }
            )
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}

class CheckoutViewModelFactory(
    private val repository: CheckoutRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CheckoutViewModel::class.java)) {
            return CheckoutViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}