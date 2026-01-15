package com.example.mediqorog.repository

import android.util.Log
import com.example.mediqorog.model.Order
import com.example.mediqorog.model.OrderItem
import com.example.mediqorog.model.OrderStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date

/**
 * Repository wrapper that uses UserRepoImpl for order operations
 */
class OrderRepository {

    private val userRepo = UserRepoImpl()
    private val auth = FirebaseAuth.getInstance()
    private val ordersCollection = FirebaseFirestore.getInstance().collection("orders")

    /**
     * Get all orders for current user
     */
    suspend fun getUserOrders(): Result<List<Order>> {
        return userRepo.getUserOrders()
    }

    /**
     * Get single order by ID
     */
    suspend fun getOrder(orderId: String): Result<Order> {
        return userRepo.getOrder(orderId)
    }

    /**
     * Create new order
     */
    suspend fun createOrder(order: Order): Result<String> {
        return userRepo.createOrder(order)
    }

    /**
     * Update order status
     */
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<Unit> {
        return userRepo.updateOrderStatus(orderId, status)
    }

    /**
     * Cancel order
     */
    suspend fun cancelOrder(orderId: String): Result<Unit> {
        return userRepo.cancelOrder(orderId)
    }

    /**
     * Delete order
     */
    suspend fun deleteOrder(orderId: String): Result<Unit> {
        return userRepo.deleteOrder(orderId)
    }

    /**
     * Create test orders (for demo/testing)
     */
    suspend fun createTestOrders(): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("User not logged in"))

            val testOrders = listOf(
                Order(
                    id = "",
                    userId = userId,
                    orderNumber = "ORD-2026-001",
                    date = Date(),
                    items = listOf(
                        OrderItem("1", "Paracetamol 500mg", "", 2, 50.0),
                        OrderItem("2", "Vitamin C 1000mg", "", 1, 120.0)
                    ),
                    totalAmount = 220.0,
                    status = OrderStatus.PROCESSING,
                    deliveryAddress = "Thamel, Kathmandu, Nepal",
                    paymentMethod = "eSewa",
                    trackingUrl = null,
                    prescriptionUrl = null,
                    notes = null
                ),
                Order(
                    id = "",
                    userId = userId,
                    orderNumber = "ORD-2026-002",
                    date = Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000),
                    items = listOf(
                        OrderItem("3", "Hand Sanitizer 500ml", "", 3, 150.0)
                    ),
                    totalAmount = 450.0,
                    status = OrderStatus.DELIVERED,
                    deliveryAddress = "Patan, Lalitpur, Nepal",
                    paymentMethod = "Cash on Delivery",
                    trackingUrl = null,
                    prescriptionUrl = null,
                    notes = null
                ),
                Order(
                    id = "",
                    userId = userId,
                    orderNumber = "ORD-2026-003",
                    date = Date(System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000),
                    items = listOf(
                        OrderItem("4", "Face Mask N95", "", 5, 200.0),
                        OrderItem("5", "Vitamin D3", "", 1, 300.0)
                    ),
                    totalAmount = 1300.0,
                    status = OrderStatus.SHIPPED,
                    deliveryAddress = "Bhaktapur, Nepal",
                    paymentMethod = "Khalti",
                    trackingUrl = null,
                    prescriptionUrl = null,
                    notes = null
                )
            )

            testOrders.forEach { order ->
                ordersCollection.add(order).await()
            }

            Log.d("OrderRepository", "Test orders created successfully")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e("OrderRepository", "Error creating test orders: ${e.message}", e)
            Result.failure(e)
        }
    }
}