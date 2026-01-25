package com.example.mediqorog.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class NotificationSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NotificationSettingsScreen(
                onBackClick = { finish() }
            )
        }
    }
}

@Composable
fun NotificationSettingsScreen(onBackClick: () -> Unit) {
    val scrollState = rememberScrollState()

    var pushNotifications by remember { mutableStateOf(true) }
    var emailNotifications by remember { mutableStateOf(true) }
    var appointmentReminders by remember { mutableStateOf(true) }
    var medicationReminders by remember { mutableStateOf(true) }
    var healthTips by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFB))
            .verticalScroll(scrollState)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0B8FAC))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = "Notification Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section Header
        Text(
            text = "Manage Notifications",
            style = TextStyle(
                color = Color(0xFF0B8FAC),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            ),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        // Push Notifications
        NotificationToggleItem(
            title = "Push Notifications",
            description = "Receive push notifications",
            checked = pushNotifications,
            onCheckedChange = { pushNotifications = it }
        )

        // Email Notifications
        NotificationToggleItem(
            title = "Email Notifications",
            description = "Receive email updates",
            checked = emailNotifications,
            onCheckedChange = { emailNotifications = it }
        )

        // Appointment Reminders
        NotificationToggleItem(
            title = "Appointment Reminders",
            description = "Get notified about upcoming appointments",
            checked = appointmentReminders,
            onCheckedChange = { appointmentReminders = it }
        )

        // Medication Reminders
        NotificationToggleItem(
            title = "Medication Reminders",
            description = "Reminders to take your medications",
            checked = medicationReminders,
            onCheckedChange = { medicationReminders = it }
        )

        // Health Tips
        NotificationToggleItem(
            title = "Health Tips",
            description = "Receive daily health tips and advice",
            checked = healthTips,
            onCheckedChange = { healthTips = it }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun NotificationToggleItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                    style = TextStyle(
                        color = Color(0xFF2D3748),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF0B8FAC),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.Gray.copy(alpha = 0.5f)
                )
            )
        }
    }
}