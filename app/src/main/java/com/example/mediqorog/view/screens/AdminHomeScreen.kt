package com.example.mediqorog.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

data class DashboardStats(
    val totalOrders: Int = 0,
    val pendingOrders: Int = 0,
    val processingOrders: Int = 0,
    val deliveredOrders: Int = 0,
    val totalRevenue: Double = 0.0,
    val totalProducts: Int = 0,
    val lowStockProducts: Int = 0
)

data class RecentOrder(
    val orderId: String = "",
    val orderNumber: String = "",
    val customerName: String = "",
    val totalAmount: Double = 0.0,
    val status: String = "PENDING",
    val timestamp: Long = 0L
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    onNavigateToProducts: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    var stats by remember { mutableStateOf(DashboardStats()) }
    var recentOrders by remember { mutableStateOf<List<RecentOrder>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var userName by remember { mutableStateOf("Admin") }
    var userEmail by remember { mutableStateOf("") }

    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    LaunchedEffect(Unit) {
        // Load user info
        auth.currentUser?.let { user ->
            userEmail = user.email ?: ""
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { doc ->
                    // ✅ FIXED: Use displayName instead of fullName/name
                    userName = doc.getString("displayName") ?: "Admin"
                }
        }

        // Load stats
        // Orders
        db.collection("orders").get()
            .addOnSuccessListener { ordersSnapshot ->
                val orders = ordersSnapshot.documents
                val pending = orders.count { it.getString("status") == "PENDING" }
                val processing = orders.count { it.getString("status") == "PROCESSING" }
                val delivered = orders.count { it.getString("status") == "DELIVERED" }
                val revenue = orders.sumOf { it.getDouble("totalAmount") ?: 0.0 }

                // Recent orders
                recentOrders = orders.take(5).mapNotNull { doc ->
                    RecentOrder(
                        orderId = doc.id,
                        orderNumber = doc.getString("orderNumber") ?: "",
                        // ✅ FIXED: Get userId and fetch user name separately, or use "Customer" as fallback
                        customerName = doc.getString("userId") ?: "Customer",
                        totalAmount = doc.getDouble("totalAmount") ?: 0.0,
                        status = doc.getString("status") ?: "PENDING",
                        timestamp = doc.getLong("createdAt") ?: 0L
                    )
                }.sortedByDescending { it.timestamp }

                // Products
                // ✅ FIXED: Removed whereEqualTo("isActive", true) - your model uses inStock
                db.collection("products")
                    .get()
                    .addOnSuccessListener { productsSnapshot ->
                        val products = productsSnapshot.documents
                        val lowStock = products.count {
                            val stock = it.getLong("stock")?.toInt() ?: 0
                            stock < 10
                        }

                        stats = DashboardStats(
                            totalOrders = orders.size,
                            pendingOrders = pending,
                            processingOrders = processing,
                            deliveredOrders = delivered,
                            totalRevenue = revenue,
                            totalProducts = products.size,
                            lowStockProducts = lowStock
                        )
                        isLoading = false
                    }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0B8FAC)
                ),
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF0B8FAC))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Welcome Header
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF0B8FAC)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    "Welcome back,",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                                Text(
                                    userName,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // Quick Actions
                item {
                    Text(
                        "Quick Actions",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickActionCard(
                            title = "Products",
                            icon = Icons.Default.Inventory,
                            color = Color(0xFF2196F3),
                            onClick = onNavigateToProducts,
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionCard(
                            title = "Orders",
                            icon = Icons.Default.ShoppingBag,
                            color = Color(0xFFFF9800),
                            onClick = onNavigateToOrders,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Statistics
                item {
                    Text(
                        "Statistics",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Total Orders",
                            value = stats.totalOrders.toString(),
                            icon = Icons.Default.ShoppingCart,
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Pending",
                            value = stats.pendingOrders.toString(),
                            icon = Icons.Default.HourglassEmpty,
                            color = Color(0xFFFF9800),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Revenue",
                            value = "₹${String.format("%.0f", stats.totalRevenue)}",
                            icon = Icons.Default.AttachMoney,
                            color = Color(0xFF2196F3),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Products",
                            value = stats.totalProducts.toString(),
                            icon = Icons.Default.Inventory,
                            color = Color(0xFF9C27B0),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Low Stock Alert
                if (stats.lowStockProducts > 0) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFF3E0)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFFF9800),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    "${stats.lowStockProducts} products running low on stock",
                                    fontSize = 14.sp,
                                    color = Color(0xFFE65100)
                                )
                            }
                        }
                    }
                }

                // Recent Orders
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Recent Orders",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )
                        TextButton(onClick = onNavigateToOrders) {
                            Text("View All", color = Color(0xFF0B8FAC))
                        }
                    }
                }

                if (recentOrders.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.ShoppingBag,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = Color.Gray.copy(alpha = 0.5f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "No orders yet",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                } else {
                    items(recentOrders) { order ->
                        RecentOrderCard(order, onClick = onNavigateToOrders)
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
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
        modifier = modifier.height(110.dp),
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
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color.copy(alpha = 0.7f),
                    modifier = Modifier.size(22.dp)
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
fun RecentOrderCard(order: RecentOrder, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = onClick
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
                    order.orderNumber,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    order.customerName,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Text(
                    SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
                        .format(Date(order.timestamp)),
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "₹${String.format("%.2f", order.totalAmount)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF0B8FAC)
                )
                Spacer(modifier = Modifier.height(4.dp))
                StatusBadge(order.status)
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (color, text) = when (status) {
        "PENDING" -> Color(0xFFFF9800) to "Pending"
        "PROCESSING" -> Color(0xFF2196F3) to "Processing"
        "SHIPPED" -> Color(0xFF9C27B0) to "Shipped"
        "DELIVERED" -> Color(0xFF4CAF50) to "Delivered"
        "CANCELLED" -> Color(0xFFF44336) to "Cancelled"
        else -> Color.Gray to status
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}