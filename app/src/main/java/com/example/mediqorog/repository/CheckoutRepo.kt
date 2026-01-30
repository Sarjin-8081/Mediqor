package com.example.mediqorog.repository

import com.example.mediqorog.model.CartModel
import com.example.mediqorog.model.CheckoutOrder

interface CheckoutRepository {
    suspend fun placeOrder(checkoutOrder: CheckoutOrder): Result<String>
    suspend fun clearCart(userId: String): Result<Unit>
    suspend fun getCartItems(userId: String): Result<List<CartModel>>
}