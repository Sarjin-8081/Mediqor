package com.example.mediqorog.view.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mediqorog.components.*
import com.example.mediqorog.model.Order
import com.example.mediqorog.model.OrderStatus
import com.example.mediqorog.viewmodel.AdminOrdersViewModel
import com.example.mediqorog.utils.DateUtils
import com.example.mediqorog.utils.NumberUtils

@Composable
fun AdminOrdersScreenContent() {
    val context = LocalContext.current
    val viewModel: AdminOrdersViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.error, uiState.successMessage) {
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
        uiState.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
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

                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = { viewModel.searchOrders(it) },
                    placeholder = "Search orders..."
                )

                Spacer(modifier = Modifier.height(12.dp))

                FilterDropdown(
                    selectedValue = uiState.selectedStatus,
                    options = listOf("All", "PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"),
                    onValueChange = { viewModel.filterByStatus(it) },
                    label = "Status"
                )
            }
        }

        if (uiState.isLoading) {
            LoadingIndicator()
        } else if (uiState.filteredOrders.isEmpty()) {
            EmptyState(
                icon = Icons.Default.ShoppingCart,
                message = "No orders found"
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.filteredOrders) { order ->
                    var showDialog by remember { mutableStateOf(false) }

                    OrderCard(
                        order = order,
                        onClick = { showDialog = true }
                    )

                    if (showDialog) {
                        OrderDetailsDialog(
                            order = order,
                            onDismiss = { showDialog = false },
                            onUpdateStatus = { newStatus ->
                                viewModel.updateOrderStatus(order.id, newStatus)
                                showDialog = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderDetailsDialog(
    order: Order,
    onDismiss: () -> Unit,
    onUpdateStatus: (OrderStatus) -> Unit
) {
    var selectedStatus by remember { mutableStateOf(order.status) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Order #${order.orderNumber}") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("User ID: ${order.userId}", fontWeight = FontWeight.Bold)
                Text("Date: ${DateUtils.formatDateTime(order.date.time)}")
                Text("Items: ${order.items.size}")
                Text("Total: ${NumberUtils.formatCurrencyWithDecimals(order.totalAmount)}")
                Text("Address: ${order.deliveryAddress}")
                Text("Payment: ${order.paymentMethod}")

                HorizontalDivider()

                Text("Update Status:", fontWeight = FontWeight.Bold)

                OrderStatus.values().forEach { status ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedStatus == status,
                            onClick = { selectedStatus = status }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(status.toDisplayString())
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onUpdateStatus(selectedStatus) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0B8FAC)
                )
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}