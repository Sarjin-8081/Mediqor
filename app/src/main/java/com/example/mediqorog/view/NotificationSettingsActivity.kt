package com.example.mediqorog.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
    var reminders by remember { mutableStateOf(true) }
    var emailNotif by remember { mutableStateOf(false) }
    var smsNotif by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notification Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8FAFB))
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                "Push Notifications",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3748),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            NotificationSettingItem(
                title = "Order Updates",
                subtitle = "Get notified about order status",
                checked = orderUpdates,
                onCheckedChange = { orderUpdates = it }
            )

            NotificationSettingItem(
                title = "Promotions & Offers",
                subtitle = "Receive special deals and discounts",
                checked = promotions,
                onCheckedChange = { promotions = it }
            )

            NotificationSettingItem(
                title = "Medicine Reminders",
                subtitle = "Never miss your medication time",
                checked = reminders,
                onCheckedChange = { reminders = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Other Channels",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3748),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            NotificationSettingItem(
                title = "Email Notifications",
                subtitle = "Receive updates via email",
                checked = emailNotif,
                onCheckedChange = { emailNotif = it }
            )

            NotificationSettingItem(
                title = "SMS Notifications",
                subtitle = "Get text message alerts",
                checked = smsNotif,
                onCheckedChange = { smsNotif = it }
            )
        }
    }
}

@Composable
fun NotificationSettingItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF2D3748))
                Spacer(modifier = Modifier.height(4.dp))
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF0B8FAC),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.Gray
                )
            )
        }
    }
}