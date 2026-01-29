package com.example.mediqorog.repository

import android.util.Log
import com.example.mediqorog.model.Order
import com.example.mediqorog.model.OrderStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class OrderRepoImpl : OrderRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val ordersCollection = firestore.collection("orders")
    private val TAG = "OrderRepository"

    override suspend fun getAllOrders(): Result<List<Order>> {
        return try {
            val snapshot = ordersCollection
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            val orders = snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(Order::class.java)
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
            val snapshot = ordersCollection
                .whereEqualTo("status", status.name)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            val orders = snapshot.toObjects(Order::class.java)
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

            val orders = snapshot.toObjects(Order::class.java)
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
                val order = document.toObject(Order::class.java)
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
            val snapshot = ordersCollection
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            val orders = snapshot.toObjects(Order::class.java)
                .filter { order ->
                    order.orderNumber.contains(query, ignoreCase = true) ||
                            order.userId.contains(query, ignoreCase = true) ||
                            order.deliveryAddress.contains(query, ignoreCase = true)
                }

            Log.d(TAG, "Found ${orders.size} orders matching: $query")
            Result.success(orders)
        } catch (e: Exception) {
            Log.e(TAG, "Error searching orders", e)
            Result.failure(e)
        }
    }

    override suspend fun createOrder(order: Order): Result<String> {
        return try {
            val documentReference = ordersCollection.add(order).await()
            Log.d(TAG, "Order created with ID: ${documentReference.id}")
            Result.success(documentReference.id)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating order", e)
            Result.failure(e)
        }
    }

    override suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<Unit> {
        return try {
            ordersCollection.document(orderId)
                .update("status", status.name)
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
}