package com.example.mediqorog.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.google.firebase.firestore.DocumentId
import java.util.*

/**
 * Order item in a purchase
 */
data class OrderItem(
    val productId: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0
) {
    // No-arg constructor for Firebase
    constructor() : this("", "", "", 0, 0.0)
}

/**
 * Complete order with all details
 */
data class Order(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val orderNumber: String = "",
    val date: Date = Date(),
    val items: List<OrderItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val status: OrderStatus = OrderStatus.PENDING,
    val deliveryAddress: String = "",
    val paymentMethod: String = "",
    val trackingUrl: String? = null,
    val prescriptionUrl: String? = null,
    val notes: String? = null
) {
    // No-arg constructor for Firebase
    constructor() : this("", "", "", Date(), emptyList(), 0.0, OrderStatus.PENDING, "", "")
}

/**
 * Order status enum
 */
//enum class OrderStatus {
//    PENDING,
//    PROCESSING,
//    SHIPPED,
//    DELIVERED,
//    CANCELLED;
enum class OrderStatus {
    PENDING,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED;

    fun toDisplayString() = name.lowercase().capitalize()  // Add this line
}
//    fun toDisplayString(): String {
//        return when (this) {
//            PENDING -> "Pending"
//            PROCESSING -> "Processing"
//            SHIPPED -> "Shipped"
//            DELIVERED -> "Delivered"
//            CANCELLED -> "Cancelled"
//        }
//    }
//}

/**
 * UI representation of order status
 */
data class OrderStatusUI(
    val backgroundColor: Color,
    val textColor: Color,
    val icon: ImageVector,
    val text: String
) {
    companion object {
        fun from(status: OrderStatus): OrderStatusUI {
            return when (status) {
                OrderStatus.PENDING -> OrderStatusUI(
                    backgroundColor = Color(0xFFFFF3E0u),
                    textColor = Color(0xFFFF9800u),
                    icon = Icons.Default.Info,
                    text = "Pending"
                )

                OrderStatus.PROCESSING -> OrderStatusUI(
                    backgroundColor = Color(0xFFE3F2FDu),
                    textColor = Color(0xFF2196F3u),
                    icon = Icons.Default.Refresh,
                    text = "Processing"
                )

                OrderStatus.SHIPPED -> OrderStatusUI(
                    backgroundColor = Color(0xFFE8F5E9u),
                    textColor = Color(0xFF4CAF50u),
                    icon = Icons.Filled.Send,
                    text = "Shipped"
                )

                OrderStatus.DELIVERED -> OrderStatusUI(
                    backgroundColor = Color(0xFFE8F5E9u),
                    textColor = Color(0xFF4CAF50u),
                    icon = Icons.Default.CheckCircle,
                    text = "Delivered"
                )

                OrderStatus.CANCELLED -> OrderStatusUI(
                    backgroundColor = Color(0xFFFFEBEEu),
                    textColor = Color(0xFFF44336u),
                    icon = Icons.Default.Close,
                    text = "Cancelled"
                )
            }
        }
    }
}