package com.example.mediqorog.view.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mediqorog.view.*
import com.example.mediqorog.viewmodel.UserViewModel
import com.example.mediqorog.repository.UserRepoImpl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    val viewModel: UserViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return UserViewModel(UserRepoImpl()) as T
            }
        }
    )
    val currentUser by viewModel.currentUser.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Profile",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0B8FAC),
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Profile Header
            ProfileHeader(
                displayName = currentUser?.displayName ?: "Guest User",
                email = currentUser?.email ?: "Not logged in",
                photoUrl = currentUser?.photoUrl
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Menu Items
            ProfileMenuItem(
                icon = Icons.Filled.ShoppingCart,
                title = "My Orders",
                subtitle = "Track and manage your orders",
                onClick = {
                    context.startActivity(Intent(context, MyOrdersActivity::class.java))
                }
            )

            ProfileMenuItem(
                icon = Icons.Filled.LocationOn,
                title = "Saved Addresses",
                subtitle = "Manage delivery addresses",
                onClick = {
                    context.startActivity(Intent(context, SavedAddressesActivity::class.java))
                }
            )

            ProfileMenuItem(
                icon = Icons.Filled.DateRange,
                title = "Prescriptions",
                subtitle = "Upload and manage prescriptions",
                onClick = {
                    context.startActivity(Intent(context, PrescriptionsActivity::class.java))
                }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            ProfileMenuItem(
                icon = Icons.Filled.Settings,
                title = "Settings",
                subtitle = "App preferences and notifications",
                onClick = {
                    context.startActivity(Intent(context, SettingsActivity::class.java))
                }
            )

            ProfileMenuItem(
                icon = Icons.Default.Build,
                title = "Help & Support",
                subtitle = "FAQs and customer support",
                onClick = {
                    context.startActivity(Intent(context, HelpSupportActivity::class.java))
                }
            )

            ProfileMenuItem(
                icon = Icons.Filled.Info,
                title = "About",
                subtitle = "App version and information",
                onClick = {
                    // Show about dialog or navigate to about screen
                }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            ProfileMenuItem(
                icon = Icons.Filled.ExitToApp,
                title = "Logout",
                subtitle = "Sign out of your account",
                textColor = Color(0xFFE53E3E),
                onClick = {
                    showLogoutDialog = true
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.signOut { success, message ->
                            if (success) {
                                // Navigate to login screen
                                context.startActivity(
                                    Intent(context, LoginActivity::class.java).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    }
                                )
                            }
                        }
                        showLogoutDialog = false
                    }
                ) {
                    Text("Logout", color = Color(0xFFE53E3E))
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
private fun ProfileHeader(
    displayName: String,
    email: String,
    photoUrl: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0B8FAC))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Picture
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            if (photoUrl != null) {
                // You can use Coil or Glide to load the image
                // For now, just show initial
                Text(
                    text = displayName.firstOrNull()?.uppercase() ?: "U",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B8FAC)
                )
            } else {
                Text(
                    text = displayName.firstOrNull()?.uppercase() ?: "U",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B8FAC)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = displayName,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Text(
            text = email,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    textColor: Color = Color.Black,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = textColor,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Icon(
            imageVector = Icons.Filled.ArrowForward,
            contentDescription = "Navigate",
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}