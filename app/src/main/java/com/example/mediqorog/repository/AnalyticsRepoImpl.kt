package com.example.mediqorog.repoimpl

import com.example.mediqorog.model.SalesData
import com.example.mediqorog.repository.AnalyticsRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class AnalyticsRepoImpl : AnalyticsRepository {
    private val db = FirebaseFirestore.getInstance()

    override suspend fun getSalesData(
        period: String,
        onSuccess: (List<SalesData>) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val calendar = Calendar.getInstance()
            val salesList = mutableListOf<SalesData>()
            val days = when (period) {
                "week" -> 7
                "month" -> 30
                else -> 7
            }

            val ordersSnapshot = db.collection("orders").get().await()
            val allOrders = ordersSnapshot.documents

            for (i in days - 1 downTo 0) {
                calendar.time = Date()
                calendar.add(Calendar.DAY_OF_YEAR, -i)
                val dayStart = calendar.timeInMillis
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                val dayEnd = calendar.timeInMillis

                val dayOrders = allOrders.filter {
                    val orderDate = it.getLong("date") ?: 0
                    orderDate in dayStart until dayEnd
                }

                val revenue = dayOrders.sumOf { it.getDouble("totalAmount") ?: 0.0 }
                val count = dayOrders.size

                salesList.add(
                    SalesData(
                        date = SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(dayStart)),
                        revenue = revenue,
                        orders = count
                    )
                )
            }

            onSuccess(salesList)
        } catch (e: Exception) {
            onError(e.message ?: "Failed to fetch sales data")
        }
    }

    override suspend fun getRevenueByCategory(
        onSuccess: (Map<String, Double>) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val ordersSnapshot = db.collection("orders").get().await()
            val categoryRevenue = mutableMapOf<String, Double>()

            ordersSnapshot.documents.forEach { doc ->
                val items = doc.get("items") as? List<Map<String, Any>> ?: emptyList()
                items.forEach { item ->
                    val category = item["category"] as? String ?: "Unknown"
                    val price = (item["price"] as? Number)?.toDouble() ?: 0.0
                    val quantity = (item["quantity"] as? Number)?.toInt() ?: 0
                    val revenue = price * quantity

                    categoryRevenue[category] = (categoryRevenue[category] ?: 0.0) + revenue
                }
            }

            onSuccess(categoryRevenue)
        } catch (e: Exception) {
            onError(e.message ?: "Failed to fetch revenue by category")
        }
    }

    override suspend fun getTopSellingProducts(
        limit: Int,
        onSuccess: (List<Pair<String, Int>>) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val ordersSnapshot = db.collection("orders").get().await()
            val productSales = mutableMapOf<String, Int>()

            ordersSnapshot.documents.forEach { doc ->
                val items = doc.get("items") as? List<Map<String, Any>> ?: emptyList()
                items.forEach { item ->
                    val productName = item["productName"] as? String ?: "Unknown"
                    val quantity = (item["quantity"] as? Number)?.toInt() ?: 0
                    productSales[productName] = (productSales[productName] ?: 0) + quantity
                }
            }

            val topProducts = productSales.entries
                .sortedByDescending { it.value }
                .take(limit)
                .map { it.key to it.value }

            onSuccess(topProducts)
        } catch (e: Exception) {
            onError(e.message ?: "Failed to fetch top selling products")
        }
    }
}