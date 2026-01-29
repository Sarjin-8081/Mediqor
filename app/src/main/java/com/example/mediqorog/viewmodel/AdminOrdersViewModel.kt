package com.example.mediqorog.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediqorog.model.Order
import com.example.mediqorog.model.OrderStatus
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OrdersUiState(
    val isLoading: Boolean = false,
    val orders: List<Order> = emptyList(),
    val filteredOrders: List<Order> = emptyList(),
    val selectedStatus: String = "All",
    val searchQuery: String = "",
    val error: String? = null,
    val successMessage: String? = null
)

class AdminOrdersViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    private val db = FirebaseFirestore.getInstance()
    private val TAG = "AdminOrdersVM"

    init {
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            db.collection("orders")
                .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { snapshot ->
                    val orders = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(Order::class.java)?.copy(id = doc.id)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing order ${doc.id}", e)
                            null
                        }
                    }

                    Log.d(TAG, "Loaded ${orders.size} orders")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        orders = orders,
                        filteredOrders = orders
                    )
                }
                .addOnFailureListener { error ->
                    Log.e(TAG, "Failed to load orders", error)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load orders"
                    )
                }
        }
    }

    fun searchOrders(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)

        if (query.isEmpty()) {
            filterByStatus(_uiState.value.selectedStatus)
            return
        }

        val filtered = _uiState.value.orders.filter { order ->
            order.orderNumber.contains(query, ignoreCase = true) ||
                    order.userId.contains(query, ignoreCase = true)
        }

        _uiState.value = _uiState.value.copy(filteredOrders = filtered)
        Log.d(TAG, "Search results: ${filtered.size}")
    }

    fun filterByStatus(status: String) {
        _uiState.value = _uiState.value.copy(selectedStatus = status)

        val filtered = if (status == "All") {
            _uiState.value.orders
        } else {
            _uiState.value.orders.filter {
                it.status.name == status
            }
        }

        _uiState.value = _uiState.value.copy(filteredOrders = filtered)
        Log.d(TAG, "Status filter: ${filtered.size} orders")
    }

    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            db.collection("orders").document(orderId)
                .update("status", newStatus.name)
                .addOnSuccessListener {
                    Log.d(TAG, "Order status updated: $orderId -> $newStatus")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Order status updated successfully"
                    )
                    loadOrders()
                }
                .addOnFailureListener { error ->
                    Log.e(TAG, "Failed to update order status", error)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to update order status"
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
}