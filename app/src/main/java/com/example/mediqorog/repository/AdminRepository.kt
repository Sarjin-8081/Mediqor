package com.example.mediqorog.repository

import com.example.mediqorog.model.AnalyticsModel

interface AdminRepository {
    suspend fun getAnalytics(onSuccess: (AnalyticsModel) -> Unit, onError: (String) -> Unit)
    suspend fun getTotalUsers(onSuccess: (Int) -> Unit)
    suspend fun getTotalOrders(onSuccess: (Int) -> Unit)
    suspend fun getTotalProducts(onSuccess: (Int) -> Unit)
    suspend fun getPendingOrders(onSuccess: (Int) -> Unit)
    suspend fun getTotalRevenue(onSuccess: (Double) -> Unit)
}