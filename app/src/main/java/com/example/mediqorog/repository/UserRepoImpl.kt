package com.example.mediqorog.repository

import android.util.Log
import com.example.mediqorog.model.Order
import com.example.mediqorog.model.OrderItem
import com.example.mediqorog.model.OrderStatus
import com.example.mediqorog.model.User
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.Date

class UserRepoImpl : UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")
    private val ordersCollection = firestore.collection("orders")

    override suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            Log.d("UserRepoImpl", "Attempting sign in for: $email")
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: return Result.failure(Exception("User not found"))

            val userDoc = usersCollection.document(firebaseUser.uid).get().await()
            val user = userDoc.toObject(User::class.java) ?: User(
                uid = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                displayName = firebaseUser.displayName ?: ""
            )

            Log.d("UserRepoImpl", "Sign in successful: ${user.email}")
            Result.success(user)
        } catch (e: Exception) {
            Log.e("UserRepoImpl", "Sign in failed: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun signUp(
        email: String,
        password: String,
        displayName: String
    ): Result<User> {
        return try {
            Log.d("UserRepoImpl", "Attempting sign up for: $email")
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser =
                authResult.user ?: return Result.failure(Exception("Failed to create user"))

            val user = User(
                uid = firebaseUser.uid,
                email = email,
                displayName = displayName
            )

            usersCollection.document(firebaseUser.uid).set(user).await()

            Log.d("UserRepoImpl", "Sign up successful: ${user.email}")
            Result.success(user)
        } catch (e: Exception) {
            Log.e("UserRepoImpl", "Sign up failed: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<User> {
        return try {
            Log.d("UserRepoImpl", "Starting Google sign-in for: ${account.email}")
            Log.d("UserRepoImpl", "ID Token present: ${account.idToken != null}")

            if (account.idToken == null) {
                Log.e(
                    "UserRepoImpl",
                    "ID Token is null! Firebase authentication may not be configured."
                )
                val user = User(
                    uid = account.id ?: "",
                    email = account.email ?: "",
                    displayName = account.displayName ?: "",
                    photoUrl = account.photoUrl?.toString() ?: ""
                )
                return Result.success(user)
            }

            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            Log.d("UserRepoImpl", "Credential created, signing in with Firebase...")

            val authResult = auth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user ?: return Result.failure(Exception("User not found"))

            val user = User(
                uid = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                displayName = firebaseUser.displayName ?: "",
                photoUrl = firebaseUser.photoUrl?.toString() ?: ""
            )

            usersCollection.document(firebaseUser.uid).set(user).await()

            Log.d("UserRepoImpl", "Google sign-in successful: ${user.email}")
            Result.success(user)
        } catch (e: Exception) {
            Log.e("UserRepoImpl", "Google sign-in failed: ${e.message}", e)
            Result.failure(Exception("Google sign-in failed: ${e.message}"))
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Log.d("UserRepoImpl", "Sign out successful")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepoImpl", "Sign out failed: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null

        return try {
            val userDoc = usersCollection.document(firebaseUser.uid).get().await()
            userDoc.toObject(User::class.java)
        } catch (e: Exception) {
            Log.e("UserRepoImpl", "Failed to get current user: ${e.message}")
            null
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Log.d("UserRepoImpl", "Password reset email sent to: $email")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepoImpl", "Password reset failed: ${e.message}")
            Result.failure(e)
        }
    }

    // ==================== ORDER METHODS ====================

    suspend fun getUserOrders(): Result<List<Order>> {
        return try {
            val userId = auth.currentUser?.uid
            Log.d("UserRepoImpl", "Current userId: $userId")

            if (userId == null) {
                Log.e("UserRepoImpl", "User not logged in!")
                return Result.failure(Exception("User not logged in"))
            }

            val snapshot = ordersCollection
                .whereEqualTo("userId", userId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            Log.d("UserRepoImpl", "Found ${snapshot.documents.size} documents")

            val orders = snapshot.documents.mapNotNull { doc ->
                try {
                    Log.d("UserRepoImpl", "Parsing document: ${doc.id}")

                    // Manual parsing instead of automatic conversion
                    val items = (doc.get("items") as? List<Map<String, Any>>)?.map { itemMap ->
                        OrderItem(
                            productId = itemMap["productId"] as? String ?: "",
                            name = itemMap["name"] as? String ?: "",
                            imageUrl = itemMap["imageUrl"] as? String ?: "",
                            quantity = (itemMap["quantity"] as? Long)?.toInt() ?: 0,
                            price = (itemMap["price"] as? Number)?.toDouble() ?: 0.0
                        )
                    } ?: emptyList()

                    // FIXED STATUS PARSING
                    val statusString = doc.getString("status")?.uppercase()?.trim() ?: "PENDING"
                    val status = when (statusString) {
                        "PROCESSING" -> OrderStatus.PROCESSING
                        "PENDING" -> OrderStatus.PENDING
                        "SHIPPED" -> OrderStatus.SHIPPED
                        "DELIVERED" -> OrderStatus.DELIVERED
                        "CANCELLED" -> OrderStatus.CANCELLED
                        else -> {
                            Log.e("UserRepoImpl", "Unknown status: '$statusString', using PENDING")
                            OrderStatus.PENDING
                        }
                    }

                    val order = Order(
                        id = doc.id,
                        userId = doc.getString("userId") ?: "",
                        orderNumber = doc.getString("orderNumber") ?: "",
                        date = doc.getTimestamp("date")?.toDate() ?: Date(),
                        items = items,
                        totalAmount = doc.getDouble("totalAmount") ?: 0.0,
                        status = status,
                        deliveryAddress = doc.getString("deliveryAddress") ?: "",
                        paymentMethod = doc.getString("paymentMethod") ?: "",
                        trackingUrl = doc.getString("trackingUrl"),
                        prescriptionUrl = doc.getString("prescriptionUrl"),
                        notes = doc.getString("notes")
                    )

                    Log.d("UserRepoImpl", "Successfully parsed order: ${order.orderNumber}")
                    order

                } catch (e: Exception) {
                    Log.e("UserRepoImpl", "Error parsing order ${doc.id}: ${e.message}", e)
                    null
                }
            }

            Log.d("UserRepoImpl", "Successfully parsed ${orders.size} orders")
            Result.success(orders)

        } catch (e: Exception) {
            Log.e("UserRepoImpl", "Error fetching orders: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getOrder(orderId: String): Result<Order> {
        return try {
            val doc = ordersCollection.document(orderId).get().await()
            val order = doc.toObject(Order::class.java)?.copy(id = doc.id)
                ?: return Result.failure(Exception("Order not found"))

            Result.success(order)

        } catch (e: Exception) {
            Log.e("UserRepoImpl", "Error fetching order: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun createOrder(order: Order): Result<String> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("User not logged in"))

            val orderWithUser = order.copy(userId = userId)
            val docRef = ordersCollection.add(orderWithUser).await()

            Log.d("UserRepoImpl", "Order created: ${docRef.id}")
            Result.success(docRef.id)

        } catch (e: Exception) {
            Log.e("UserRepoImpl", "Error creating order: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<Unit> {
        return try {
            ordersCollection.document(orderId)
                .update("status", status.name)
                .await()

            Log.d("UserRepoImpl", "Order $orderId status updated to ${status.name}")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e("UserRepoImpl", "Error updating order status: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun cancelOrder(orderId: String): Result<Unit> {
        return updateOrderStatus(orderId, OrderStatus.CANCELLED)
    }

    suspend fun deleteOrder(orderId: String): Result<Unit> {
        return try {
            ordersCollection.document(orderId).delete().await()
            Log.d("UserRepoImpl", "Order deleted: $orderId")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e("UserRepoImpl", "Error deleting order: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun createTestOrders(): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("User not logged in"))

            val testOrders = listOf(
                Order(
                    id = "",
                    userId = userId,
                    orderNumber = "ORD-2026-001",
                    date = Date(),
                    items = listOf(
                        OrderItem("1", "Paracetamol 500mg", "", 2, 50.0),
                        OrderItem("2", "Vitamin C 1000mg", "", 1, 120.0)
                    ),
                    totalAmount = 220.0,
                    status = OrderStatus.PROCESSING,
                    deliveryAddress = "Thamel, Kathmandu, Nepal",
                    paymentMethod = "eSewa",
                    trackingUrl = null,
                    prescriptionUrl = null,
                    notes = null
                ),
                Order(
                    id = "",
                    userId = userId,
                    orderNumber = "ORD-2026-002",
                    date = Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000),
                    items = listOf(
                        OrderItem("3", "Hand Sanitizer 500ml", "", 3, 150.0)
                    ),
                    totalAmount = 450.0,
                    status = OrderStatus.DELIVERED,
                    deliveryAddress = "Patan, Lalitpur, Nepal",
                    paymentMethod = "Cash on Delivery",
                    trackingUrl = null,
                    prescriptionUrl = null,
                    notes = null
                ),
                Order(
                    id = "",
                    userId = userId,
                    orderNumber = "ORD-2026-003",
                    date = Date(System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000),
                    items = listOf(
                        OrderItem("4", "Face Mask N95", "", 5, 200.0),
                        OrderItem("5", "Vitamin D3", "", 1, 300.0)
                    ),
                    totalAmount = 1300.0,
                    status = OrderStatus.SHIPPED,
                    deliveryAddress = "Bhaktapur, Nepal",
                    paymentMethod = "Khalti",
                    trackingUrl = null,
                    prescriptionUrl = null,
                    notes = null
                )
            )

            testOrders.forEach { order ->
                ordersCollection.add(order).await()
            }

            Log.d("UserRepoImpl", "Test orders created successfully")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e("UserRepoImpl", "Error creating test orders: ${e.message}", e)
            Result.failure(e)
        }
    }
}