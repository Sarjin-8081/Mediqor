package com.example.mediqorog.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class NotificationSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                NotificationSettingsScreen(onNavigateBack = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(onNavigateBack: () -> Unit) {
    var orderUpdates by remember { mutableStateOf(true) }
    var promotions by remember { mutableStateOf(true) }
    var medicineReminders by remember { mutableStateOf(true) }
    var prescriptionAlerts by remember { mutableStateOf(true) }
    var deliveryUpdates by remember { mutableStateOf(true) }
    var newOffers by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notification Settings", fontWeight = FontWeight.SemiBold) },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Manage your notification preferences",
                            fontSize = 14.sp,
                            color = Color(0xFF1E40AF),
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Order Notifications",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
            }

            item {
                NotificationToggleCard(
                    title = "Order Updates",
                    description = "Get notified about your order status",
                    checked = orderUpdates,
                    onCheckedChange = { orderUpdates = it }
                )
            }

            item {
                NotificationToggleCard(
                    title = "Delivery Updates",
                    description = "Real-time delivery tracking notifications",
                    checked = deliveryUpdates,
                    onCheckedChange = { deliveryUpdates = it }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Health Notifications",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
            }

            item {
                NotificationToggleCard(
                    title = "Medicine Reminders",
                    description = "Never miss your medication time",
                    checked = medicineReminders,
                    onCheckedChange = { medicineReminders = it }
                )
            }

            item {
                NotificationToggleCard(
                    title = "Prescription Alerts",
                    description = "Reminders to refill prescriptions",
                    checked = prescriptionAlerts,
                    onCheckedChange = { prescriptionAlerts = it }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Marketing",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
            }

            item {
                NotificationToggleCard(
                    title = "Promotions",
                    description = "Special deals and discount offers",
                    checked = promotions,
                    onCheckedChange = { promotions = it }
                )
            }

            item {
                NotificationToggleCard(
                    title = "New Offers",
                    description = "Latest offers and product launches",
                    checked = newOffers,
                    onCheckedChange = { newOffers = it }
                )
            }
        }
    }
}

@Composable
fun NotificationToggleCard(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}