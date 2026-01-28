package com.example.mediqorog.repository

import com.example.mediqorog.model.CartModel
import kotlinx.coroutines.flow.Flow

interface CartRepo {
    /**
     * Add product to cart or update quantity if already exists
     */
    suspend fun addToCart(
        userId: String,
        productId: String,
        productName: String,
        productImage: String,
        price: Double,
        quantity: Int,
        category: String,
        stock: Int
    ): Result<String>

    /**
     * Get all cart items for a user as Flow (real-time updates)
     */
    fun getCartItems(userId: String): Flow<List<CartModel>>

    /**
     * Update quantity of a cart item
     */
    suspend fun updateQuantity(cartItemId: String, newQuantity: Int): Result<Unit>

    /**
     * Remove item from cart
     */
    suspend fun removeFromCart(cartItemId: String): Result<Unit>

    /**
     * Clear entire cart for user
     */
    suspend fun clearCart(userId: String): Result<Unit>

    /**
     * Get cart item count for user
     */
    suspend fun getCartItemCount(userId: String): Result<Int>

    /**
     * Get total cart value
     */
    suspend fun getCartTotal(userId: String): Result<Double>
}