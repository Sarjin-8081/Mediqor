package com.example.mediqorog.model

data class AnalyticsModel(
    val totalUsers: Int = 0,
    val totalOrders: Int = 0,
    val totalProducts: Int = 0,
    val pendingOrders: Int = 0,
    val totalRevenue: Double = 0.0,
    val todayOrders: Int = 0,
    val todayRevenue: Double = 0.0,
    val activeUsers: Int = 0,
    val lowStockProducts: Int = 0
)

data class ChartData(
    val label: String = "",
    val value: Double = 0.0
)

data class SalesData(
    val date: String = "",
    val revenue: Double = 0.0,
    val orders: Int = 0
)