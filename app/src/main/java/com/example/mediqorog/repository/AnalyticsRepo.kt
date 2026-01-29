package com.example.mediqorog.repository

import com.example.mediqorog.model.SalesData

interface AnalyticsRepository {
    suspend fun getSalesData(period: String, onSuccess: (List<SalesData>) -> Unit, onError: (String) -> Unit)
    suspend fun getRevenueByCategory(onSuccess: (Map<String, Double>) -> Unit, onError: (String) -> Unit)
    suspend fun getTopSellingProducts(limit: Int, onSuccess: (List<Pair<String, Int>>) -> Unit, onError: (String) -> Unit)
}