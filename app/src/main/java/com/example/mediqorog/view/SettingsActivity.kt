package com.example.mediqorog.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SettingsScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    var notificationsEnabled by remember { mutableStateOf(true) }
    var orderUpdatesEnabled by remember { mutableStateOf(true) }
    var medicineRemindersEnabled by remember { mutableStateOf(true) }
    var promotionalOffersEnabled by remember { mutableStateOf(false) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    var biometricEnabled by remember { mutableStateOf(false) }

    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showPhoneDialog by remember { mutableStateOf(false) }
    var showDOBDialog by remember { mutableStateOf(false) }
    var showBloodGroupDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showFontSizeDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    var phoneNumber by remember { mutableStateOf("+977 9800000000") }
    var dateOfBirth by remember { mutableStateOf("Not set") }
    var bloodGroup by remember { mutableStateOf("Not set") }
    var selectedLanguage by remember { mutableStateOf("English") }
    var selectedFontSize by remember { mutableStateOf("Medium") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { (context as ComponentActivity).finish() }) {
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
                .verticalScroll(scrollState)
                .background(Color(0xFFF5F5F5))
        ) {

            // 👤 Account Section
            SettingsSection(title = "Account")

            SettingsCard {
                // Profile Picture & Name
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            Toast.makeText(context, "Edit profile coming soon", Toast.LENGTH_SHORT).show()
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape),
                        color = Color(0xFF0B8FAC)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = currentUser?.displayName?.firstOrNull()?.uppercase() ?: "U",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentUser?.displayName ?: "User Name",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = currentUser?.email ?: "user@email.com",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }

                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = "Edit",
                        tint = Color.Gray
                    )
                }

                HorizontalDivider()

                SettingsItem(
                    icon = Icons.Outlined.Lock,
                    title = "Change Password",
                    onClick = { showChangePasswordDialog = true }
                )

                SettingsItem(
                    icon = Icons.Outlined.Phone,
                    title = "Phone Number",
                    subtitle = phoneNumber,
                    onClick = { showPhoneDialog = true }
                )

                SettingsItem(
                    icon = Icons.Outlined.DateRange,
                    title = "Date of Birth",
                    subtitle = dateOfBirth,
                    onClick = { showDOBDialog = true }
                )

                SettingsItem(
                    icon = Icons.Outlined.Favorite,
                    title = "Blood Group",
                    subtitle = bloodGroup,
                    onClick = { showBloodGroupDialog = true }
                )
            }

            // 🔔 Notifications Section
            SettingsSection(title = "Notifications")

            SettingsCard {
                SettingsSwitchItem(
                    icon = Icons.Outlined.Notifications,
                    title = "Push Notifications",
                    subtitle = "Receive order updates and reminders",
                    checked = notificationsEnabled,
                    onCheckedChange = {
                        notificationsEnabled = it
                        Toast.makeText(
                            context,
                            if (it) "Notifications enabled" else "Notifications disabled",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )

                HorizontalDivider()

                SettingsSwitchItem(
                    icon = Icons.Outlined.ShoppingCart,
                    title = "Order Updates",
                    subtitle = "Get notified about order status",
                    checked = orderUpdatesEnabled,
                    onCheckedChange = {
                        orderUpdatesEnabled = it
                        Toast.makeText(
                            context,
                            if (it) "Order updates enabled" else "Order updates disabled",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )

                HorizontalDivider()

                SettingsSwitchItem(
                    icon = Icons.Outlined.MedicalServices,
                    title = "Medicine Reminders",
                    subtitle = "Reminders for scheduled medications",
                    checked = medicineRemindersEnabled,
                    onCheckedChange = {
                        medicineRemindersEnabled = it
                        Toast.makeText(
                            context,
                            if (it) "Medicine reminders enabled" else "Medicine reminders disabled",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )

                HorizontalDivider()

                SettingsSwitchItem(
                    icon = Icons.Outlined.LocalOffer,
                    title = "Promotional Offers",
                    subtitle = "Receive special deals and discounts",
                    checked = promotionalOffersEnabled,
                    onCheckedChange = {
                        promotionalOffersEnabled = it
                        Toast.makeText(
                            context,
                            if (it) "Promotional offers enabled" else "Promotional offers disabled",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }

            // 🎨 App Preferences Section
            SettingsSection(title = "App Preferences")

            SettingsCard {
                SettingsItem(
                    icon = Icons.Outlined.Language,
                    title = "Language",
                    subtitle = selectedLanguage,
                    onClick = { showLanguageDialog = true }
                )

                HorizontalDivider()

                SettingsSwitchItem(
                    icon = Icons.Outlined.DarkMode,
                    title = "Dark Mode",
                    subtitle = "Switch to dark theme",
                    checked = darkModeEnabled,
                    onCheckedChange = {
                        darkModeEnabled = it
                        Toast.makeText(
                            context,
                            "Dark mode ${if (it) "enabled" else "disabled"} (requires app restart)",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )

                HorizontalDivider()

                SettingsItem(
                    icon = Icons.Outlined.TextFields,
                    title = "Font Size",
                    subtitle = selectedFontSize,
                    onClick = { showFontSizeDialog = true }
                )
            }

            // 🔒 Privacy & Security Section
            SettingsSection(title = "Privacy & Security")

            SettingsCard {
                SettingsSwitchItem(
                    icon = Icons.Outlined.Fingerprint,
                    title = "Biometric Login",
                    subtitle = "Use fingerprint or face ID",
                    checked = biometricEnabled,
                    onCheckedChange = {
                        biometricEnabled = it
                        Toast.makeText(
                            context,
                            if (it) "Biometric login enabled" else "Biometric login disabled",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )

                HorizontalDivider()

                SettingsItem(
                    icon = Icons.Outlined.Security,
                    title = "Two-Factor Authentication",
                    subtitle = "Add extra security to your account",
                    onClick = {
                        Toast.makeText(context, "2FA setup coming soon", Toast.LENGTH_SHORT).show()
                    }
                )

                HorizontalDivider()

                SettingsItem(
                    icon = Icons.Outlined.History,
                    title = "Login Activity",
                    subtitle = "View recent login history",
                    onClick = {
                        Toast.makeText(context, "Login activity coming soon", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // 💳 Payment Section
            SettingsSection(title = "Payment")

            SettingsCard {
                SettingsItem(
                    icon = Icons.Outlined.CreditCard,
                    title = "Saved Payment Methods",
                    subtitle = "Manage your cards and UPI",
                    onClick = {
                        Toast.makeText(context, "Payment methods coming soon", Toast.LENGTH_SHORT).show()
                    }
                )

                HorizontalDivider()

                SettingsItem(
                    icon = Icons.Outlined.Receipt,
                    title = "Transaction History",
                    subtitle = "View all transactions",
                    onClick = {
                        Toast.makeText(context, "Transaction history coming soon", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // ℹ️ About Section
            SettingsSection(title = "About")

            SettingsCard {
                SettingsItem(
                    icon = Icons.Outlined.Info,
                    title = "App Version",
                    subtitle = "1.0.0",
                    onClick = {
                        Toast.makeText(context, "MediQorOG v1.0.0", Toast.LENGTH_SHORT).show()
                    }
                )

                HorizontalDivider()

                SettingsItem(
                    icon = Icons.Outlined.Description,
                    title = "Terms & Conditions",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://yourwebsite.com/terms"))
                        context.startActivity(intent)
                    }
                )

                HorizontalDivider()

                SettingsItem(
                    icon = Icons.Outlined.PrivacyTip,
                    title = "Privacy Policy",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://yourwebsite.com/privacy"))
                        context.startActivity(intent)
                    }
                )

                HorizontalDivider()

                SettingsItem(
                    icon = Icons.Outlined.Article,
                    title = "Licenses",
                    onClick = {
                        Toast.makeText(context, "Open source licenses", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // ⚠️ Danger Zone Section
            SettingsSection(title = "Danger Zone")

            SettingsCard {
                SettingsItem(
                    icon = Icons.Outlined.DeleteSweep,
                    title = "Clear Cache",
                    subtitle = "Free up storage space",
                    onClick = {
                        try {
                            context.cacheDir.deleteRecursively()
                            Toast.makeText(context, "✅ Cache cleared successfully", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "❌ Failed to clear cache", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                HorizontalDivider()

                SettingsItem(
                    icon = Icons.Outlined.DeleteForever,
                    title = "Delete Account",
                    subtitle = "Permanently delete your account",
                    textColor = Color.Red,
                    onClick = { showDeleteAccountDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Dialogs
    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showChangePasswordDialog = false },
            onConfirm = { email ->
                scope.launch {
                    try {
                        auth.sendPasswordResetEmail(email)
                        Toast.makeText(context, "✅ Password reset email sent!", Toast.LENGTH_LONG).show()
                        showChangePasswordDialog = false
                    } catch (e: Exception) {
                        Toast.makeText(context, "❌ Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }

    if (showPhoneDialog) {
        InputDialog(
            title = "Phone Number",
            initialValue = phoneNumber,
            onDismiss = { showPhoneDialog = false },
            onConfirm = {
                phoneNumber = it
                showPhoneDialog = false
                Toast.makeText(context, "Phone number updated", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showDOBDialog) {
        InputDialog(
            title = "Date of Birth",
            initialValue = dateOfBirth,
            placeholder = "DD/MM/YYYY",
            onDismiss = { showDOBDialog = false },
            onConfirm = {
                dateOfBirth = it
                showDOBDialog = false
                Toast.makeText(context, "Date of birth updated", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showBloodGroupDialog) {
        SelectionDialog(
            title = "Select Blood Group",
            options = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"),
            onDismiss = { showBloodGroupDialog = false },
            onSelect = {
                bloodGroup = it
                showBloodGroupDialog = false
                Toast.makeText(context, "Blood group updated", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showLanguageDialog) {
        SelectionDialog(
            title = "Select Language",
            options = listOf("English", "नेपाली", "हिन्दी"),
            onDismiss = { showLanguageDialog = false },
            onSelect = {
                selectedLanguage = it
                showLanguageDialog = false
                Toast.makeText(context, "Language updated (requires app restart)", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showFontSizeDialog) {
        SelectionDialog(
            title = "Select Font Size",
            options = listOf("Small", "Medium", "Large", "Extra Large"),
            onDismiss = { showFontSizeDialog = false },
            onSelect = {
                selectedFontSize = it
                showFontSizeDialog = false
                Toast.makeText(context, "Font size updated", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text("Delete Account?", color = Color.Red) },
            text = {
                Text("This action cannot be undone. All your data will be permanently deleted.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                currentUser?.delete()?.addOnSuccessListener {
                                    Toast.makeText(context, "Account deleted", Toast.LENGTH_SHORT).show()
                                    context.startActivity(
                                        Intent(context, LoginActivity::class.java).apply {
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        }
                                    )
                                }?.addOnFailureListener { e ->
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                        showDeleteAccountDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val email = auth.currentUser?.email ?: ""

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Password") },
        text = {
            Text("A password reset email will be sent to $email")
        },
        confirmButton = {
            Button(onClick = { onConfirm(email) }) {
                Text("Send Email")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun InputDialog(
    title: String,
    initialValue: String,
    placeholder: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(initialValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text(placeholder) },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(text) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SelectionDialog(
    title: String,
    options: List<String>,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                options.forEach { option ->
                    TextButton(
                        onClick = { onSelect(option) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(option, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SettingsSection(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            content()
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    textColor: Color = Color.Black,
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
            imageVector = icon,
            contentDescription = title,
            tint = Color(0xFF0B8FAC),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Navigate",
            tint = Color.Gray
        )
    }
}

@Composable
fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color(0xFF0B8FAC),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF0B8FAC),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.LightGray
            )
        )
    }
}