package com.example.mediqorog.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mediqorog.model.Order
import com.example.mediqorog.model.OrderStatus
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ModernOrderCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Order #${order.orderNumber}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(order.date),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                ModernStatusBadge(order.status)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Order details section
                Column {
                    Text(
                        text = "Total Amount",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "₹${String.format("%.2f", order.totalAmount)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF0B8FAC)
                    )
                }

                // Action buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { /* View details */ },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Visibility,
                            contentDescription = "View",
                            tint = Color(0xFF0B8FAC)
                        )
                    }

                    if (order.status == OrderStatus.PENDING) {
                        IconButton(
                            onClick = { /* Process order */ },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Process",
                                tint = Color(0xFF4CAF50)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModernStatusBadge(status: OrderStatus) {
    val (color, bgColor) = when (status) {
        OrderStatus.PENDING -> Color(0xFFFF9800) to Color(0xFFFFF3E0)
        OrderStatus.PROCESSING -> Color(0xFF2196F3) to Color(0xFFE3F2FD)
        OrderStatus.SHIPPED -> Color(0xFF9C27B0) to Color(0xFFF3E5F5)
        OrderStatus.DELIVERED -> Color(0xFF4CAF50) to Color(0xFFE8F5E9)
        OrderStatus.CANCELLED -> Color(0xFFF44336) to Color(0xFFFFEBEE)
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor
    ) {
        Text(
            status.toDisplayString(),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}