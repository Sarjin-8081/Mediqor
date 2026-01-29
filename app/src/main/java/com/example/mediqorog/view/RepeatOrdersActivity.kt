package com.example.mediqorog.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class RepeatOrdersActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                RepeatOrdersScreen(onNavigateBack = { finish() })
            }
        }
    }
}

data class PastOrder(
    val orderId: String,
    val date: String,
    val items: List<OrderItem>,
    val total: Double
)

data class OrderItem(
    val name: String,
    val quantity: Int,
    val price: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepeatOrdersScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current

    val pastOrders = listOf(
        PastOrder(
            orderId = "ORD-2401-567",
            date = "Jan 15, 2026",
            items = listOf(
                OrderItem("Paracetamol 500mg (10 tablets)", 2, 45.0),
                OrderItem("Vitamin D3 1000IU (30 capsules)", 1, 320.0),
                OrderItem("Cough Syrup 100ml", 1, 125.0)
            ),
            total = 535.0
        ),
        PastOrder(
            orderId = "ORD-2312-892",
            date = "Dec 28, 2025",
            items = listOf(
                OrderItem("Aspirin 75mg (30 tablets)", 1, 85.0),
                OrderItem("Omega-3 Fish Oil (60 capsules)", 1, 650.0)
            ),
            total = 735.0
        ),
        PastOrder(
            orderId = "ORD-2312-445",
            date = "Dec 10, 2025",
            items = listOf(
                OrderItem("Multivitamin Tablets (30 tablets)", 2, 480.0),
                OrderItem("Antacid Syrup 200ml", 1, 95.0),
                OrderItem("Pain Relief Gel 30g", 1, 145.0)
            ),
            total = 1200.0
        ),
        PastOrder(
            orderId = "ORD-2311-223",
            date = "Nov 22, 2025",
            items = listOf(
                OrderItem("Blood Pressure Monitor", 1, 1850.0),
                OrderItem("Diabetes Test Strips (50 strips)", 1, 890.0)
            ),
            total = 2740.0
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Repeat Orders", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F7FA)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Info",
                            tint = Color(0xFF10B981)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Reorder your past purchases with one click!",
                            fontSize = 13.sp,
                            color = Color(0xFF065F46)
                        )
                    }
                }
            }

            items(pastOrders) { order ->
                PastOrderCard(order = order, onReorder = {
                    Toast.makeText(
                        context,
                        "Added to cart: ${order.items.size} items",
                        Toast.LENGTH_SHORT
                    ).show()
                })
            }
        }
    }
}

@Composable
fun PastOrderCard(order: PastOrder, onReorder: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = order.orderId,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Date",
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFF6B7280)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = order.date,
                            fontSize = 13.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }

                Text(
                    text = "₹${order.total}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "${order.items.size} items",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6B7280)
            )

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Color(0xFFE5E7EB))
                Spacer(modifier = Modifier.height(12.dp))

                order.items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.name,
                                fontSize = 13.sp,
                                color = Color(0xFF1F2937)
                            )
                            Text(
                                text = "Qty: ${item.quantity}",
                                fontSize = 12.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                        Text(
                            text = "₹${item.price * item.quantity}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1F2937)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (expanded) "Show Less" else "View Items")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Button(
                    onClick = onReorder,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reorder",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reorder")
                }
            }
        }
    }
}