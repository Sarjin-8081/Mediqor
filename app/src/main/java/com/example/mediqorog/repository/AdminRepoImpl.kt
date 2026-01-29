package com.example.mediqorog.repoimpl

import com.example.mediqorog.model.AnalyticsModel
import com.example.mediqorog.model.OrderStatus
import com.example.mediqorog.repository.AdminRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AdminRepoImpl : AdminRepository {
    private val db = FirebaseFirestore.getInstance()

    override suspend fun getAnalytics(
        onSuccess: (AnalyticsModel) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val usersSnapshot = db.collection("users").get().await()
            val ordersSnapshot = db.collection("orders").get().await()
            val productsSnapshot = db.collection("products").get().await()

            val totalUsers = usersSnapshot.size()
            val totalOrders = ordersSnapshot.size()
            val totalProducts = productsSnapshot.size()

            val orders = ordersSnapshot.documents.mapNotNull { doc ->
                doc.getString("status")?.let { status ->
                    doc.getDouble("totalAmount")?.let { amount ->
                        Pair(status, amount)
                    }
                }
            }

            val pendingOrders = orders.count { it.first == OrderStatus.PENDING.name }
            val totalRevenue = orders.sumOf { it.second }

            // Today's data
            val todayStart = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
            val todayOrders = ordersSnapshot.documents.count {
                (it.getLong("date") ?: 0) >= todayStart
            }
            val todayRevenue = ordersSnapshot.documents
                .filter { (it.getLong("date") ?: 0) >= todayStart }
                .sumOf { it.getDouble("totalAmount") ?: 0.0 }

            val activeUsers = usersSnapshot.documents.count {
                it.getBoolean("isActive") ?: true
            }

            val lowStockProducts = productsSnapshot.documents.count {
                (it.getLong("stock") ?: 0) < 10
            }

            val analytics = AnalyticsModel(
                totalUsers = totalUsers,
                totalOrders = totalOrders,
                totalProducts = totalProducts,
                pendingOrders = pendingOrders,
                totalRevenue = totalRevenue,
                todayOrders = todayOrders,
                todayRevenue = todayRevenue,
                activeUsers = activeUsers,
                lowStockProducts = lowStockProducts
            )

            onSuccess(analytics)
        } catch (e: Exception) {
            onError(e.message ?: "Failed to fetch analytics")
        }
    }

    override suspend fun getTotalUsers(onSuccess: (Int) -> Unit) {
        try {
            val snapshot = db.collection("users").get().await()
            onSuccess(snapshot.size())
        } catch (e: Exception) {
            onSuccess(0)
        }
    }

    override suspend fun getTotalOrders(onSuccess: (Int) -> Unit) {
        try {
            val snapshot = db.collection("orders").get().await()
            onSuccess(snapshot.size())
        } catch (e: Exception) {
            onSuccess(0)
        }
    }

    override suspend fun getTotalProducts(onSuccess: (Int) -> Unit) {
        try {
            val snapshot = db.collection("products").get().await()
            onSuccess(snapshot.size())
        } catch (e: Exception) {
            onSuccess(0)
        }
    }

    override suspend fun getPendingOrders(onSuccess: (Int) -> Unit) {
        try {
            val snapshot = db.collection("orders")
                .whereEqualTo("status", OrderStatus.PENDING.name)
                .get()
                .await()
            onSuccess(snapshot.size())
        } catch (e: Exception) {
            onSuccess(0)
        }
    }

    override suspend fun getTotalRevenue(onSuccess: (Double) -> Unit) {
        try {
            val snapshot = db.collection("orders").get().await()
            val revenue = snapshot.documents.sumOf { it.getDouble("totalAmount") ?: 0.0 }
            onSuccess(revenue)
        } catch (e: Exception) {
            onSuccess(0.0)
        }
    }
}