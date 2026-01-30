package com.example.mediqorog.model

import com.google.firebase.Timestamp
import java.util.*

data class Order(
    val id: String = "",
    val userId: String = "",
    val orderNumber: String = "",
    val items: List<OrderItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val status: OrderStatus = OrderStatus.PENDING,
    val date: Date = Date(),
    val timestamp: Timestamp = Timestamp.now(),

    // Address details
    val deliveryAddress: String = "",
    val contactPhone: String = "",
    val contactName: String = "",

    // Payment
    val paymentMethod: String = "Cash on Delivery",
    val isPaid: Boolean = false,

    // Optional notes
    val orderNotes: String = ""
)

data class OrderItem(
    val id: String = "",
    val name: String = "",
    val productName: String = "", // Alternative name field
    val quantity: Int = 1,
    val price: Double = 0.0,
    val imageUrl: String = ""
)

enum class OrderStatus {
    PENDING,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED
}