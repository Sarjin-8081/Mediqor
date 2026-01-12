package com.mediqor.app.ui.view

import android.content.Intent
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

    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }

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
                        .clickable { /* TODO: Edit Profile */ }
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
                    onClick = { /* TODO: Change Password */ }
                )

                SettingsItem(
                    icon = Icons.Outlined.Phone,
                    title = "Phone Number",
                    subtitle = "+977 9800000000",
                    onClick = { /* TODO: Edit Phone */ }
                )

                SettingsItem(
                    icon = Icons.Outlined.DateRange,
                    title = "Date of Birth",
                    subtitle = "Not set",
                    onClick = { /* TODO: Set DOB */ }
                )

                SettingsItem(
                    icon = Icons.Outlined.Favorite,
                    title = "Blood Group",
                    subtitle = "Not set",
                    onClick = { /* TODO: Set Blood Group */ }
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
                    onCheckedChange = { notificationsEnabled = it }
                )

                HorizontalDivider()

                SettingsSwitchItem(
                    icon = Icons.Outlined.ShoppingCart,
                    title = "Order Updates",
                    subtitle = "Get notified about order status",
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )

                HorizontalDivider()

                SettingsSwitchItem(
                    icon = Icons.Outlined.Medication,
                    title = "Medicine Reminders",
                    subtitle = "Reminders for scheduled medications",
                    checked = true,
                    onCheckedChange = { }
                )

                HorizontalDivider()

                SettingsSwitchItem(
                    icon = Icons.Outlined.LocalOffer,
                    title = "Promotional Offers",
                    subtitle = "Receive special deals and discounts",
                    checked = false,
                    onCheckedChange = { }
                )
            }

            // 🎨 App Preferences Section
            SettingsSection(title = "App Preferences")

            SettingsCard {
                SettingsItem(
                    icon = Icons.Outlined.Language,
                    title = "Language",
                    subtitle = "English",
                    onClick = { /* TODO: Language Selector */ }
                )

                HorizontalDivider()

                SettingsSwitchItem(
                    icon = Icons.Outlined.DarkMode,
                    title = "Dark Mode",
                    subtitle = "Switch to dark theme",
                    checked = darkModeEnabled,
                    onCheckedChange = { darkModeEnabled = it }
                )

                HorizontalDivider()

                SettingsItem(
                    icon = Icons.Outlined.TextFields,
                    title = "Font Size",
                    subtitle = "Medium",
                    onClick = { /* TODO: Font Size Selector */ }
                )
            }

            // 🔒 Privacy & Security Section
            SettingsSection(title = "Privacy & Security")

            SettingsCard {
                SettingsItem(
                    icon = Icons.Outlined.Fingerprint,
                    title = "Biometric Login",
                    subtitle = "Use fingerprint or face ID",
                    onClick = { /* TODO: Enable Biometric */ }
                )

                HorizontalDivider()

                SettingsItem(
                    icon = Icons.Outlined.Security,
                    title = "Two-Factor Authentication",
                    subtitle = "Add extra security to your account",
                    onClick = { /* TODO: Setup 2FA */ }
                )

                HorizontalDivider()

                SettingsItem(
                    icon = Icons.Outlined.History,
                    title = "Login Activity",
                    subtitle = "View recent login history",
                    onClick = { /* TODO: Show Login History */ }
                )
            }

            // 💳 Payment Section
            SettingsSection(title = "Payment")

            SettingsCard {
                SettingsItem(
                    icon = Icons.Outlined.CreditCard,
                    title = "Saved Payment Methods",
                    subtitle = "Manage your cards and UPI",
                    onClick = { /* TODO: Payment Methods */ }
                )

                HorizontalDivider()

                SettingsItem(
                    icon = Icons.Outlined.Receipt,
                    title = "Transaction History",
                    subtitle = "View all transactions",
                    onClick = { /* TODO: Transaction History */ }
                )
            }

            // ℹ️ About Section
            SettingsSection(title = "About")

            SettingsCard {
                SettingsItem(
                    icon = Icons.Outlined.Info,
                    title = "App Version",
                    subtitle = "1.0.0",
                    onClick = { }
                )

                HorizontalDivider()

                SettingsItem(
                    icon = Icons.Outlined.Description,
                    title = "Terms & Conditions",
                    onClick = { /* TODO: Open Terms */ }
                )

                HorizontalDivider()

                SettingsItem(
                    icon = Icons.Outlined.PrivacyTip,
                    title = "Privacy Policy",
                    onClick = { /* TODO: Open Privacy Policy */ }
                )

                HorizontalDivider()

                SettingsItem(
                    icon = Icons.Outlined.Article,
                    title = "Licenses",
                    onClick = { /* TODO: Show Licenses */ }
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
                        Toast.makeText(context, "Cache cleared", Toast.LENGTH_SHORT).show()
                    }
                )

                HorizontalDivider()

                SettingsItem(
                    icon = Icons.Outlined.DeleteForever,
                    title = "Delete Account",
                    subtitle = "Permanently delete your account",
                    textColor = Color.Red,
                    onClick = { /* TODO: Show Delete Confirmation */ }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
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