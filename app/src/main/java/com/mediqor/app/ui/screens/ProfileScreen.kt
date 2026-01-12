package com.mediqor.app.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // 🔹 User Info
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {}

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "User Name",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "user@email.com",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Menu Items (FIXED ICONS)
        ProfileItem(Icons.Outlined.ShoppingCart, "My Orders")
        ProfileItem(Icons.Outlined.LocationOn, "Saved Addresses")
        ProfileItem(Icons.Outlined.Notifications, "Prescriptions")
        ProfileItem(Icons.Outlined.Settings, "Settings")
        ProfileItem(Icons.Outlined.Info, "Help & Support")

        Spacer(modifier = Modifier.height(32.dp))

        // 🔹 Logout
        Button(
            onClick = { /* later */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Logout")
        }
    }
}

@Composable
private fun ProfileItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = title)
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, style = MaterialTheme.typography.bodyLarge)
    }
}