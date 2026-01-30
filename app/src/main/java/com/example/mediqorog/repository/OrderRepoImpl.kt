package com.example.mediqorog.repository

import android.util.Log
import com.example.mediqorog.model.Order
import com.example.mediqorog.model.OrderStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class OrderRepoImpl : OrderRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val ordersCollection = firestore.collection("orders")
    private val TAG = "OrderRepository"

    /**
     * Get orders for current logged-in user (NO userId parameter)
     */
    override suspend fun getUserOrders(): Result<List<Order>> {
        return try {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                return Result.failure(Exception("User not logged in"))
            }

            val snapshot = ordersCollection
                .whereEqualTo("userId", userId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            val orders = snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(Order::class.java)?.copy(id = doc.id)
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing order ${doc.id}", e)
                    null
                }
            }

            Log.d(TAG, "Loaded ${orders.size} orders for user: $userId")
            Result.success(orders)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user orders", e)
            Result.failure(e)
        }
    }

    override suspend fun getAllOrders(): Result<List<Order>> {
        return try {
            val snapshot = ordersCollection
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            val orders = snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(Order::class.java)?.copy(id = doc.id)
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing order ${doc.id}", e)
                    null
                }
            }

            Log.d(TAG, "Loaded ${orders.size} orders")
            Result.success(orders)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting all orders", e)
            Result.failure(e)
        }
    }

    override suspend fun getOrdersByStatus(status: OrderStatus): Result<List<Order>> {
        return try {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                return Result.failure(Exception("User not logged in"))
            }

            val snapshot = ordersCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", status)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            val orders = snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(Order::class.java)?.copy(id = doc.id)
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing order ${doc.id}", e)
                    null
                }
            }

            Log.d(TAG, "Loaded ${orders.size} orders with status: $status")
            Result.success(orders)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting orders by status", e)
            Result.failure(e)
        }
    }

    override suspend fun getOrdersByUserId(userId: String): Result<List<Order>> {
        return try {
            val snapshot = ordersCollection
                .whereEqualTo("userId", userId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            val orders = snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(Order::class.java)?.copy(id = doc.id)
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing order ${doc.id}", e)
                    null
                }
            }

            Log.d(TAG, "Loaded ${orders.size} orders for user: $userId")
            Result.success(orders)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting orders by user", e)
            Result.failure(e)
        }
    }

    override suspend fun getOrderById(orderId: String): Result<Order?> {
        return try {
            val document = ordersCollection.document(orderId).get().await()

            if (document.exists()) {
                val order = document.toObject(Order::class.java)?.copy(id = document.id)
                Log.d(TAG, "Order loaded: ${order?.orderNumber}")
                Result.success(order)
            } else {
                Log.w(TAG, "Order not found: $orderId")
                Result.success(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting order by ID", e)
            Result.failure(e)
        }
    }

    override suspend fun searchOrders(query: String): Result<List<Order>> {
        return try {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                return Result.failure(Exception("User not logged in"))
            }

            // Get all user orders first
            val snapshot = ordersCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val allOrders = snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(Order::class.java)?.copy(id = doc.id)
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing order ${doc.id}", e)
                    null
                }
            }

            // Filter in memory
            val filteredOrders = allOrders.filter { order ->
                order.orderNumber.contains(query, ignoreCase = true) ||
                        order.items.any {
                            (it.productName ?: it.name).contains(query, ignoreCase = true)
                        }
            }

            Log.d(TAG, "Found ${filteredOrders.size} orders matching: $query")
            Result.success(filteredOrders)
        } catch (e: Exception) {
            Log.e(TAG, "Error searching orders", e)
            Result.failure(e)
        }
    }

    override suspend fun createOrder(order: Order): Result<String> {
        return try {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                return Result.failure(Exception("User not logged in"))
            }

            // Generate order ID
            val orderId = ordersCollection.document().id

            // Create order with ID and userId
            val orderWithDetails = order.copy(
                id = orderId,
                userId = userId,
                date = java.util.Date()
            )

            // Save to Firestore
            ordersCollection.document(orderId)
                .set(orderWithDetails)
                .await()

            Log.d(TAG, "Order created with ID: $orderId")
            Result.success(orderId)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating order", e)
            Result.failure(e)
        }
    }

    override suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<Unit> {
        return try {
            ordersCollection.document(orderId)
                .update("status", status)
                .await()

            Log.d(TAG, "Order status updated: $orderId -> $status")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating order status", e)
            Result.failure(e)
        }
    }

    override suspend fun updateOrder(order: Order): Result<Unit> {
        return try {
            if (order.id.isBlank()) {
                return Result.failure(Exception("Order ID is required"))
            }

            ordersCollection.document(order.id)
                .set(order)
                .await()

            Log.d(TAG, "Order updated: ${order.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating order", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteOrder(orderId: String): Result<Unit> {
        return try {
            ordersCollection.document(orderId).delete().await()
            Log.d(TAG, "Order deleted: $orderId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting order", e)
            Result.failure(e)
        }
    }

    override suspend fun cancelOrder(orderId: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                return Result.failure(Exception("User not logged in"))
            }

            val orderDoc = ordersCollection.document(orderId).get().await()
            val order = orderDoc.toObject(Order::class.java)

            if (order == null) {
                return Result.failure(Exception("Order not found"))
            }

            if (order.userId != userId) {
                return Result.failure(Exception("Unauthorized"))
            }

            // Check if order can be cancelled
            if (order.status == OrderStatus.DELIVERED || order.status == OrderStatus.CANCELLED) {
                return Result.failure(Exception("Order cannot be cancelled"))
            }

            // Update status to cancelled
            ordersCollection.document(orderId)
                .update("status", OrderStatus.CANCELLED)
                .await()

            Log.d(TAG, "Order cancelled: $orderId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error cancelling order", e)
            Result.failure(e)
        }
    }
}