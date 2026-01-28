package com.example.mediqorog.model

import com.google.firebase.Timestamp

data class CartModel(
    val id: String = "",
    val userId: String = "",
    val productId: String = "",
    val productName: String = "",
    val productImage: String = "",
    val price: Double = 0.0,
    val quantity: Int = 1,
    val category: String = "",
    val stock: Int = 0,
    val addedAt: Timestamp = Timestamp.now()
) {
    // Calculate total for this cart item
    fun getTotal(): Double = price * quantity

    // Check if product is in stock for requested quantity
    fun isAvailable(): Boolean = stock >= quantity
}