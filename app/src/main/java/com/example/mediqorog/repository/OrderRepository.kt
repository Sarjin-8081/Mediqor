package com.example.mediqorog.repository

import com.example.mediqorog.model.Order
import com.example.mediqorog.model.OrderStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

interface OrderRepository {
    suspend fun getUserOrders(): Result<List<Order>>
    suspend fun getAllOrders(): Result<List<Order>>
    suspend fun getOrdersByStatus(status: OrderStatus): Result<List<Order>>
    suspend fun getOrdersByUserId(userId: String): Result<List<Order>>
    suspend fun getOrderById(orderId: String): Result<Order?>
    suspend fun searchOrders(query: String): Result<List<Order>>
    suspend fun createOrder(order: Order): Result<String>
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<Unit>
    suspend fun updateOrder(order: Order): Result<Unit>
    suspend fun deleteOrder(orderId: String): Result<Unit>
    suspend fun cancelOrder(orderId: String): Result<Unit>
}

class OrderRepositoryImpl : OrderRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val ordersCollection = firestore.collection("orders")

    /**
     * Get orders for current logged-in user
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
                doc.toObject(Order::class.java)
            }

            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get all orders (admin function)
     */
    override suspend fun getAllOrders(): Result<List<Order>> {
        return try {
            val snapshot = ordersCollection
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            val orders = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Order::class.java)
            }

            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get orders filtered by status
     */
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
                doc.toObject(Order::class.java)
            }

            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get orders for specific user (admin function)
     */
    override suspend fun getOrdersByUserId(userId: String): Result<List<Order>> {
        return try {
            val snapshot = ordersCollection
                .whereEqualTo("userId", userId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            val orders = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Order::class.java)
            }

            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get single order by ID
     */
    override suspend fun getOrderById(orderId: String): Result<Order?> {
        return try {
            val doc = ordersCollection.document(orderId).get().await()
            val order = doc.toObject(Order::class.java)
            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Search orders by order number or item name
     */
    override suspend fun searchOrders(query: String): Result<List<Order>> {
        return try {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                return Result.failure(Exception("User not logged in"))
            }

            // Get all user orders first (Firestore doesn't support full-text search)
            val snapshot = ordersCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val allOrders = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Order::class.java)
            }

            // Filter in memory
            val filteredOrders = allOrders.filter { order ->
                order.orderNumber.contains(query, ignoreCase = true) ||
                        order.items.any { it.name.contains(query, ignoreCase = true) }
            }

            Result.success(filteredOrders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Create new order
     */
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

            Result.success(orderId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update order status
     */
    override suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<Unit> {
        return try {
            ordersCollection.document(orderId)
                .update("status", status)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update entire order
     */
    override suspend fun updateOrder(order: Order): Result<Unit> {
        return try {
            if (order.id.isBlank()) {
                return Result.failure(Exception("Order ID is required"))
            }

            ordersCollection.document(order.id)
                .set(order)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Delete order
     */
    override suspend fun deleteOrder(orderId: String): Result<Unit> {
        return try {
            ordersCollection.document(orderId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Cancel order (sets status to CANCELLED)
     */
    override suspend fun cancelOrder(orderId: String): Result<Unit> {
        return try {
            // Check if order exists and belongs to current user
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

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}