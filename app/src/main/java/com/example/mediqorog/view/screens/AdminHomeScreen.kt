package com.example.mediqorog.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mediqorog.model.Order
import com.example.mediqorog.model.OrderStatus
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminHomeScreen(
    onAddProductClick: () -> Unit
) {
    var totalOrders by remember { mutableStateOf(0) }
    var pendingOrders by remember { mutableStateOf(0) }
    var totalRevenue by remember { mutableStateOf(0.0) }
    var lowStockCount by remember { mutableStateOf(0) }
    var recentOrders by remember { mutableStateOf<List<Order>>(emptyList()) }

    val db = FirebaseFirestore.getInstance()

    // Load dashboard data
    LaunchedEffect(Unit) {
        // Get orders
        db.collection("orders")
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let {
                    val orders = it.toObjects(Order::class.java)
                    totalOrders = orders.size
                    pendingOrders = orders.count { order -> order.status == OrderStatus.PENDING }
                    totalRevenue = orders.sumOf { order -> order.totalAmount }
                    recentOrders = orders.sortedByDescending { order -> order.date }.take(5)
                }
            }

        // Get low stock products
        db.collection("products")
            .whereLessThan("stock", 10)
            .addSnapshotListener { snapshot, _ ->
                lowStockCount = snapshot?.size() ?: 0
            }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddProductClick,
                containerColor = Color(0xFF0B8FAC),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Admin Dashboard",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B8FAC)
                )
            }

            // Stats Cards Row 1
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Total Orders",
                        value = totalOrders.toString(),
                        icon = Icons.Default.ShoppingCart,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Pending",
                        value = pendingOrders.toString(),
                        icon = Icons.Default.HourglassEmpty,
                        color = Color(0xFFFF9800),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Stats Cards Row 2
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Revenue",
                        value = "₹${String.format("%.0f", totalRevenue)}",
                        icon = Icons.Default.AttachMoney,
                        color = Color(0xFF2196F3),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Low Stock",
                        value = lowStockCount.toString(),
                        icon = Icons.Default.Warning,
                        color = Color(0xFFF44336),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Recent Orders
            item {
                Text(
                    "Recent Orders",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(recentOrders) { order ->
                RecentOrderCard(order)
            }

            // Extra padding for FAB
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    title,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun RecentOrderCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Order #${order.orderNumber}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(order.date),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "₹${String.format("%.2f", order.totalAmount)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF0B8FAC)
                )
                StatusBadge(order.status)
            }
        }
    }
}

@Composable
fun StatusBadge(status: OrderStatus) {
    val color = when (status) {
        OrderStatus.PENDING -> Color(0xFFFF9800)
        OrderStatus.PROCESSING -> Color(0xFF2196F3)
        OrderStatus.SHIPPED -> Color(0xFF9C27B0)
        OrderStatus.DELIVERED -> Color(0xFF4CAF50)
        OrderStatus.CANCELLED -> Color(0xFFF44336)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Text(
            status.toDisplayString(),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

// Extension function for OrderStatus
fun OrderStatus.toDisplayString(): String {
    return when (this) {
        OrderStatus.PENDING -> "Pending"
        OrderStatus.PROCESSING -> "Processing"
        OrderStatus.SHIPPED -> "Shipped"
        OrderStatus.DELIVERED -> "Delivered"
        OrderStatus.CANCELLED -> "Cancelled"
    }
}