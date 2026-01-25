//package com.example.mediqorog.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.mediqorog.model.Order
//import com.example.mediqorog.model.OrderStatus
//import com.example.mediqorog.repository.OrderRepository
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch
//
///**
// * UI State for Orders Screen
// */
//data class OrdersUiState(
//    val orders: List<Order> = emptyList(),
//    val filteredOrders: List<Order> = emptyList(),
//    val isLoading: Boolean = false,
//    val error: String? = null,
//    val selectedTab: Int = 0,
//    val searchQuery: String = ""
//)
//
///**
// * ViewModel for managing orders
// */
//class OrderViewModel(
//    private val repository: OrderRepository = OrderRepository()
//) : ViewModel() {
//
//    private val _uiState = MutableStateFlow(OrdersUiState())
//    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()
//
//    init {
//        loadOrders()
//    }
//
//    /**
//     * Load all orders from Firebase
//     */
//    fun loadOrders() {
//        viewModelScope.launch {
//            _uiState.update { it.copy(isLoading = true, error = null) }
//
//            repository.getUserOrders().fold(
//                onSuccess = { orders ->
//                    _uiState.update { state ->
//                        state.copy(
//                            orders = orders,
//                            filteredOrders = filterOrders(orders, state.selectedTab, state.searchQuery),
//                            isLoading = false
//                        )
//                    }
//                },
//                onFailure = { error ->
//                    _uiState.update {
//                        it.copy(
//                            isLoading = false,
//                            error = error.message ?: "Failed to load orders"
//                        )
//                    }
//                }
//            )
//        }
//    }
//
//    /**
//     * Refresh orders
//     */
//    fun refreshOrders() {
//        loadOrders()
//    }
//
//    /**
//     * Change selected tab and filter orders
//     */
//    fun selectTab(tabIndex: Int) {
//        _uiState.update { state ->
//            state.copy(
//                selectedTab = tabIndex,
//                filteredOrders = filterOrders(state.orders, tabIndex, state.searchQuery)
//            )
//        }
//    }
//
//    /**
//     * Update search query and filter orders
//     */
//    fun updateSearchQuery(query: String) {
//        _uiState.update { state ->
//            state.copy(
//                searchQuery = query,
//                filteredOrders = filterOrders(state.orders, state.selectedTab, query)
//            )
//        }
//    }
//
//    /**
//     * Clear search
//     */
//    fun clearSearch() {
//        updateSearchQuery("")
//    }
//
//    /**
//     * Cancel an order
//     */
//    fun cancelOrder(orderId: String, onResult: (Boolean, String) -> Unit) {
//        viewModelScope.launch {
//            repository.cancelOrder(orderId).fold(
//                onSuccess = {
//                    loadOrders() // Refresh list
//                    onResult(true, "Order cancelled successfully")
//                },
//                onFailure = { error ->
//                    onResult(false, error.message ?: "Failed to cancel order")
//                }
//            )
//        }
//    }
//
//    /**
//     * Reorder (create new order from existing one)
//     */
//    fun reorder(order: Order, onResult: (Boolean, String) -> Unit) {
//        viewModelScope.launch {
//            val newOrder = order.copy(
//                id = "",
//                orderNumber = generateOrderNumber(),
//                date = java.util.Date(),
//                status = OrderStatus.PENDING
//            )
//
//            repository.createOrder(newOrder).fold(
//                onSuccess = { orderId ->
//                    loadOrders() // Refresh list
//                    onResult(true, "Order placed successfully!")
//                },
//                onFailure = { error ->
//                    onResult(false, error.message ?: "Failed to place order")
//                }
//            )
//        }
//    }
//
//    /**
//     * Clear error message
//     */
//    fun clearError() {
//        _uiState.update { it.copy(error = null) }
//    }
//
//    /**
//     * Filter orders based on tab and search query
//     */
//    private fun filterOrders(
//        orders: List<Order>,
//        tabIndex: Int,
//        query: String
//    ): List<Order> {
//        // Filter by tab
//        val tabFiltered = when (tabIndex) {
//            0 -> orders // All
//            1 -> orders.filter { // Pending
//                it.status == OrderStatus.PENDING ||
//                        it.status == OrderStatus.PROCESSING ||
//                        it.status == OrderStatus.SHIPPED
//            }
//            2 -> orders.filter { it.status == OrderStatus.DELIVERED } // Completed
//            3 -> orders.filter { it.status == OrderStatus.CANCELLED } // Cancelled
//            else -> orders
//        }
//
//        // Filter by search query
//        return if (query.isBlank()) {
//            tabFiltered
//        } else {
//            tabFiltered.filter { order ->
//                order.orderNumber.contains(query, ignoreCase = true) ||
//                        order.items.any { it.name.contains(query, ignoreCase = true) }
//            }
//        }
//    }
//
//    /**
//     * Generate unique order number
//     */
//    private fun generateOrderNumber(): String {
//        val timestamp = System.currentTimeMillis()
//        val random = (1000..9999).random()
//        return "ORD-${timestamp}-${random}"
//    }
//}