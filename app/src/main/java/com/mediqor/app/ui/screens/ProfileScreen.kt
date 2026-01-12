package com.mediqor.app.ui.screens.profile

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.mediqor.app.ui.view.LoginActivity
import com.mediqor.app.ui.view.MyOrdersActivity
import com.mediqor.app.ui.view.SavedAddressesActivity
import com.mediqor.app.ui.view.PrescriptionsActivity
import com.mediqor.app.ui.view.SettingsActivity
import com.mediqor.app.ui.view.HelpSupportActivity

@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    var showLogoutDialog by remember { mutableStateOf(false) }

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
            ) {
                // You can add user profile image here later
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = currentUser?.displayName?.firstOrNull()?.uppercase() ?: "U",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = currentUser?.displayName ?: "User Name",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = currentUser?.email ?: "user@email.com",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Menu Items with Click Actions
        ProfileItem(
            icon = Icons.Outlined.ShoppingCart,
            title = "My Orders",
            onClick = {
                val intent = Intent(context, MyOrdersActivity::class.java)
                context.startActivity(intent)
            }
        )

        ProfileItem(
            icon = Icons.Outlined.LocationOn,
            title = "Saved Addresses",
            onClick = {
                val intent = Intent(context, SavedAddressesActivity::class.java)
                context.startActivity(intent)
            }
        )

        ProfileItem(
            icon = Icons.Outlined.Notifications,
            title = "Prescriptions",
            onClick = {
                val intent = Intent(context, PrescriptionsActivity::class.java)
                context.startActivity(intent)
            }
        )

        ProfileItem(
            icon = Icons.Outlined.Settings,
            title = "Settings",
            onClick = {
                val intent = Intent(context, SettingsActivity::class.java)
                context.startActivity(intent)
            }
        )

        ProfileItem(
            icon = Icons.Outlined.Info,
            title = "Help & Support",
            onClick = {
                val intent = Intent(context, HelpSupportActivity::class.java)
                context.startActivity(intent)
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 🔹 Logout Button
        Button(
            onClick = { showLogoutDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Logout")
        }
    }

    // 🔹 Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Sign out from Firebase
                        auth.signOut()

                        // Navigate to Login and clear back stack
                        val intent = Intent(context, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    }
                ) {
                    Text("Yes", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ProfileItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Outlined.KeyboardArrowRight,
            contentDescription = "Navigate",
            tint = Color.Gray
        )
    }
}