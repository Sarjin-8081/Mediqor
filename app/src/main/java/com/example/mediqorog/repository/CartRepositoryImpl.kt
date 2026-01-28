package com.example.mediqorog.repository

import com.example.mediqorog.model.CartModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CartRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : CartRepo {

    private val cartCollection = firestore.collection("carts")

    override suspend fun addToCart(
        userId: String,
        productId: String,
        productName: String,
        productImage: String,
        price: Double,
        quantity: Int,
        category: String,
        stock: Int
    ): Result<String> {
        return try {
            // Check if product already exists in cart
            val existingCart = cartCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("productId", productId)
                .get()
                .await()

            if (!existingCart.isEmpty) {
                // Update quantity if exists
                val doc = existingCart.documents[0]
                val currentQty = doc.getLong("quantity")?.toInt() ?: 1
                val newQty = currentQty + quantity

                doc.reference.update("quantity", newQty).await()
                Result.success(doc.id)
            } else {
                // Add new cart item
                val cartItem = hashMapOf(
                    "userId" to userId,
                    "productId" to productId,
                    "productName" to productName,
                    "productImage" to productImage,
                    "price" to price,
                    "quantity" to quantity,
                    "category" to category,
                    "stock" to stock,
                    "addedAt" to Timestamp.now()
                )

                val docRef = cartCollection.add(cartItem).await()
                Result.success(docRef.id)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCartItems(userId: String): Flow<List<CartModel>> = callbackFlow {
        val listener = cartCollection
            .whereEqualTo("userId", userId)
            .orderBy("addedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val items = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        CartModel(
                            id = doc.id,
                            userId = doc.getString("userId") ?: "",
                            productId = doc.getString("productId") ?: "",
                            productName = doc.getString("productName") ?: "",
                            productImage = doc.getString("productImage") ?: "",
                            price = doc.getDouble("price") ?: 0.0,
                            quantity = doc.getLong("quantity")?.toInt() ?: 1,
                            category = doc.getString("category") ?: "",
                            stock = doc.getLong("stock")?.toInt() ?: 0,
                            addedAt = doc.getTimestamp("addedAt") ?: Timestamp.now()
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(items)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun updateQuantity(cartItemId: String, newQuantity: Int): Result<Unit> {
        return try {
            if (newQuantity <= 0) {
                // Remove item if quantity is 0 or less
                removeFromCart(cartItemId)
            } else {
                cartCollection.document(cartItemId)
                    .update("quantity", newQuantity)
                    .await()
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFromCart(cartItemId: String): Result<Unit> {
        return try {
            cartCollection.document(cartItemId).delete().await()
            Result.success(Unit)
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

            cartItems.documents.forEach { doc ->
                doc.reference.delete().await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCartItemCount(userId: String): Result<Int> {
        return try {
            val snapshot = cartCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val count = snapshot.documents.sumOf { doc ->
                doc.getLong("quantity")?.toInt() ?: 0
            }

            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCartTotal(userId: String): Result<Double> {
        return try {
            val snapshot = cartCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val total = snapshot.documents.sumOf { doc ->
                val price = doc.getDouble("price") ?: 0.0
                val quantity = doc.getLong("quantity")?.toInt() ?: 0
                price * quantity
            }

            Result.success(total)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}