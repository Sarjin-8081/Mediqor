package com.example.mediqorog.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediqorog.model.CartModel
import com.example.mediqorog.repository.CartRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CartUiState(
    val cartItems: List<CartModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val cartTotal: Double = 0.0,
    val itemCount: Int = 0,
    val successMessage: String? = null
)

class CartViewModel(
    private val cartRepository: CartRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    private val TAG = "CartViewModel"

    fun loadCart(userId: String) {
        Log.d(TAG, "Loading cart for user: $userId")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                cartRepository.getCartItems(userId).collect { items ->
                    Log.d(TAG, "Cart loaded: ${items.size} items")
                    val total = items.sumOf { it.getTotal() }
                    val count = items.sumOf { it.quantity }

                    _uiState.value = _uiState.value.copy(
                        cartItems = items,
                        cartTotal = total,
                        itemCount = count,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading cart", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load cart"
                )
            }
        }
    }

    fun addToCart(
        userId: String,
        productId: String,
        productName: String,
        productImage: String,
        price: Double,
        quantity: Int,
        category: String,
        stock: Int
    ) {
        Log.d(TAG, "Adding to cart: $productName (user: $userId)")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, successMessage = null)

            val result = cartRepository.addToCart(
                userId, productId, productName, productImage,
                price, quantity, category, stock
            )

            result.fold(
                onSuccess = { cartItemId ->
                    Log.d(TAG, "Successfully added to cart. Item ID: $cartItemId")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Added to cart successfully!",
                        error = null
                    )
                    // IMPORTANT: Reload cart after adding
                    loadCart(userId)
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to add to cart", error)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to add to cart",
                        successMessage = null
                    )
                }
            )
        }
    }

    fun updateQuantity(cartItemId: String, newQuantity: Int) {
        Log.d(TAG, "Updating quantity for $cartItemId to $newQuantity")
        viewModelScope.launch {
            val result = cartRepository.updateQuantity(cartItemId, newQuantity)

            result.fold(
                onSuccess = {
                    Log.d(TAG, "Quantity updated successfully")
                    // Cart will auto-update via Flow
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to update quantity", error)
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Failed to update quantity"
                    )
                }
            )
        }
    }

    fun removeFromCart(cartItemId: String) {
        Log.d(TAG, "Removing from cart: $cartItemId")
        viewModelScope.launch {
            val result = cartRepository.removeFromCart(cartItemId)

            result.fold(
                onSuccess = {
                    Log.d(TAG, "Item removed successfully")
                    _uiState.value = _uiState.value.copy(
                        successMessage = "Item removed from cart"
                    )
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to remove item", error)
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Failed to remove item"
                    )
                }
            )
        }
    }

    fun clearCart(userId: String) {
        Log.d(TAG, "Clearing cart")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = cartRepository.clearCart(userId)

            result.fold(
                onSuccess = {
                    Log.d(TAG, "Cart cleared successfully")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Cart cleared"
                    )
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to clear cart", error)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to clear cart"
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