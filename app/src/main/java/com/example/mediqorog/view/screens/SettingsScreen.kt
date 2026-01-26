package com.example.mediqorog.view.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mediqorog.model.User
import com.example.mediqorog.view.*
import com.example.mediqorog.viewmodel.UserViewModel
import com.example.mediqorog.repository.UserRepoImpl
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SettingsScreen() {
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
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

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
            Text(
                text = "Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(3.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0B8FAC).copy(alpha = 0.15f))
                        .border(2.dp, Color(0xFF0B8FAC).copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (!currentUser?.photoUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = currentUser?.photoUrl,
                            contentDescription = "Profile",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.size(40.dp),
                            tint = Color(0xFF0B8FAC)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = currentUser?.displayName?.ifEmpty { "Guest User" } ?: "Guest User",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3748)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentUser?.email ?: "",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    if (!currentUser?.phoneNumber.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF0B8FAC).copy(alpha = 0.15f)
                            ),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = currentUser?.phoneNumber ?: "",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0B8FAC),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Account Section
        SectionHeader(title = "Account")

        SettingsItem(
            icon = Icons.Default.Edit,
            title = "Edit Profile",
            subtitle = "Update your personal information",
            onClick = {
                context.startActivity(Intent(context, EditProfileActivity::class.java))
            }
        )

        SettingsItem(
            icon = Icons.Default.Lock,
            title = "Change Password",
            subtitle = "Update your account password",
            onClick = {
                context.startActivity(Intent(context, ChangePasswordActivity::class.java))
            }
        )

        SettingsItem(
            icon = Icons.Default.LocationOn,
            title = "Saved Addresses",
            subtitle = "Manage delivery locations",
            onClick = {
                context.startActivity(Intent(context, SavedAddressesActivity::class.java))
            }
        )

        SettingsItem(
            icon = Icons.Default.DateRange,
            title = "Prescriptions",
            subtitle = "Upload and manage prescriptions",
            onClick = {
                context.startActivity(Intent(context, PrescriptionsActivity::class.java))
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Preferences Section
        SectionHeader(title = "Preferences")

        SettingsItem(
            icon = Icons.Default.Notifications,
            title = "Notifications",
            subtitle = "Manage your notification alerts",
            onClick = {
                context.startActivity(Intent(context, NotificationSettingsActivity::class.java))
            }
        )

        SettingsItem(
            icon = Icons.Default.Language,
            title = "Language",
            subtitle = "Select app language",
            onClick = { showLanguageDialog = true }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Help & Support Section
        SectionHeader(title = "Help & Support")

        SettingsItem(
            icon = Icons.Default.HelpOutline,
            title = "Help Center",
            subtitle = "Get help and support",
            onClick = {
                context.startActivity(Intent(context, HelpCenterActivity::class.java))
            }
        )

        SettingsItem(
            icon = Icons.Default.PrivacyTip,
            title = "Privacy Policy",
            subtitle = "Read our privacy policy",
            onClick = {
                context.startActivity(Intent(context, PrivacyPolicyActivity::class.java))
            }
        )

        SettingsItem(
            icon = Icons.Default.Article,
            title = "Terms of Service",
            subtitle = "Read our terms of service",
            onClick = {
                context.startActivity(Intent(context, TermsOfServiceActivity::class.java))
            }
        )

        SettingsItem(
            icon = Icons.Default.Info,
            title = "About",
            subtitle = "Version 1.0.0",
            onClick = { showAboutDialog = true }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Danger Zone Section
        SectionHeader(title = "Danger Zone")

        SettingsItem(
            icon = Icons.Default.ExitToApp,
            title = "Logout",
            subtitle = "Sign out of your account",
            titleColor = Color(0xFFD32F2F),
            iconTint = Color(0xFFD32F2F),
            onClick = { showLogoutDialog = true }
        )

        SettingsItem(
            icon = Icons.Default.Delete,
            title = "Delete Account",
            subtitle = "Permanently delete your account",
            titleColor = Color(0xFFD32F2F),
            iconTint = Color(0xFFD32F2F),
            onClick = { showDeleteDialog = true }
        )

        Spacer(modifier = Modifier.height(80.dp))
    }

    // Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to logout from your account?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.signOut { success, _ ->
                            if (success) {
                                FirebaseAuth.getInstance().signOut()
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
                    Text("Logout", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Delete Account Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Account", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to permanently delete your account? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // TODO: Implement delete account
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Text(
                    "About Mediqor",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text("Version 1.0.0")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Mediqor is your complete healthcare companion app, providing easy access to health records, consultations, and medical services.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("© 2026 Mediqor. All rights reserved.")
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Close", color = Color(0xFF0B8FAC), fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Language Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = {
                Text(
                    "Select Language",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // TODO: Implement language change to English
                                showLanguageDialog = false
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = true, // TODO: Get from preferences
                            onClick = {
                                // TODO: Implement language change
                                showLanguageDialog = false
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF0B8FAC)
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("English")
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // TODO: Implement language change to Nepali
                                showLanguageDialog = false
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = false, // TODO: Get from preferences
                            onClick = {
                                // TODO: Implement language change
                                showLanguageDialog = false
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF0B8FAC)
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Nepali")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Close", color = Color(0xFF0B8FAC), fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        color = Color(0xFF2D3748),
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    )
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    titleColor: Color = Color(0xFF2D3748),
    iconTint: Color = Color(0xFF0B8FAC),
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
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = titleColor,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = subtitle,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = Color.Gray.copy(alpha = 0.5f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}