// ========== OrderTrackingActivity.kt ==========
package com.example.mediqorog.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*

class OrderTrackingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OrderTrackingScreen(onBackClick = { finish() })
        }
    }
}

data class TrackingOrder(
    val id: String = "",
    val orderNumber: String = "",
    val status: String = "pending",
    val items: List<String> = emptyList(),
    val totalAmount: Double = 0.0,
    val trackingHistory: List<TrackingEvent> = emptyList()
)

data class TrackingEvent(
    val status: String = "",
    val timestamp: Long = 0,
    val description: String = ""
)

class OrderTrackingViewModel : ViewModel() {
    private val _orders = MutableStateFlow<List<TrackingOrder>>(emptyList())
    val orders: StateFlow<List<TrackingOrder>> = _orders

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    init {
        loadActiveOrders()
    }

    private fun loadActiveOrders() {
        _loading.value = true
        val userId = auth.currentUser?.uid

        if (userId != null) {
            firestore.collection("orders")
                .whereEqualTo("userId", userId)
                .addSnapshotListener { snapshot, _ ->
                    _orders.value = snapshot?.mapNotNull {
                        it.toObject(TrackingOrder::class.java)
                    }?.filter { it.status != "delivered" && it.status != "cancelled" }
                        ?: emptyList()
                    _loading.value = false
                }
        } else {
            _orders.value = getMockOrders()
            _loading.value = false
        }
    }

    private fun getMockOrders(): List<TrackingOrder> {
        return listOf(
            TrackingOrder(
                id = "ORD001",
                orderNumber = "MDQ2024001",
                status = "shipped",
                items = listOf("Paracetamol 500mg x2", "Vitamin C x1"),
                totalAmount = 245.0,
                trackingHistory = listOf(
                    TrackingEvent("pending", System.currentTimeMillis() - 3*24*60*60*1000, "Order placed successfully"),
                    TrackingEvent("packed", System.currentTimeMillis() - 2*24*60*60*1000, "Your order has been packed"),
                    TrackingEvent("shipped", System.currentTimeMillis() - 1*24*60*60*1000, "Out for delivery")
                )
            ),
            TrackingOrder(
                id = "ORD002",
                orderNumber = "MDQ2024002",
                status = "packed",
                items = listOf("Blood Pressure Monitor x1"),
                totalAmount = 1500.0,
                trackingHistory = listOf(
                    TrackingEvent("pending", System.currentTimeMillis() - 1*24*60*60*1000, "Order received"),
                    TrackingEvent("packed", System.currentTimeMillis() - 6*60*60*1000, "Packed and ready to ship")
                )
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(onBackClick: () -> Unit) {
    val viewModel: OrderTrackingViewModel = viewModel()
    val orders by viewModel.orders.collectAsState()
    val loading by viewModel.loading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Tracking", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0B8FAC),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (orders.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Filled.LocalShipping,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No active orders", fontSize = 18.sp, color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(orders) { order ->
                        OrderTrackingCard(order)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderTrackingCard(order: TrackingOrder) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
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
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${order.items.size} items • ₹${order.totalAmount}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }

                StatusChip(order.status)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress indicator
            OrderProgressBar(order.status)

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (expanded) "Hide Details" else "View Details")
                Icon(
                    if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null
                )
            }

            if (expanded) {
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                Text("Items:", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                order.items.forEach { item ->
                    Text("• $item", fontSize = 13.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Tracking History:", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))

                order.trackingHistory.forEach { event ->
                    TrackingEventItem(event)
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val (color, text) = when (status) {
        "pending" -> Color(0xFFFF9800) to "PENDING"
        "packed" -> Color(0xFF2196F3) to "PACKED"
        "shipped" -> Color(0xFF9C27B0) to "SHIPPED"
        "delivered" -> Color(0xFF4CAF50) to "DELIVERED"
        else -> Color.Gray to status.uppercase()
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun OrderProgressBar(status: String) {
    val stages = listOf("pending", "packed", "shipped", "delivered")
    val currentStage = stages.indexOf(status).coerceAtLeast(0)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        stages.forEachIndexed { index, stage ->
            val isCompleted = index <= currentStage
            val icon = when (stage) {
                "pending" -> Icons.Filled.ShoppingCart
                "packed" -> Icons.Filled.Inventory
                "shipped" -> Icons.Filled.LocalShipping
                "delivered" -> Icons.Filled.CheckCircle
                else -> Icons.Filled.Circle
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isCompleted) Color(0xFF0B8FAC) else Color.LightGray,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        icon,
                        contentDescription = stage,
                        tint = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    stage.capitalize(Locale.ROOT),
                    fontSize = 11.sp,
                    color = if (isCompleted) Color.Black else Color.Gray
                )
            }

            if (index < stages.size - 1) {
                Canvas(
                    modifier = Modifier
                        .weight(0.5f)
                        .height(2.dp)
                        .offset(y = (-20).dp)
                ) {
                    drawLine(
                        color = if (index < currentStage) Color(0xFF0B8FAC) else Color.LightGray,
                        start = Offset(0f, size.height / 2),
                        end = Offset(size.width, size.height / 2),
                        strokeWidth = 4f
                    )
                }
            }
        }
    }
}

@Composable
fun TrackingEventItem(event: TrackingEvent) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(16.dp)
        )
        Column {
            Text(
                event.description,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(event.timestamp)),
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}