//package com.example.mediqorog.view
//
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.widget.Toast
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material.icons.outlined.*
//import androidx.compose.material3.*
//import androidx.compose.material3.pulltorefresh.PullToRefreshBox
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.media3.exoplayer.offline.Download
//import com.example.mediqorog.model.Order
//import com.example.mediqorog.model.OrderStatusUI
//import com.example.mediqorog.viewmodel.OrderViewModel
//import java.text.SimpleDateFormat
//import java.util.*
//
//class MyOrdersActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            MyOrdersScreen()
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MyOrdersScreen(viewModel: OrderViewModel = viewModel()) {
//    val context = LocalContext.current
//    val uiState by viewModel.uiState.collectAsState()
//    var showSearchBar by remember { mutableStateOf(false) }
//    var selectedOrder by remember { mutableStateOf<Order?>(null) }
//
//    // Show error toast
//    LaunchedEffect(uiState.error) {
//        uiState.error?.let { error ->
//            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
//            viewModel.clearError()
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    if (showSearchBar) {
//                        TextField(
//                            value = uiState.searchQuery,
//                            onValueChange = { viewModel.updateSearchQuery(it) },
//                            placeholder = { Text("Search orders...") },
//                            modifier = Modifier.fillMaxWidth(),
//                            colors = TextFieldDefaults.colors(
//                                focusedContainerColor = Color.Transparent,
//                                unfocusedContainerColor = Color.Transparent,
//                                focusedIndicatorColor = Color.Transparent,
//                                unfocusedIndicatorColor = Color.Transparent
//                            ),
//                            singleLine = true
//                        )
//                    } else {
//                        Text("My Orders")
//                    }
//                },
//                navigationIcon = {
//                    IconButton(onClick = {
//                        if (showSearchBar) {
//                            showSearchBar = false
//                            viewModel.clearSearch()
//                        } else {
//                            (context as ComponentActivity).finish()
//                        }
//                    }) {
//                        Icon(Icons.Default.ArrowBack, "Back")
//                    }
//                },
//                actions = {
//                    IconButton(onClick = { showSearchBar = !showSearchBar }) {
//                        Icon(
//                            if (showSearchBar) Icons.Default.Close else Icons.Default.Search,
//                            "Search"
//                        )
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = Color(0xFF0B8FAC),
//                    titleContentColor = Color.White,
//                    navigationIconContentColor = Color.White,
//                    actionIconContentColor = Color.White
//                )
//            )
//        }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding)
//                .background(Color(0xFFF5F5F5))
//        ) {
//            // Tab Row
//            TabRow(
//                selectedTabIndex = uiState.selectedTab,
//                containerColor = Color.White,
//                contentColor = Color(0xFF0B8FAC)
//            ) {
//                Tab(
//                    selected = uiState.selectedTab == 0,
//                    onClick = { viewModel.selectTab(0) },
//                    text = { Text("All") }
//                )
//                Tab(
//                    selected = uiState.selectedTab == 1,
//                    onClick = { viewModel.selectTab(1) },
//                    text = { Text("Pending") }
//                )
//                Tab(
//                    selected = uiState.selectedTab == 2,
//                    onClick = { viewModel.selectTab(2) },
//                    text = { Text("Completed") }
//                )
//                Tab(
//                    selected = uiState.selectedTab == 3,
//                    onClick = { viewModel.selectTab(3) },
//                    text = { Text("Cancelled") }
//                )
//            }
//
//            // Pull to refresh
//            PullToRefreshBox(
//                isRefreshing = uiState.isLoading,
//                onRefresh = { viewModel.refreshOrders() },
//                modifier = Modifier.fillMaxSize()
//            ) {
//                if (uiState.isLoading && uiState.orders.isEmpty()) {
//                    // Loading state
//                    Box(
//                        modifier = Modifier.fillMaxSize(),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        CircularProgressIndicator(color = Color(0xFF0B8FAC))
//                    }
//                } else if (uiState.filteredOrders.isEmpty()) {
//                    // Empty State
//                    Column(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(32.dp),
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        verticalArrangement = Arrangement.Center
//                    ) {
//                        Icon(
//                            Icons.Outlined.ShoppingCart,
//                            contentDescription = "No orders",
//                            modifier = Modifier.size(120.dp),
//                            tint = Color.Gray
//                        )
//                        Spacer(modifier = Modifier.height(16.dp))
//                        Text(
//                            "No orders found",
//                            style = MaterialTheme.typography.titleLarge,
//                            fontWeight = FontWeight.Bold,
//                            color = Color.Gray
//                        )
//                        Text(
//                            if (uiState.searchQuery.isNotBlank())
//                                "Try different search terms"
//                            else
//                                "Start shopping to see your orders here",
//                            style = MaterialTheme.typography.bodyMedium,
//                            color = Color.Gray
//                        )
//                    }
//                } else {
//                    // Orders List
//                    LazyColumn(
//                        contentPadding = PaddingValues(16.dp),
//                        verticalArrangement = Arrangement.spacedBy(12.dp)
//                    ) {
//                        items(uiState.filteredOrders, key = { it.id }) { order ->
//                            OrderCard(
//                                order = order,
//                                onClick = { selectedOrder = order },
//                                onReorder = {
//                                    viewModel.reorder(order) { success, message ->
//                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
//                                    }
//                                },
//                                onCancel = {
//                                    viewModel.cancelOrder(order.id) { success, message ->
//                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
//                                    }
//                                }
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    // Order Details Dialog
//    selectedOrder?.let { order ->
//        OrderDetailsDialog(
//            order = order,
//            onDismiss = { selectedOrder = null },
//            onReorder = {
//                viewModel.reorder(order) { success, message ->
//                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
//                    if (success) selectedOrder = null
//                }
//            }
//        )
//    }
//}
//
//@Composable
//fun OrderCard(
//    order: Order,
//    onClick: () -> Unit,
//    onReorder: () -> Unit,
//    onCancel: () -> Unit
//) {
//    val dateFormat = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
//    val context = LocalContext.current
//    val statusUI = OrderStatusUI.from(order.status)
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable(onClick = onClick),
//        colors = CardDefaults.cardColors(containerColor = Color.White),
//        shape = RoundedCornerShape(12.dp),
//        elevation = CardDefaults.cardElevation(2.dp)
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp)
//        ) {
//            // Header
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Column {
//                    Text(
//                        text = order.orderNumber,
//                        style = MaterialTheme.typography.titleMedium,
//                        fontWeight = FontWeight.Bold
//                    )
//                    Text(
//                        text = dateFormat.format(order.date),
//                        style = MaterialTheme.typography.bodySmall,
//                        color = Color.Gray
//                    )
//                }
//
//                Surface(
//                    shape = RoundedCornerShape(20.dp),
//                    color = statusUI.backgroundColor
//                ) {
//                    Row(
//                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Icon(
//                            imageVector = statusUI.icon,
//                            contentDescription = null,
//                            modifier = Modifier.size(16.dp),
//                            tint = statusUI.textColor
//                        )
//                        Spacer(modifier = Modifier.width(4.dp))
//                        Text(
//                            text = statusUI.text,
//                            style = MaterialTheme.typography.bodySmall,
//                            color = statusUI.textColor,
//                            fontWeight = FontWeight.Bold
//                        )
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//            HorizontalDivider()
//            Spacer(modifier = Modifier.height(12.dp))
//
//            // Items
//            order.items.take(2).forEachIndexed { index, item ->
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Surface(
//                        modifier = Modifier
//                            .size(60.dp)
//                            .clip(RoundedCornerShape(8.dp)),
//                        color = Color(0xFFF0F0F0)
//                    ) {
//                        Icon(
//                            Icons.Outlined.ShoppingCart,
//                            contentDescription = null,
//                            modifier = Modifier.padding(12.dp),
//                            tint = Color.Gray
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.width(12.dp))
//
//                    Column(modifier = Modifier.weight(1f)) {
//                        Text(
//                            text = item.name,
//                            style = MaterialTheme.typography.bodyLarge,
//                            fontWeight = FontWeight.Medium
//                        )
//                        Text(
//                            text = "Qty: ${item.quantity}",
//                            style = MaterialTheme.typography.bodySmall,
//                            color = Color.Gray
//                        )
//                    }
//
//                    Text(
//                        text = "Rs ${item.price * item.quantity}",
//                        style = MaterialTheme.typography.bodyLarge,
//                        fontWeight = FontWeight.Bold,
//                        color = Color(0xFF0B8FAC)
//                    )
//                }
//
//                if (index < 1 && order.items.size > 1) {
//                    Spacer(modifier = Modifier.height(8.dp))
//                }
//            }
//
//            if (order.items.size > 2) {
//                Spacer(modifier = Modifier.height(8.dp))
//                Text(
//                    "+ ${order.items.size - 2} more items",
//                    style = MaterialTheme.typography.bodySmall,
//                    color = Color.Gray
//                )
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//            HorizontalDivider()
//            Spacer(modifier = Modifier.height(12.dp))
//
//            // Total & Actions
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Column {
//                    Text(
//                        "Total Amount",
//                        style = MaterialTheme.typography.bodySmall,
//                        color = Color.Gray
//                    )
//                    Text(
//                        "Rs ${order.totalAmount}",
//                        style = MaterialTheme.typography.titleLarge,
//                        fontWeight = FontWeight.Bold,
//                        color = Color(0xFF0B8FAC)
//                    )
//                }
//
//                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                    when (order.status) {
//                        com.example.mediqorog.model.OrderStatus.PENDING,
//                        com.example.mediqorog.model.OrderStatus.PROCESSING -> {
//                            OutlinedButton(
//                                onClick = onCancel,
//                                colors = ButtonDefaults.outlinedButtonColors(
//                                    contentColor = Color(0xFFF44336)
//                                )
//                            ) {
//                                Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp))
//                                Spacer(modifier = Modifier.width(4.dp))
//                                Text("Cancel")
//                            }
//                        }
//                        com.example.mediqorog.model.OrderStatus.SHIPPED -> {
//                            OutlinedButton(
//                                onClick = {
//                                    Toast.makeText(context, "Tracking info coming soon", Toast.LENGTH_SHORT).show()
//                                },
//                                colors = ButtonDefaults.outlinedButtonColors(
//                                    contentColor = Color(0xFF0B8FAC)
//                                )
//                            ) {
//                                Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(16.dp))
//                                Spacer(modifier = Modifier.width(4.dp))
//                                Text("Track")
//                            }
//                        }
//                        com.example.mediqorog.model.OrderStatus.DELIVERED -> {
//                            OutlinedButton(
//                                onClick = onReorder,
//                                colors = ButtonDefaults.outlinedButtonColors(
//                                    contentColor = Color(0xFF0B8FAC)
//                                )
//                            ) {
//                                Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp))
//                                Spacer(modifier = Modifier.width(4.dp))
//                                Text("Reorder")
//                            }
//                        }
//                        com.example.mediqorog.model.OrderStatus.CANCELLED -> {
//                            OutlinedButton(
//                                onClick = {
//                                    val intent = Intent(Intent.ACTION_DIAL).apply {
//                                        data = Uri.parse("tel:9800000000")
//                                    }
//                                    context.startActivity(intent)
//                                },
//                                colors = ButtonDefaults.outlinedButtonColors(
//                                    contentColor = Color.Gray
//                                )
//                            ) {
//                                Icon(Icons.Default.Phone, null, modifier = Modifier.size(16.dp))
//                                Spacer(modifier = Modifier.width(4.dp))
//                                Text("Help")
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun OrderDetailsDialog(
//    order: Order,
//    onDismiss: () -> Unit,
//    onReorder: () -> Unit
//) {
//    val dateFormat = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
//    val context = LocalContext.current
//
//    AlertDialog(
//        onDismissRequest = onDismiss,
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        Card(
//            colors = CardDefaults.cardColors(containerColor = Color.White),
//            shape = RoundedCornerShape(16.dp)
//        ) {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(24.dp)
//            ) {
//                // Header
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        "Order Details",
//                        style = MaterialTheme.typography.titleLarge,
//                        fontWeight = FontWeight.Bold
//                    )
//                    IconButton(onClick = onDismiss) {
//                        Icon(Icons.Default.Close, "Close")
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                // Order Info
//                DetailRow("Order Number", order.orderNumber)
//                DetailRow("Order Date", dateFormat.format(order.date))
//                DetailRow("Status", order.status.toDisplayString())
//                DetailRow("Payment Method", order.paymentMethod)
//                DetailRow("Delivery Address", order.deliveryAddress)
//
//                Spacer(modifier = Modifier.height(16.dp))
//                Text(
//                    "Items (${order.items.size})",
//                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.Bold
//                )
//                Spacer(modifier = Modifier.height(8.dp))
//
//                order.items.forEach { item ->
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(vertical = 4.dp),
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        Text(
//                            "${item.name} x${item.quantity}",
//                            modifier = Modifier.weight(1f)
//                        )
//                        Text(
//                            "Rs ${item.price * item.quantity}",
//                            fontWeight = FontWeight.Bold
//                        )
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(12.dp))
//                HorizontalDivider()
//                Spacer(modifier = Modifier.height(12.dp))
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    Text(
//                        "Total Amount",
//                        style = MaterialTheme.typography.titleMedium,
//                        fontWeight = FontWeight.Bold
//                    )
//                    Text(
//                        "Rs ${order.totalAmount}",
//                        style = MaterialTheme.typography.titleMedium,
//                        fontWeight = FontWeight.Bold,
//                        color = Color(0xFF0B8FAC)
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                // Actions
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    OutlinedButton(
//                        onClick = {
//                            Toast.makeText(context, "Downloading invoice...", Toast.LENGTH_SHORT).show()
//                        },
//                        modifier = Modifier.weight(1f)
//                    ) {
//                        Icon(Icons.Filled.KeyboardArrowDown, null, modifier = Modifier.size(16.dp))
//                        Spacer(modifier = Modifier.width(4.dp))
//                        Text("Invoice")
//                    }
//
//                    Button(
//                        onClick = {
//                            val intent = Intent(Intent.ACTION_DIAL).apply {
//                                data = Uri.parse("tel:9800000000")
//                            }
//                            context.startActivity(intent)
//                        },
//                        modifier = Modifier.weight(1f),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Color(0xFF0B8FAC)
//                        )
//                    ) {
//                        Icon(Icons.Default.Phone, null, modifier = Modifier.size(16.dp))
//                        Spacer(modifier = Modifier.width(4.dp))
//                        Text("Support")
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun DetailRow(label: String, value: String) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 4.dp),
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//        Text(
//            label,
//            style = MaterialTheme.typography.bodyMedium,
//            color = Color.Gray,
//            modifier = Modifier.weight(1f)
//        )
//        Text(
//            value,
//            style = MaterialTheme.typography.bodyMedium,
//            fontWeight = FontWeight.Medium,
//            modifier = Modifier.weight(1.5f)
//        )
//    }
//}