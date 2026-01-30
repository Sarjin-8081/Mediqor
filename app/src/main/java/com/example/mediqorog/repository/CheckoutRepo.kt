package com.example.mediqorog.repository

import com.example.mediqorog.model.CartModel
import com.example.mediqorog.model.CheckoutOrder
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface CheckoutRepository {
    suspend fun placeOrder(checkoutOrder: CheckoutOrder): Result<String>
    suspend fun clearCart(userId: String): Result<Unit>
    suspend fun getCartItems(userId: String): Result<List<CartModel>>
}

class CheckoutRepositoryImpl : CheckoutRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val ordersCollection = firestore.collection("orders")
    private val cartCollection = firestore.collection("cart")

    override suspend fun placeOrder(checkoutOrder: CheckoutOrder): Result<String> {
        return try {
            val orderId = ordersCollection.document().id
            val orderWithId = checkoutOrder.copy(orderId = orderId)

            ordersCollection.document(orderId)
                .set(orderWithId)
                .await()

            Result.success(orderId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun clearCart(userId: String): Result<Unit> {
        return try {
            val cartItems = cartCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val batch = firestore.batch()
            cartItems.documents.forEach { document ->
                batch.delete(document.reference)
            }
            batch.commit().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCartItems(userId: String): Result<List<CartModel>> {
        return try {
            val snapshot = cartCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val items = snapshot.documents.mapNotNull { doc ->
                doc.toObject(CartModel::class.java)?.copy(id = doc.id)
            }

            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}