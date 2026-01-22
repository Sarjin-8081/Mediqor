// ============================================================
// FILE: AdminOrdersScreen.kt
// ============================================================
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.mediqorog.model.Order
import com.example.mediqorog.model.OrderStatus
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminOrdersScreen(navController: NavHostController) {
    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var selectedFilter by remember { mutableStateOf("All") }
    var showUpdateDialog by remember { mutableStateOf<Order?>(null) }

    val db = FirebaseFirestore.getInstance()

    // Load orders
    LaunchedEffect(Unit) {
        db.collection("orders")
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let {
                    orders = it.toObjects(Order::class.java)
                        .sortedByDescending { order -> order.date }
                }
            }
    }

    // Filter orders
    val filteredOrders = remember(orders, selectedFilter) {
        when (selectedFilter) {
            "All" -> orders
            "Pending" -> orders.filter { it.status == OrderStatus.PENDING }
            "Processing" -> orders.filter { it.status == OrderStatus.PROCESSING }
            "Shipped" -> orders.filter { it.status == OrderStatus.SHIPPED }
            "Delivered" -> orders.filter { it.status == OrderStatus.DELIVERED }
            else -> orders
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Orders Management",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B8FAC)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Filter chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == "All",
                        onClick = { selectedFilter = "All" },
                        label = { Text("All (${orders.size})") }
                    )
                    FilterChip(
                        selected = selectedFilter == "Pending",
                        onClick = { selectedFilter = "Pending" },
                        label = { Text("Pending (${orders.count { it.status == OrderStatus.PENDING }})") }
                    )
                    FilterChip(
                        selected = selectedFilter == "Shipped",
                        onClick = { selectedFilter = "Shipped" },
                        label = { Text("Shipped") }
                    )
                }
            }
        }

        // Orders list
        if (filteredOrders.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No orders found",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredOrders) { order ->
                    OrderCard(
                        order = order,
                        onUpdateClick = { showUpdateDialog = order }
                    )
                }
            }
        }
    }

    // Update status dialog
    showUpdateDialog?.let { order ->
        UpdateOrderDialog(
            order = order,
            onDismiss = { showUpdateDialog = null },
            onUpdate = { newStatus ->
                db.collection("orders").document(order.id)
                    .update("status", newStatus.name)
                    .addOnSuccessListener {
                        showUpdateDialog = null
                    }
            }
        )
    }
}

@Composable
fun OrderCard(order: Order, onUpdateClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Order #${order.orderNumber}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(order.date),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                OrderStatusBadge(order.status)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Items: ${order.items.size}", fontSize = 14.sp, color = Color.Gray)
                    Text(
                        "Total: ₹${String.format("%.2f", order.totalAmount)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0B8FAC)
                    )
                }

                Button(
                    onClick = onUpdateClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0B8FAC)
                    )
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Update")
                }
            }
        }
    }
}

@Composable
fun OrderStatusBadge(status: OrderStatus) {
    val (backgroundColor, textColor) = when (status) {
        OrderStatus.PENDING -> Color(0xFFFFF3E0) to Color(0xFFFF9800)
        OrderStatus.PROCESSING -> Color(0xFFE3F2FD) to Color(0xFF2196F3)
        OrderStatus.SHIPPED -> Color(0xFFF3E5F5) to Color(0xFF9C27B0)
        OrderStatus.DELIVERED -> Color(0xFFE8F5E9) to Color(0xFF4CAF50)
        OrderStatus.CANCELLED -> Color(0xFFFFEBEE) to Color(0xFFF44336)
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor
    ) {
        Text(
            text = status.name,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
fun UpdateOrderDialog(
    order: Order,
    onDismiss: () -> Unit,
    onUpdate: (OrderStatus) -> Unit
) {
    var selectedStatus by remember { mutableStateOf(order.status) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Update Order Status",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Column {
                Text(
                    "Order #${order.orderNumber}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF0B8FAC)
                )
                Text(
                    "Customer: ${order.userId}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Select new status:",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                OrderStatus.values().forEach { status ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedStatus == status,
                            onClick = { selectedStatus = status }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = status.name,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onUpdate(selectedStatus)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0B8FAC)
                )
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}