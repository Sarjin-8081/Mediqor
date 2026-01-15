package com.example.mediqorog.view

import android.app.DatePickerDialog
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
import kotlinx.coroutines.launch
import java.util.*

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

    val prefs = remember {
        context.getSharedPreferences("app_settings", android.content.Context.MODE_PRIVATE)
    }

    var notificationsEnabled by remember { mutableStateOf(prefs.getBoolean("notifications", true)) }
    var orderUpdatesEnabled by remember { mutableStateOf(prefs.getBoolean("order_updates", true)) }
    var medicineRemindersEnabled by remember { mutableStateOf(prefs.getBoolean("medicine_reminders", true)) }
    var promotionalOffersEnabled by remember { mutableStateOf(prefs.getBoolean("promotional_offers", false)) }
    var darkModeEnabled by remember { mutableStateOf(prefs.getBoolean("dark_mode", false)) }
    var biometricEnabled by remember { mutableStateOf(prefs.getBoolean("biometric", false)) }

    var phoneNumber by remember { mutableStateOf(prefs.getString("phone", "+977 9800000000") ?: "+977 9800000000") }
    var dateOfBirth by remember { mutableStateOf(prefs.getString("dob", "Not set") ?: "Not set") }
    var bloodGroup by remember { mutableStateOf(prefs.getString("blood_group", "Not set") ?: "Not set") }
    var selectedLanguage by remember { mutableStateOf(prefs.getString("language", "English") ?: "English") }
    var selectedFontSize by remember { mutableStateOf(prefs.getString("font_size", "Medium") ?: "Medium") }

    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showPhoneDialog by remember { mutableStateOf(false) }
    var showBloodGroupDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showFontSizeDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showLicensesDialog by remember { mutableStateOf(false) }

    // Save preference helper
    fun savePref(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }
    fun savePref(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

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
                    onClick = {
                        val calendar = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                dateOfBirth = "$day/${month + 1}/$year"
                                savePref("dob", dateOfBirth)
                                Toast.makeText(context, "Date of birth updated", Toast.LENGTH_SHORT).show()
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }
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
                        savePref("notifications", it)
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
                        savePref("order_updates", it)
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
                        savePref("medicine_reminders", it)
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
                        savePref("promotional_offers", it)
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
                    subtitle = "$selectedLanguage (Restart app to apply)",
                    onClick = { showLanguageDialog = true }
                )

                HorizontalDivider()

                SettingsSwitchItem(
                    icon = Icons.Outlined.DarkMode,
                    title = "Dark Mode",
                    subtitle = if (darkModeEnabled) "Dark theme active" else "Light theme active",
                    checked = darkModeEnabled,
                    onCheckedChange = {
                        darkModeEnabled = it
                        savePref("dark_mode", it)
                        Toast.makeText(
                            context,
                            "Dark mode ${if (it) "enabled" else "disabled"}. Restart app to apply.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )

                HorizontalDivider()

                SettingsItem(
                    icon = Icons.Outlined.TextFields,
                    title = "Font Size",
                    subtitle = "$selectedFontSize (Restart app to apply)",
                    onClick = { showFontSizeDialog = true }
                )
                // Calculate font scale based on selected size
                val fontScale = when (selectedFontSize) {
                    "Small" -> 0.85f
                    "Medium" -> 1.0f
                    "Large" -> 1.15f
                    "Extra Large" -> 1.3f
                    else -> 1.0f
                }

// Apply typography scaling
                val scaledTypography = MaterialTheme.typography.copy(
                    bodyLarge = MaterialTheme.typography.bodyLarge.copy(fontSize = (16 * fontScale).sp),
                    bodyMedium = MaterialTheme.typography.bodyMedium.copy(fontSize = (14 * fontScale).sp),
                    bodySmall = MaterialTheme.typography.bodySmall.copy(fontSize = (12 * fontScale).sp),
                    titleMedium = MaterialTheme.typography.titleMedium.copy(fontSize = (16 * fontScale).sp),
                    titleSmall = MaterialTheme.typography.titleSmall.copy(fontSize = (14 * fontScale).sp)
                )
            }

            // 🔒 Privacy & Security Section
            SettingsSection(title = "Privacy & Security")

            SettingsCard {
                SettingsSwitchItem(
                    icon = Icons.Outlined.Fingerprint,
                    title = "Biometric Login",
                    subtitle = if (biometricEnabled) "Enabled" else "Disabled",
                    checked = biometricEnabled,
                    onCheckedChange = {
                        biometricEnabled = it
                        savePref("biometric", it)
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
                    onClick = { showTermsDialog = true }
                )

                HorizontalDivider()

                SettingsItem(
                    icon = Icons.Outlined.PrivacyTip,
                    title = "Privacy Policy",
                    onClick = { showPrivacyDialog = true }
                )

                HorizontalDivider()

                SettingsItem(
                    icon = Icons.Outlined.Article,
                    title = "Licenses",
                    onClick = { showLicensesDialog = true }
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
                savePref("phone", it)
                showPhoneDialog = false
                Toast.makeText(context, "Phone number updated", Toast.LENGTH_SHORT).show()
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
                savePref("blood_group", it)
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
                savePref("language", it)
                showLanguageDialog = false
                Toast.makeText(context, "Language set to $it", Toast.LENGTH_SHORT).show()
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
                savePref("font_size", it)
                showFontSizeDialog = false
                Toast.makeText(context, "Font size set to $it", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showTermsDialog) {
        LegalDocumentDialog(
            title = "Terms & Conditions",
            content = """
                **MediQorOG - Terms & Conditions**
                
                Last Updated: January 15, 2026
                
                **1. Acceptance of Terms**
                By accessing and using MediQorOG, you accept and agree to be bound by these Terms and Conditions.
                
                **2. Use of Service**
                • You must be 18 years or older to use this service
                • You are responsible for maintaining account security
                • Prescription medicines require valid prescriptions
                
                **3. Orders & Delivery**
                • All orders are subject to product availability
                • Delivery times are estimates and may vary
                • Prescription verification is mandatory
                
                **4. Returns & Refunds**
                • Medicines cannot be returned once delivered
                • Refunds processed within 7-10 business days
                • Damaged products must be reported within 24 hours
                
                **5. Privacy**
                • We protect your personal and medical information
                • Data is encrypted and stored securely
                • Information not shared without consent
                
                **6. Prohibited Activities**
                • Fraudulent transactions
                • Sharing of prescriptions
                • Reselling of medicines
                
                **7. Liability**
                MediQorOG is not liable for:
                • Adverse drug reactions
                • Incorrect self-medication
                • Third-party service failures
                
                **8. Changes to Terms**
                We reserve the right to modify these terms at any time. Continued use constitutes acceptance.
                
                **Contact Us**
                Email: support@mediqorog.com
                Phone: +977 980-0000-000
            """.trimIndent(),
            onDismiss = { showTermsDialog = false }
        )
    }

    if (showPrivacyDialog) {
        LegalDocumentDialog(
            title = "Privacy Policy",
            content = """
                **MediQorOG - Privacy Policy**
                
                Last Updated: January 15, 2026
                
                **1. Information We Collect**
                • Personal Information: Name, email, phone, address
                • Medical Information: Prescriptions, health records
                • Payment Information: Card details, transaction history
                • Usage Data: App interactions, preferences
                
                **2. How We Use Your Information**
                • Process orders and deliveries
                • Verify prescriptions with licensed pharmacists
                • Send order updates and reminders
                • Improve our services
                • Comply with legal requirements
                
                **3. Data Security**
                • End-to-end encryption for sensitive data
                • Secure servers with regular backups
                • HIPAA-compliant data handling
                • Regular security audits
                
                **4. Information Sharing**
                We share data only with:
                • Delivery partners (name, address, phone)
                • Payment processors (transaction details)
                • Healthcare providers (prescriptions only)
                • Law enforcement (when legally required)
                
                We NEVER:
                • Sell your personal information
                • Share medical records without consent
                • Use data for unauthorized marketing
                
                **5. Your Rights**
                • Access your personal data
                • Request data correction or deletion
                • Opt-out of promotional communications
                • Download your data
                
                **6. Data Retention**
                • Account data: Until account deletion
                • Order history: 7 years (legal requirement)
                • Prescriptions: 5 years
                • Payment info: Not stored (tokenized)
                
                **7. Cookies & Tracking**
                • Essential cookies for app functionality
                • Analytics to improve user experience
                • No third-party advertising cookies
                
                **8. Children's Privacy**
                MediQorOG is not intended for users under 18. We do not knowingly collect data from minors.
                
                **9. Changes to Policy**
                We will notify users of significant changes via email or in-app notification.
                
                **Contact Us**
                Data Protection Officer
                Email: privacy@mediqorog.com
                Phone: +977 980-0000-000
                Address: Kathmandu, Nepal
            """.trimIndent(),
            onDismiss = { showPrivacyDialog = false }
        )
    }

    if (showLicensesDialog) {
        LegalDocumentDialog(
            title = "Open Source Licenses",
            content = """
                **MediQorOG - Open Source Licenses**
                
                This app uses the following open-source libraries:
                
                **1. Jetpack Compose**
                Copyright 2021 The Android Open Source Project
                Licensed under Apache License 2.0
                
                **2. Firebase SDK**
                Copyright 2023 Google LLC
                Licensed under Apache License 2.0
                
                **3. Kotlin**
                Copyright 2010-2023 JetBrains s.r.o.
                Licensed under Apache License 2.0
                
                **4. Material Design Components**
                Copyright 2023 Google LLC
                Licensed under Apache License 2.0
                
                **5. Coroutines**
                Copyright 2016-2023 JetBrains s.r.o.
                Licensed under Apache License 2.0
                
                **6. Coil Image Loading**
                Copyright 2023 Coil Contributors
                Licensed under Apache License 2.0
                
                ---
                
                **Apache License 2.0**
                
                Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files, to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software.
                
                THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
                
                Full license text: https://www.apache.org/licenses/LICENSE-2.0
                
                ---
                
                **Special Thanks**
                • Android Open Source Project
                • Google Firebase Team
                • JetBrains Kotlin Team
                • Material Design Community
                • All open-source contributors
            """.trimIndent(),
            onDismiss = { showLicensesDialog = false }
        )
    }

    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text("Delete Account?", color = Color.Red, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("⚠️ This action is PERMANENT and CANNOT be undone!")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("All your data will be deleted:")
                    Text("• Order history")
                    Text("• Saved addresses")
                    Text("• Prescriptions")
                    Text("• Account information")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                currentUser?.delete()?.addOnSuccessListener {
                                    Toast.makeText(context, "Account deleted successfully", Toast.LENGTH_LONG).show()
                                    context.startActivity(
                                        Intent(context, LoginActivity::class.java).apply {
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        }
                                    )
                                }?.addOnFailureListener { e ->
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                        showDeleteAccountDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("DELETE ACCOUNT")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun LegalDocumentDialog(
    title: String,
    content: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 20.sp
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        },
        modifier = Modifier.fillMaxWidth(0.95f)
    )
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
            Column {
                Text("A password reset link will be sent to:")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    email,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B8FAC)
                )
            }
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
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(text) },
                enabled = text.isNotBlank()
            ) {
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
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                options.forEach { option ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(option) },
                        color = Color.Transparent
                    ) {
                        Text(
                            text = option,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    if (option != options.last()) {
                        HorizontalDivider()
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