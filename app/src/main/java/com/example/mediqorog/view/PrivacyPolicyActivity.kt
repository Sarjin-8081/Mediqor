package com.example.mediqorog.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class PrivacyPolicyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                PrivacyPolicyScreen(onNavigateBack = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy Policy", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F7FA)),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Last Updated: January 30, 2026",
                            fontSize = 13.sp,
                            color = Color(0xFF6B7280),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "At Mediqor, we are committed to protecting your privacy and ensuring the security of your personal health information. This Privacy Policy explains how we collect, use, and safeguard your data.",
                            fontSize = 14.sp,
                            color = Color(0xFF4B5563),
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            item {
                PolicySection(
                    title = "1. Information Collection",
                    content = "We collect information that you provide directly to us, including:\n\n" +
                            "• Personal identification information (name, email, phone number, date of birth)\n" +
                            "• Health information (prescriptions, medical history, allergies)\n" +
                            "• Payment information (billing address, payment method details)\n" +
                            "• Location data for delivery purposes\n" +
                            "• Device information and usage data\n\n" +
                            "We collect this information when you create an account, place orders, upload prescriptions, or interact with our services."
                )
            }

            item {
                PolicySection(
                    title = "2. Usage of Data",
                    content = "We use your information for the following purposes:\n\n" +
                            "• To process and deliver your medicine orders\n" +
                            "• To verify prescriptions with licensed pharmacists\n" +
                            "• To send order confirmations and delivery updates\n" +
                            "• To provide customer support and respond to inquiries\n" +
                            "• To send medicine reminders and health-related notifications\n" +
                            "• To improve our services and user experience\n" +
                            "• To detect and prevent fraudulent activities\n" +
                            "• To comply with legal obligations and regulations"
                )
            }

            item {
                PolicySection(
                    title = "3. Data Security",
                    content = "We implement industry-standard security measures to protect your information:\n\n" +
                            "• End-to-end encryption for sensitive data transmission\n" +
                            "• Secure SSL/TLS protocols for all communications\n" +
                            "• Regular security audits and vulnerability assessments\n" +
                            "• Restricted access to personal data on a need-to-know basis\n" +
                            "• Secure cloud storage with multiple backup systems\n" +
                            "• Two-factor authentication for account access\n\n" +
                            "However, no method of transmission over the internet is 100% secure. While we strive to protect your information, we cannot guarantee absolute security."
                )
            }

            item {
                PolicySection(
                    title = "4. User Rights",
                    content = "You have the following rights regarding your personal data:\n\n" +
                            "• Access: Request copies of your personal information\n" +
                            "• Correction: Request correction of inaccurate data\n" +
                            "• Deletion: Request deletion of your personal data\n" +
                            "• Data Portability: Receive your data in a portable format\n" +
                            "• Opt-out: Unsubscribe from marketing communications\n" +
                            "• Withdraw Consent: Withdraw consent for data processing\n\n" +
                            "To exercise these rights, please contact our support team through the Help Center. We will respond to your request within 30 days."
                )
            }

            item {
                PolicySection(
                    title = "5. Contact Information",
                    content = "If you have questions or concerns about this Privacy Policy or our data practices, please contact us:\n\n" +
                            "Email: privacy@mediqor.com\n" +
                            "Phone: +91-11-4567-8900\n" +
                            "Address: Mediqor Healthcare Pvt. Ltd.\n" +
                            "123 Medical Plaza, Connaught Place\n" +
                            "New Delhi - 110001, India\n\n" +
                            "Our Data Protection Officer is available Monday to Friday, 9:00 AM to 6:00 PM IST."
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Policy Updates",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "We may update this Privacy Policy from time to time. We will notify you of any significant changes via email or through the app. Your continued use of our services after changes indicates acceptance of the updated policy.",
                            fontSize = 13.sp,
                            color = Color(0xFFA16207),
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PolicySection(title: String, content: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = content,
                fontSize = 14.sp,
                color = Color(0xFF4B5563),
                lineHeight = 22.sp
            )
        }
    }
}