package com.example.mediqorog.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.mediqorog.model.User
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EnhancedUserDetailDialog(
    user: User,
    onDismiss: () -> Unit,
    onDeleteUser: () -> Unit,
    onViewOrders: () -> Unit,
    onSuspendAccount: () -> Unit,
    onViewFullProfile: () -> Unit
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showSuspendConfirmation by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Header with Profile Picture
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF0B8FAC),
                                    Color(0xFF0DA5C7)
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Profile Picture
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (user.photoUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = user.photoUrl,
                                    contentDescription = "Profile",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = user.displayName.firstOrNull()?.uppercase() ?: "U",
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = user.displayName.ifEmpty { "User" },
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Role Badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.25f)
                        ) {
                            Text(
                                text = user.role.uppercase(),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    // Account Status Card
                    StatusCard(
                        status = user.accountStatus,
                        isEmailVerified = user.isEmailVerified,
                        isPhoneVerified = user.isPhoneVerified
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Contact Information Section
                    SectionTitle("Contact Information")
                    Spacer(modifier = Modifier.height(8.dp))

                    UserInfoRow(
                        icon = Icons.Default.Email,
                        label = "Email",
                        value = user.email,
                        isVerified = user.isEmailVerified
                    )

                    if (user.phoneNumber.isNotEmpty()) {
                        UserInfoRow(
                            icon = Icons.Default.Phone,
                            label = "Phone",
                            value = user.phoneNumber,
                            isVerified = user.isPhoneVerified
                        )
                    }

                    if (user.address.isNotEmpty()) {
                        UserInfoRow(
                            icon = Icons.Default.LocationOn,
                            label = "Address",
                            value = user.address
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Medical Information Section
                    SectionTitle("Medical Information")
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (user.bloodGroup.isNotEmpty()) {
                            InfoChip(
                                icon = Icons.Default.Bloodtype,
                                label = "Blood Group",
                                value = user.bloodGroup,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (user.gender.isNotEmpty()) {
                            InfoChip(
                                icon = Icons.Default.Person,
                                label = "Gender",
                                value = user.gender,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    if (user.dateOfBirth.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        UserInfoRow(
                            icon = Icons.Default.Cake,
                            label = "Date of Birth",
                            value = user.dateOfBirth
                        )
                    }

                    if (user.emergencyContact.isNotEmpty()) {
                        UserInfoRow(
                            icon = Icons.Default.ContactEmergency,
                            label = "Emergency Contact",
                            value = user.emergencyContact
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Statistics Section
                    SectionTitle("Account Statistics")
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            icon = Icons.Default.ShoppingBag,
                            label = "Total Orders",
                            value = user.totalOrders.toString(),
                            color = Color(0xFF3B82F6),
                            modifier = Modifier.weight(1f)
                        )

                        StatCard(
                            icon = Icons.Default.AttachMoney,
                            label = "Total Spent",
                            value = "Rs. ${String.format("%.0f", user.totalSpent)}",
                            color = Color(0xFF10B981),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    UserInfoRow(
                        icon = Icons.Default.CalendarToday,
                        label = "Registered",
                        value = formatDate(user.createdAt)
                    )

                    UserInfoRow(
                        icon = Icons.Default.AccessTime,
                        label = "Last Active",
                        value = getTimeAgo(user.lastLoginAt)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Action Buttons
                Divider()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Close")
                    }

                    Button(
                        onClick = onViewOrders,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0B8FAC)
                        )
                    ) {
                        Icon(
                            Icons.Default.ShoppingBag,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Orders")
                    }
                }

                // Admin Actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showSuspendConfirmation = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFF59E0B)
                        )
                    ) {
                        Icon(
                            Icons.Default.Block,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Suspend", fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = { showDeleteConfirmation = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFEF4444)
                        )
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete", fontSize = 13.sp)
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete User", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to permanently delete this user? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteUser()
                        showDeleteConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF4444)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Suspend Confirmation Dialog
    if (showSuspendConfirmation) {
        AlertDialog(
            onDismissRequest = { showSuspendConfirmation = false },
            title = { Text("Suspend Account", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to suspend this user's account? They won't be able to log in until reactivated.") },
            confirmButton = {
                Button(
                    onClick = {
                        onSuspendAccount()
                        showSuspendConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF59E0B)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Suspend")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSuspendConfirmation = false }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun StatusCard(
    status: String,
    isEmailVerified: Boolean,
    isPhoneVerified: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (status) {
                "active" -> Color(0xFF10B981).copy(alpha = 0.1f)
                "suspended" -> Color(0xFFEF4444).copy(alpha = 0.1f)
                else -> Color(0xFFF59E0B).copy(alpha = 0.1f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    when (status) {
                        "active" -> Icons.Default.CheckCircle
                        "suspended" -> Icons.Default.Block
                        else -> Icons.Default.Pending
                    },
                    contentDescription = null,
                    tint = when (status) {
                        "active" -> Color(0xFF10B981)
                        "suspended" -> Color(0xFFEF4444)
                        else -> Color(0xFFF59E0B)
                    },
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = status.uppercase(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (status) {
                        "active" -> Color(0xFF10B981)
                        "suspended" -> Color(0xFFEF4444)
                        else -> Color(0xFFF59E0B)
                    }
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (isEmailVerified) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                }

                if (isPhoneVerified) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Phone,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF2D3748)
    )
}

@Composable
private fun UserInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    isVerified: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color(0xFF0B8FAC),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 11.sp,
                color = Color.Gray
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = value,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2D3748)
                )
                if (isVerified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        Icons.Default.Verified,
                        contentDescription = "Verified",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoChip(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0B8FAC).copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color(0xFF0B8FAC),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                color = Color.Gray
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3748)
            )
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = Color.Gray
                )
                Text(
                    text = value,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748)
                )
            }
        }
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private fun getTimeAgo(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60000 -> "Just now"
        diff < 3600000 -> "${diff / 60000} minutes ago"
        diff < 86400000 -> "${diff / 3600000} hours ago"
        diff < 604800000 -> "${diff / 86400000} days ago"
        else -> formatDate(timestamp)
    }
}