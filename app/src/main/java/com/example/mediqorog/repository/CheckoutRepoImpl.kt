package com.example.mediqorog.repository

import android.util.Log
import com.example.mediqorog.model.CartModel
import com.example.mediqorog.model.CheckoutOrder
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CheckoutRepositoryImpl : CheckoutRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val ordersCollection = firestore.collection("orders")
    private val cartCollection = firestore.collection("carts")

    companion object {
        private const val TAG = "CheckoutRepo"
    }

    override suspend fun placeOrder(checkoutOrder: CheckoutOrder): Result<String> {
        return try {
            Log.d(TAG, "📝 Placing order for user: ${checkoutOrder.userId}")

            val orderId = ordersCollection.document().id
            val orderWithId = checkoutOrder.copy(orderId = orderId)

            Log.d(TAG, "🆔 Order ID: $orderId")
            Log.d(TAG, "📦 Items: ${orderWithId.items.size}")
            Log.d(TAG, "💰 Total: Rs.${orderWithId.total}")

            ordersCollection.document(orderId)
                .set(orderWithId)
                .await()

            Log.d(TAG, "✅ Order placed successfully!")
            Result.success(orderId)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error placing order: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun clearCart(userId: String): Result<Unit> {
        return try {
            Log.d(TAG, "🗑️ Clearing cart for user: $userId")

            val cartItems = cartCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            Log.d(TAG, "📊 Found ${cartItems.documents.size} items to delete")

            val batch = firestore.batch()
            cartItems.documents.forEach { document ->
                batch.delete(document.reference)
            }
            batch.commit().await()

            Log.d(TAG, "✅ Cart cleared successfully!")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error clearing cart: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun getCartItems(userId: String): Result<List<CartModel>> {
        return try {
            Log.d(TAG, "═══════════════════════════════════════")
            Log.d(TAG, "🔍 FETCHING CART ITEMS")
            Log.d(TAG, "═══════════════════════════════════════")
            Log.d(TAG, "🔑 User ID: $userId")
            Log.d(TAG, "📂 Collection: cart")

            val snapshot = cartCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            Log.d(TAG, "─────────────────────────────────────")
            Log.d(TAG, "📦 Firestore query completed")
            Log.d(TAG, "📊 Total documents found: ${snapshot.documents.size}")
            Log.d(TAG, "📊 Is empty: ${snapshot.isEmpty}")
            Log.d(TAG, "─────────────────────────────────────")

            if (snapshot.isEmpty) {
                Log.w(TAG, "⚠️ WARNING: No documents found!")
                Log.w(TAG, "   Possible reasons:")
                Log.w(TAG, "   1. Cart is actually empty")
                Log.w(TAG, "   2. User ID doesn't match")
                Log.w(TAG, "   3. Items in different collection")
                Log.d(TAG, "═══════════════════════════════════════")
                return Result.success(emptyList())
            }

            val items = mutableListOf<CartModel>()

            snapshot.documents.forEachIndexed { index, doc ->
                Log.d(TAG, "\n📄 Document ${index + 1}:")
                Log.d(TAG, "   ID: ${doc.id}")
                Log.d(TAG, "   Exists: ${doc.exists()}")
                Log.d(TAG, "   Raw data: ${doc.data}")

                // Log each field separately
                Log.d(TAG, "   Fields:")
                Log.d(TAG, "     - userId: ${doc.getString("userId")}")
                Log.d(TAG, "     - productId: ${doc.getString("productId")}")
                Log.d(TAG, "     - productName: ${doc.getString("productName")}")
                Log.d(TAG, "     - productImage: ${doc.getString("productImage")}")
                Log.d(TAG, "     - price: ${doc.get("price")} (${doc.get("price")?.javaClass?.simpleName})")
                Log.d(TAG, "     - quantity: ${doc.get("quantity")} (${doc.get("quantity")?.javaClass?.simpleName})")
                Log.d(TAG, "     - category: ${doc.getString("category")}")
                Log.d(TAG, "     - stock: ${doc.get("stock")}")

                try {
                    val cartItem = doc.toObject(CartModel::class.java)

                    if (cartItem != null) {
                        val itemWithId = cartItem.copy(id = doc.id)
                        items.add(itemWithId)

                        Log.d(TAG, "   ✅ Successfully parsed:")
                        Log.d(TAG, "      Product: ${itemWithId.productName}")
                        Log.d(TAG, "      Price: Rs. ${itemWithId.price}")
                        Log.d(TAG, "      Qty: ${itemWithId.quantity}")
                        Log.d(TAG, "      Subtotal: Rs. ${itemWithId.price * itemWithId.quantity}")
                    } else {
                        Log.e(TAG, "   ❌ toObject returned null!")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "   ❌ Error parsing document: ${e.message}", e)
                    Log.e(TAG, "   Stack trace: ${e.stackTraceToString()}")
                }
            }

            val totalValue = items.sumOf { it.price * it.quantity }

            Log.d(TAG, "\n─────────────────────────────────────")
            Log.d(TAG, "✅ FINAL RESULTS:")
            Log.d(TAG, "   Items loaded: ${items.size}")
            Log.d(TAG, "   Total value: Rs. $totalValue")
            Log.d(TAG, "═══════════════════════════════════════\n")

            Result.success(items)
        } catch (e: Exception) {
            Log.e(TAG, "═══════════════════════════════════════")
            Log.e(TAG, "❌ EXCEPTION in getCartItems")
            Log.e(TAG, "Error: ${e.message}")
            Log.e(TAG, "Stack trace:", e)
            Log.e(TAG, "═══════════════════════════════════════")
            Result.failure(e)
        }
    }
}