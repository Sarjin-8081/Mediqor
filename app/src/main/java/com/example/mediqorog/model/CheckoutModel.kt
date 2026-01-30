package com.example.mediqorog.model

data class CheckoutModel(
    val id: String = "",
    val userId: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val colonyLandmark: String = "",
    val promoCode: String = "",
    val cartItems: List<CartModel> = emptyList(),
    val itemsTotal: Double = 0.0,
    val deliveryFee: Double = 83.0,
    val discount: Double = 0.0,
    val total: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val orderStatus: String = "pending" // pending, confirmed, delivered, cancelled
)

data class CheckoutOrder(
    val orderId: String = "",
    val userId: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val colonyLandmark: String = "",
    val items: List<CartModel> = emptyList(),
    val itemsTotal: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val discount: Double = 0.0,
    val total: Double = 0.0,
    val orderStatus: String = "pending",
    val orderDate: Long = System.currentTimeMillis(),
    val estimatedDelivery: String = ""
)