package com.example.mediqorog.repository

import com.example.mediqorog.model.Order
import com.example.mediqorog.model.OrderStatus

interface OrderRepository {
    suspend fun getAllOrders(): Result<List<Order>>
    suspend fun getOrdersByStatus(status: OrderStatus): Result<List<Order>>
    suspend fun getOrdersByUserId(userId: String): Result<List<Order>>
    suspend fun getOrderById(orderId: String): Result<Order?>
    suspend fun searchOrders(query: String): Result<List<Order>>
    suspend fun createOrder(order: Order): Result<String>
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<Unit>
    suspend fun updateOrder(order: Order): Result<Unit>
    suspend fun deleteOrder(orderId: String): Result<Unit>
}