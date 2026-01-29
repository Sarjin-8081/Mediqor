package com.example.mediqorog.view.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSettingsScreen(
    onNavigateToLogin: () -> Unit = {}  // ✅ ADDED with default empty lambda
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    var showLogoutDialog by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("Admin") }
    var userEmail by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        currentUser?.let { user ->
            userEmail = user.email ?: ""
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { doc ->
                    userName = doc.getString("displayName") ?: "Admin"
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0B8FAC),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Profile Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0B8FAC).copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = Color(0xFF0B8FAC),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            userName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            userEmail,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF4CAF50).copy(alpha = 0.1f),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                "ADMIN",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }
                }
            }

            // Profile Section
            Text(
                "Profile",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column {
                    SettingsItem(
                        icon = Icons.Default.Edit,
                        title = "Edit Profile",
                        onClick = {
                            Toast.makeText(context, "Edit Profile - Coming Soon", Toast.LENGTH_SHORT).show()
                        }
                    )
                    HorizontalDivider()
                    SettingsItem(
                        icon = Icons.Default.Lock,
                        title = "Change Password",
                        onClick = {
                            Toast.makeText(context, "Change Password - Coming Soon", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            // Preferences
            Text(
                "Preferences",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                var notificationsEnabled by remember { mutableStateOf(true) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Color(0xFF0B8FAC)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Notifications", fontSize = 15.sp)
                    }
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF0B8FAC)
                        )
                    )
                }
            }

            // Help & Support
            Text(
                "Help & Support",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column {
                    SettingsItem(
                        icon = Icons.Default.HelpOutline,
                        title = "Help Center",
                        onClick = {
                            Toast.makeText(context, "Help Center - Coming Soon", Toast.LENGTH_SHORT).show()
                        }
                    )
                    HorizontalDivider()
                    SettingsItem(
                        icon = Icons.Default.PrivacyTip,
                        title = "Privacy Policy",
                        onClick = {
                            Toast.makeText(context, "Privacy Policy - Coming Soon", Toast.LENGTH_SHORT).show()
                        }
                    )
                    HorizontalDivider()
                    SettingsItem(
                        icon = Icons.Default.Description,
                        title = "Terms of Service",
                        onClick = {
                            Toast.makeText(context, "Terms of Service - Coming Soon", Toast.LENGTH_SHORT).show()
                        }
                    )
                    HorizontalDivider()
                    SettingsItem(
                        icon = Icons.Default.Info,
                        title = "About",
                        onClick = {
                            Toast.makeText(context, "MediqorOG v1.0.0", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            // Danger Zone
            Text(
                "Danger Zone",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column {
                    SettingsItem(
                        icon = Icons.Default.Logout,
                        title = "Log Out",
                        titleColor = Color.Red,
                        onClick = { showLogoutDialog = true }
                    )
                    HorizontalDivider()
                    SettingsItem(
                        icon = Icons.Default.Delete,
                        title = "Delete Account",
                        titleColor = Color.Red,
                        onClick = {
                            Toast.makeText(context, "Delete Account - Coming Soon", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log Out") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                Button(
                    onClick = {
                        auth.signOut()
                        onNavigateToLogin()
                        showLogoutDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("Log Out")
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
fun SettingsItem(
    icon: ImageVector,
    title: String,
    titleColor: Color = Color.Black,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (titleColor == Color.Red) Color.Red else Color(0xFF0B8FAC)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            title,
            fontSize = 15.sp,
            color = titleColor,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.Gray
        )
    }
}