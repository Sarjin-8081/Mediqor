package com.example.mediqorog.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediqorog.model.Order
import com.example.mediqorog.model.OrderStatus
import com.example.mediqorog.repository.OrderRepository
import com.example.mediqorog.repository.OrderRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI State for User Orders Screen
 */
data class UserOrdersUiState(
    val orders: List<Order> = emptyList(),
    val filteredOrders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedTab: Int = 0,
    val searchQuery: String = ""
)

/**
 * ViewModel for managing user's orders
 */
class OrderViewModel(
    private val repository: OrderRepository = OrderRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserOrdersUiState())
    val uiState: StateFlow<UserOrdersUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "OrderViewModel"
    }

    init {
        Log.d(TAG, "OrderViewModel initialized")
        loadOrders()
    }

    /**
     * Load all orders from Firebase
     */
    fun loadOrders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // Call getUserOrders() - no userId parameter needed
                // The repository gets userId from FirebaseAuth internally
                val result = repository.getUserOrders()

                // Handle Result
                if (result.isSuccess) {
                    val orders = result.getOrNull() ?: emptyList()
                    Log.d(TAG, "Successfully loaded ${orders.size} orders")

                    orders.forEach { order ->
                        Log.d(TAG, "Order: ${order.orderNumber}, Status: ${order.status}")
                    }

                    _uiState.update { state ->
                        state.copy(
                            orders = orders,
                            filteredOrders = filterOrders(
                                orders,
                                state.selectedTab,
                                state.searchQuery
                            ),
                            isLoading = false,
                            error = null
                        )
                    }
                } else {
                    val error = result.exceptionOrNull()
                    Log.e(TAG, "Failed to load orders: ${error?.message}", error)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error?.message ?: "Failed to load orders"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading orders: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "An error occurred while loading orders"
                    )
                }
            }
        }
    }

    /**
     * Refresh orders
     */
    fun refreshOrders() {
        Log.d(TAG, "Refreshing orders")
        loadOrders()
    }

    /**
     * Change selected tab and filter orders
     */
    fun selectTab(tabIndex: Int) {
        Log.d(TAG, "Selected tab: $tabIndex")
        _uiState.update { state ->
            state.copy(
                selectedTab = tabIndex,
                filteredOrders = filterOrders(
                    state.orders,
                    tabIndex,
                    state.searchQuery
                )
            )
        }
    }

    /**
     * Update search query and filter orders
     */
    fun updateSearchQuery(query: String) {
        Log.d(TAG, "Search query: $query")
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredOrders = filterOrders(
                    state.orders,
                    state.selectedTab,
                    query
                )
            )
        }
    }

    /**
     * Clear search
     */
    fun clearSearch() {
        Log.d(TAG, "Clearing search")
        updateSearchQuery("")
    }

    /**
     * Cancel an order
     */
    fun cancelOrder(orderId: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            Log.d(TAG, "Cancelling order: $orderId")

            try {
                val result = repository.cancelOrder(orderId)

                if (result.isSuccess) {
                    Log.d(TAG, "Order cancelled successfully")
                    loadOrders() // Refresh list
                    onResult(true, "Order cancelled successfully")
                } else {
                    val error = result.exceptionOrNull()
                    Log.e(TAG, "Failed to cancel order: ${error?.message}", error)
                    onResult(false, error?.message ?: "Failed to cancel order")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception cancelling order: ${e.message}", e)
                onResult(false, e.message ?: "An error occurred")
            }
        }
    }

    /**
     * Reorder (create new order from existing one)
     */
    fun reorder(order: Order, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            Log.d(TAG, "Reordering: ${order.orderNumber}")

            try {
                val newOrder = order.copy(
                    id = "",
                    orderNumber = generateOrderNumber(),
                    date = java.util.Date(),
                    timestamp = com.google.firebase.Timestamp.now(),
                    status = OrderStatus.PENDING,
                    isPaid = false
                )

                val result = repository.createOrder(newOrder)

                if (result.isSuccess) {
                    val orderId = result.getOrNull()
                    Log.d(TAG, "Reorder successful: $orderId")
                    loadOrders() // Refresh list
                    onResult(true, "Order placed successfully!")
                } else {
                    val error = result.exceptionOrNull()
                    Log.e(TAG, "Failed to reorder: ${error?.message}", error)
                    onResult(false, error?.message ?: "Failed to place order")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception reordering: ${e.message}", e)
                onResult(false, e.message ?: "An error occurred")
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Filter orders based on tab and search query
     */
    private fun filterOrders(
        orders: List<Order>,
        tabIndex: Int,
        query: String
    ): List<Order> {
        // Filter by tab
        val tabFiltered = when (tabIndex) {
            0 -> orders // All
            1 -> orders.filter { // Pending
                it.status == OrderStatus.PENDING ||
                        it.status == OrderStatus.PROCESSING ||
                        it.status == OrderStatus.SHIPPED
            }
            2 -> orders.filter { it.status == OrderStatus.DELIVERED } // Completed
            3 -> orders.filter { it.status == OrderStatus.CANCELLED } // Cancelled
            else -> orders
        }

        // Filter by search query
        val filtered = if (query.isBlank()) {
            tabFiltered
        } else {
            tabFiltered.filter { order ->
                order.orderNumber.contains(query, ignoreCase = true) ||
                        order.items.any {
                            it.name.contains(query, ignoreCase = true) ||
                                    it.productName.contains(query, ignoreCase = true)
                        }
            }
        }

        Log.d(TAG, "Filtered ${filtered.size} orders (tab: $tabIndex, query: '$query')")
        return filtered
    }

    /**
     * Generate unique order number
     */
    private fun generateOrderNumber(): String {
        val timestamp = System.currentTimeMillis()
        val random = (1000..9999).random()
        return "ORD-$timestamp-$random"
    }
}