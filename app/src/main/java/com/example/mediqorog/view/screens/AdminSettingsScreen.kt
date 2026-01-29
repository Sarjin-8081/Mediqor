package com.example.mediqorog.view.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mediqorog.view.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSettingsScreen(
    onNavigateToLogin: () -> Unit = {}
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser
    val scope = rememberCoroutineScope()

    // State variables
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    var userName by remember { mutableStateOf("Admin") }
    var userEmail by remember { mutableStateOf("") }
    var userPhone by remember { mutableStateOf("") }
    var userPhotoUrl by remember { mutableStateOf("") }
    var notificationsEnabled by remember { mutableStateOf(true) }

    val scrollState = rememberScrollState()

    // Load user data
    LaunchedEffect(Unit) {
        currentUser?.let { user ->
            userEmail = user.email ?: ""
            userPhotoUrl = user.photoUrl?.toString() ?: ""
            try {
                val doc = firestore.collection("users")
                    .document(user.uid)
                    .get()
                    .await()

                userName = doc.getString("displayName") ?: "Admin"
                userPhone = doc.getString("phoneNumber") ?: ""
                userPhotoUrl = doc.getString("photoUrl") ?: ""
            } catch (e: Exception) {
                Toast.makeText(context, "Error loading profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Refresh data when returning from activities
    DisposableEffect(Unit) {
        onDispose {
            // Reload data when coming back
            scope.launch {
                currentUser?.let { user ->
                    try {
                        val doc = firestore.collection("users")
                            .document(user.uid)
                            .get()
                            .await()

                        userName = doc.getString("displayName") ?: "Admin"
                        userPhone = doc.getString("phoneNumber") ?: ""
                        userPhotoUrl = doc.getString("photoUrl") ?: ""
                    } catch (e: Exception) {
                        // Silent fail on refresh
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFB))
            .verticalScroll(scrollState)
    ) {
        // Top Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF0B8FAC),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Settings",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            if (userPhotoUrl.isEmpty()) {
                                androidx.compose.ui.graphics.Brush.linearGradient(
                                    colors = listOf(Color(0xFF0B8FAC), Color(0xFF0DA5C7))
                                )
                            } else {
                                androidx.compose.ui.graphics.Brush.linearGradient(
                                    colors = listOf(Color.Transparent, Color.Transparent)
                                )
                            }
                        )
                        .border(
                            2.dp,
                            Color(0xFF0B8FAC).copy(alpha = 0.3f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (userPhotoUrl.isNotEmpty()) {
                        AsyncImage(
                            model = userPhotoUrl,
                            contentDescription = "Profile",
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = userName.firstOrNull()?.uppercase() ?: "A",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = userName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    Text(
                        text = userEmail,
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                    if (userPhone.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = userPhone,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF059669)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "ADMIN",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF059669)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Account Section
        SectionHeader(title = "Account")

        AdminSettingsItem(
            icon = Icons.Default.Edit,
            iconColor = Color(0xFF3B82F6),
            iconBgColor = Color(0xFF3B82F6).copy(alpha = 0.1f),
            title = "Edit Profile",
            subtitle = "Update your profile information",
            onClick = {
                context.startActivity(Intent(context, EditProfileActivity::class.java))
            }
        )

        AdminSettingsItem(
            icon = Icons.Default.Lock,
            iconColor = Color(0xFF8B5CF6),
            iconBgColor = Color(0xFF8B5CF6).copy(alpha = 0.1f),
            title = "Change Password",
            subtitle = "Update your account password",
            onClick = {
                context.startActivity(Intent(context, ChangePasswordActivity::class.java))
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Preferences Section
        SectionHeader(title = "Preferences")

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF59E0B).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Notifications",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A1A1A)
                    )
                    Text(
                        "Receive order and product updates",
                        fontSize = 12.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = {
                        notificationsEnabled = it
                        Toast.makeText(
                            context,
                            if (it) "Notifications enabled" else "Notifications disabled",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF0B8FAC),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFD1D5DB)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Help & Support Section
        SectionHeader(title = "Help & Support")

        AdminSettingsItem(
            Icons.Default.HelpOutline,
            Color(0xFF06B6D4),
            Color(0xFF06B6D4).copy(alpha = 0.1f),
            "Help Center",
            "Get help and support"
        ) {
            context.startActivity(Intent(context, HelpCenterActivity::class.java))
        }

        AdminSettingsItem(
            Icons.Default.PrivacyTip,
            Color(0xFF10B981),
            Color(0xFF10B981).copy(alpha = 0.1f),
            "Privacy Policy",
            "View our privacy policy"
        ) {
            context.startActivity(Intent(context, PrivacyPolicyActivity::class.java))
        }

        AdminSettingsItem(
            Icons.Default.Description,
            Color(0xFF6366F1),
            Color(0xFF6366F1).copy(alpha = 0.1f),
            "Terms of Service",
            "View terms and conditions"
        ) {
            context.startActivity(Intent(context, TermsOfServiceActivity::class.java))
        }

        AdminSettingsItem(
            Icons.Default.Info,
            Color(0xFF8B5CF6),
            Color(0xFF8B5CF6).copy(alpha = 0.1f),
            "About",
            "App version 1.0.0"
        ) {
            showAboutDialog = true
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Danger Zone Section
        SectionHeader(title = "Danger Zone", color = Color(0xFFEF4444))

        AdminSettingsItem(
            Icons.Default.Logout,
            Color(0xFFEF4444),
            Color(0xFFEF4444).copy(alpha = 0.1f),
            "Log Out",
            "Sign out of your account"
        ) {
            showLogoutDialog = true
        }

        AdminSettingsItem(
            Icons.Default.Delete,
            Color(0xFFDC2626),
            Color(0xFFDC2626).copy(alpha = 0.1f),
            "Delete Account",
            "Permanently delete your account"
        ) {
            showDeleteAccountDialog = true
        }

        Spacer(modifier = Modifier.height(80.dp))
    }

    // Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log Out", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to log out from your admin account?") },
            confirmButton = {
                Button(
                    onClick = {
                        auth.signOut()
                        onNavigateToLogin()
                        showLogoutDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Log Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = Color(0xFF6B7280))
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Delete Account Dialog
    if (showDeleteAccountDialog) {
        AdminDeleteAccountDialog(
            onDismiss = { showDeleteAccountDialog = false },
            onDelete = { password ->
                scope.launch {
                    try {
                        currentUser?.let { user ->
                            val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(user.email!!, password)
                            user.reauthenticate(credential).await()
                            firestore.collection("users").document(user.uid).delete().await()
                            user.delete().await()
                            Toast.makeText(context, "Account deleted successfully", Toast.LENGTH_SHORT).show()
                            onNavigateToLogin()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                    showDeleteAccountDialog = false
                }
            }
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About MediqorOG Admin", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Version: 1.0.0", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    Text("MediqorOG Admin Panel")
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "A comprehensive medical pharmacy management system for administrators.",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text("© 2026 MediqorOG. All rights reserved.", fontSize = 12.sp, color = Color(0xFF9CA3AF))
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAboutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B8FAC)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("OK")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun SectionHeader(title: String, color: Color = Color(0xFF2D3748)) {
    Text(
        text = title,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    )
}

@Composable
fun AdminSettingsItem(
    icon: ImageVector,
    iconColor: Color,
    iconBgColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
                Spacer(modifier = Modifier.height(3.dp))
                Text(subtitle, fontSize = 12.sp, color = Color(0xFF9CA3AF))
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray.copy(alpha = 0.5f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun AdminDeleteAccountDialog(onDismiss: () -> Unit, onDelete: (String) -> Unit) {
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Account", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("This action cannot be undone. All your data will be permanently deleted.")
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Enter Password to Confirm") },
                    singleLine = true,
                    visualTransformation = if (showPassword) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton({ showPassword = !showPassword }) {
                            Icon(
                                if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                null
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (password.isNotBlank()) onDelete(password) },
                enabled = password.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Delete Account")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF6B7280))
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}