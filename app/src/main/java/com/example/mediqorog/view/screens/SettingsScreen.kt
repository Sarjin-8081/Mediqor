package com.example.mediqorog.view.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mediqorog.model.User
import com.example.mediqorog.view.*
import com.example.mediqorog.viewmodel.UserViewModel
import com.example.mediqorog.repository.UserRepoImpl
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
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
    var isEditMode by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditMode) "Edit Profile" else "Settings",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    if (!isEditMode) {
                        IconButton(onClick = { isEditMode = true }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0B8FAC),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = isEditMode,
                transitionSpec = {
                    slideInHorizontally { it } + fadeIn() togetherWith
                            slideOutHorizontally { -it } + fadeOut()
                },
                label = "profile_mode"
            ) { editMode ->
                if (editMode) {
                    EditProfileContent(
                        currentUser = currentUser,
                        viewModel = viewModel,
                        onSaveSuccess = { isEditMode = false },
                        onCancel = { isEditMode = false }
                    )
                } else {
                    ViewProfileContent(
                        currentUser = currentUser,
                        onLogoutClick = { showLogoutDialog = true }
                    )
                }
            }
        }
    }

    // Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    Icons.Default.Logout,
                    contentDescription = null,
                    tint = Color(0xFFE53E3E),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    "Logout",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                Text("Are you sure you want to logout from your account?")
            },
            confirmButton = {
                Button(
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
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53E3E)
                    )
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = Color(0xFF0B8FAC))
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun ViewProfileContent(
    currentUser: User?,
    onLogoutClick: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8FAFB),
                        Color(0xFFEDF2F7)
                    )
                )
            )
            .verticalScroll(rememberScrollState())
    ) {
        // Profile Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Picture
                Box(
                    modifier = Modifier.size(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (!currentUser?.photoUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = currentUser?.photoUrl,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .border(4.dp, Color(0xFF0B8FAC), CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color(0xFF0B8FAC).copy(alpha = 0.1f))
                                .border(4.dp, Color(0xFF0B8FAC), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (currentUser?.displayName?.firstOrNull()
                                    ?: currentUser?.email?.firstOrNull())?.uppercase() ?: "U",
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0B8FAC)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = currentUser?.displayName?.ifEmpty { "Guest User" } ?: "Guest User",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748)
                )

                Text(
                    text = currentUser?.email ?: "Not logged in",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                if (!currentUser?.phoneNumber.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentUser?.phoneNumber ?: "",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        // Quick Stats
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickStatCard(
                icon = Icons.Default.ShoppingCart,
                value = "12",
                label = "Orders",
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            )
            QuickStatCard(
                icon = Icons.Default.Favorite,
                value = "8",
                label = "Favorites",
                color = Color(0xFFE91E63),
                modifier = Modifier.weight(1f)
            )
            QuickStatCard(
                icon = Icons.Default.Star,
                value = "4.8",
                label = "Rating",
                color = Color(0xFFFFC107),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Menu Items
        Text(
            text = "Account",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D3748),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        ProfileMenuItem(
            icon = Icons.Default.ShoppingCart,
            title = "My Orders",
            subtitle = "Track and manage orders",
            onClick = { /* TODO: Navigate to Orders */ }
        )

        ProfileMenuItem(
            icon = Icons.Default.LocationOn,
            title = "Saved Addresses",
            subtitle = "Manage delivery locations",
            onClick = {
                context.startActivity(Intent(context, SavedAddressesActivity::class.java))
            }
        )

        ProfileMenuItem(
            icon = Icons.Default.DateRange,
            title = "Prescriptions",
            subtitle = "Upload and manage prescriptions",
            onClick = {
                context.startActivity(Intent(context, PrescriptionsActivity::class.java))
            }
        )

        ProfileMenuItem(
            icon = Icons.Default.Favorite,
            title = "Wishlist",
            subtitle = "Your saved items",
            onClick = { /* TODO: Navigate to Wishlist */ }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Settings",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D3748),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        ProfileMenuItem(
            icon = Icons.Default.Notifications,
            title = "Notifications",
            subtitle = "Manage your alerts",
            onClick = { /* TODO: Navigate to Notifications */ }
        )

        ProfileMenuItem(
            icon = Icons.Default.Lock,
            title = "Privacy & Security",
            subtitle = "Control your data",
            onClick = { /* TODO: Navigate to Privacy */ }
        )

        ProfileMenuItem(
            icon = Icons.Default.Info,
            title = "Help & Support",
            subtitle = "FAQs and contact us",
            onClick = {
                context.startActivity(Intent(context, HelpSupportActivity::class.java))
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        ProfileMenuItem(
            icon = Icons.Default.ExitToApp,
            title = "Logout",
            subtitle = "Sign out of your account",
            textColor = Color(0xFFE53E3E),
            onClick = onLogoutClick
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileContent(
    currentUser: User?,
    viewModel: UserViewModel,
    onSaveSuccess: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }

    var name by remember(currentUser) { mutableStateOf(currentUser?.displayName ?: "") }
    var email by remember(currentUser) { mutableStateOf(currentUser?.email ?: "") }
    var phone by remember(currentUser) { mutableStateOf(currentUser?.phoneNumber ?: "") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }

    // Track the current display image URL (updates when image is uploaded)
    var currentPhotoUrl by remember(currentUser) { mutableStateOf(currentUser?.photoUrl ?: "") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            profileImageUri = uri
            // Update the display immediately with the local URI
            currentPhotoUrl = uri.toString()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFB))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Profile Image Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                // Display image (priority: local URI > current user photo > default)
                if (currentPhotoUrl.isNotEmpty()) {
                    AsyncImage(
                        model = currentPhotoUrl,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .border(4.dp, Color(0xFF0B8FAC), CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color(0xFF0B8FAC).copy(alpha = 0.1f))
                            .border(4.dp, Color(0xFF0B8FAC), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = Color(0xFF0B8FAC)
                        )
                    }
                }

                // Camera Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(Color(0xFF0B8FAC))
                        .border(3.dp, Color.White, CircleShape)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Change Photo",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Form Fields
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Name Field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0B8FAC),
                        focusedLabelColor = Color(0xFF0B8FAC),
                        focusedLeadingIconColor = Color(0xFF0B8FAC)
                    )
                )

                // Email Field (Read-only)
                OutlinedTextField(
                    value = email,
                    onValueChange = { },
                    label = { Text("Email") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null)
                    },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )

                // Phone Field
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0B8FAC),
                        focusedLabelColor = Color(0xFF0B8FAC),
                        focusedLeadingIconColor = Color(0xFF0B8FAC)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    if (name.isBlank()) {
                        Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true

                    // TODO: Upload image to Firebase Storage and get URL
                    // TODO: Update user profile with new data

                    Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    isLoading = false
                    onSaveSuccess()
                },
                enabled = !isLoading,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0B8FAC)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Save Changes")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun QuickStatCard(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3748)
            )
            Text(
                label,
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    textColor: Color = Color(0xFF2D3748),
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = textColor.copy(alpha = 0.1f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )
                Text(
                    subtitle,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}