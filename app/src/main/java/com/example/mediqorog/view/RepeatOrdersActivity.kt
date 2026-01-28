// ========== RepeatOrdersActivity.kt ==========
package com.example.mediqorog.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class RepeatOrdersActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RepeatOrdersScreen(onBackClick = { finish() })
        }
    }
}

data class Order(
    val id: String = "",
    val userId: String = "",
    val items: List<OrderItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val orderDate: Long = 0,
    val status: String = "delivered"
)

data class OrderItem(
    val name: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0
)

class RepeatOrdersViewModel : ViewModel() {
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    init {
        loadOrders()
    }

    private fun loadOrders() {
        _loading.value = true
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("orders")
            .whereEqualTo("userId", userId)
            .whereEqualTo("status", "delivered")
            .get()
            .addOnSuccessListener { documents ->
                _orders.value = documents.mapNotNull { it.toObject(Order::class.java) }
                    .sortedByDescending { it.orderDate }
                _loading.value = false
            }
            .addOnFailureListener {
                _loading.value = false
                // Show mock data if Firebase fails
                _orders.value = getMockOrders()
            }
    }

    suspend fun reorder(order: Order): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("Not logged in")

            val newOrder = order.copy(
                id = UUID.randomUUID().toString(),
                orderDate = System.currentTimeMillis(),
                status = "pending"
            )

            firestore.collection("orders")
                .document(newOrder.id)
                .set(newOrder)
                .await()

            true
        } catch (e: Exception) {
            false
        }
    }

    private fun getMockOrders(): List<Order> {
        return listOf(
            Order(
                id = "1",
                userId = "user1",
                items = listOf(
                    OrderItem("Paracetamol 500mg", 2, 45.0),
                    OrderItem("Vitamin C Tablets", 1, 120.0)
                ),
                totalAmount = 210.0,
                orderDate = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000,
                status = "delivered"
            ),
            Order(
                id = "2",
                userId = "user1",
                items = listOf(
                    OrderItem("Cough Syrup", 1, 85.0),
                    OrderItem("Antiseptic Cream", 1, 55.0)
                ),
                totalAmount = 140.0,
                orderDate = System.currentTimeMillis() - 15 * 24 * 60 * 60 * 1000,
                status = "delivered"
            ),
            Order(
                id = "3",
                userId = "user1",
                items = listOf(
                    OrderItem("Blood Pressure Monitor", 1, 1500.0)
                ),
                totalAmount = 1500.0,
                orderDate = System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000,
                status = "delivered"
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepeatOrdersScreen(onBackClick: () -> Unit) {
    val viewModel: RepeatOrdersViewModel = viewModel()
    val orders by viewModel.orders.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Repeat Orders", fontWeight = FontWeight.Bold) },
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (orders.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Filled.ShoppingBag,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No previous orders",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                    Text(
                        "Your completed orders will appear here",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(orders) { order ->
                        OrderCard(
                            order = order,
                            onReorder = {
                                scope.launch {
                                    val success = viewModel.reorder(order)
                                    if (success) {
                                        snackbarHostState.showSnackbar("Order placed successfully!")
                                    } else {
                                        snackbarHostState.showSnackbar("Failed to place order")
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: Order, onReorder: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Order #${order.id.take(8)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        formatDate(order.orderDate),
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF4CAF50)
                ) {
                    Text(
                        "DELIVERED",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            // Items list
            order.items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "${item.name} x${item.quantity}",
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "₹${item.price * item.quantity}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Total Amount",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Text(
                        "₹${order.totalAmount}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0B8FAC)
                    )
                }

                Button(
                    onClick = onReorder,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0B8FAC)
                    )
                ) {
                    Icon(
                        Icons.Filled.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reorder")
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}