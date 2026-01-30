package com.example.mediqorog.utils

import com.example.mediqorog.model.OrderStatus

/**
 * Extension function to convert OrderStatus enum to display string
 */
fun OrderStatus.toDisplayString(): String {
    return when (this) {
        OrderStatus.PENDING -> "Pending"
        OrderStatus.PROCESSING -> "Processing"
        OrderStatus.SHIPPED -> "Shipped"
        OrderStatus.DELIVERED -> "Delivered"
        OrderStatus.CANCELLED -> "Cancelled"
    }
}

/**
 * Convert string to OrderStatus enum
 */
fun String.toOrderStatus(): OrderStatus {
    return when (this.uppercase()) {
        "PENDING" -> OrderStatus.PENDING
        "PROCESSING" -> OrderStatus.PROCESSING
        "SHIPPED" -> OrderStatus.SHIPPED
        "DELIVERED" -> OrderStatus.DELIVERED
        "CANCELLED" -> OrderStatus.CANCELLED
        else -> OrderStatus.PENDING
    }
}

/**
 * Get all order statuses as display strings
 */
fun getAllOrderStatusStrings(): List<String> {
    return OrderStatus.values().map { it.toDisplayString() }
}

/**
 * Get all order statuses
 */
fun getAllOrderStatuses(): List<OrderStatus> {
    return OrderStatus.values().toList()
}