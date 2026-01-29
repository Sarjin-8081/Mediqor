package com.example.mediqorog.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class HelpCenterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HelpCenterScreen(
                onBackClick = { finish() }
            )
        }
    }
}

@Composable
fun HelpCenterScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
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
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = "Help Center",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Contact Support Section
        Text(
            text = "Contact Support",
            style = TextStyle(
                color = Color(0xFF2D3748),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            ),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        // Email Support
        SupportOptionCard(
            icon = Icons.Default.Email,
            title = "Email Support",
            subtitle = "mediqor@gmail.com",
            onClick = {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:mediqor@gmail.com")
                }
                context.startActivity(intent)
            }
        )

        // Report a Bug
        SupportOptionCard(
            icon = Icons.Default.BugReport,
            title = "Report a Bug",
            subtitle = "Help us improve Mediqor",
            onClick = {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:mediqor@gmail.com?subject=Bug Report - Mediqor App")
                }
                context.startActivity(intent)
            }
        )

        // Send Feedback
        SupportOptionCard(
            icon = Icons.Default.Feedback,
            title = "Send Feedback",
            subtitle = "Share your suggestions",
            onClick = {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:mediqor@gmail.com?subject=Mediqor Feedback")
                }
                context.startActivity(intent)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // FAQs Section
        Text(
            text = "Frequently Asked Questions",
            style = TextStyle(
                color = Color(0xFF2D3748),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            ),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        // Account & Profile FAQs
        FAQCategoryHeader(title = "Account & Profile")

        FAQItem(
            question = "How do I create a Mediqor account?",
            answer = "You can sign up using your email or phone number. Once registered, you can manage your profile and health info."
        )

        FAQItem(
            question = "Can I update my profile information later?",
            answer = "Yes, go to your profile page and update your details anytime."
        )

        FAQItem(
            question = "How do I delete my Mediqor account?",
            answer = "Contact our support team from the app, and they will guide you through the account deletion process."
        )

        // Chatbot & Health Guidance FAQs
        FAQCategoryHeader(title = "Chatbot & Health Guidance")

        FAQItem(
            question = "How does the AI chatbot work?",
            answer = "The chatbot provides guidance on health-related queries and medicine reminders. It can't replace professional medical advice but can assist with general questions."
        )

        FAQItem(
            question = "Is my conversation with the chatbot private?",
            answer = "Yes, all chat interactions are secure and encrypted. Your data is never shared without your consent."
        )

        // Prescriptions & Medicine Reminders FAQs
        FAQCategoryHeader(title = "Prescriptions & Medicine Reminders")

        FAQItem(
            question = "How do I upload my prescription?",
            answer = "Tap \"Upload Prescription\" in the app, take a clear photo of your prescription, and submit."
        )

        FAQItem(
            question = "Can I get reminders for my medicines?",
            answer = "Yes, Mediqor will automatically send notifications for each dose based on your uploaded prescription."
        )

        // Finding Healthcare Facilities FAQs
        FAQCategoryHeader(title = "Finding Healthcare Facilities")

        FAQItem(
            question = "How do I find nearby hospitals, clinics, or pharmacies?",
            answer = "Use the \"Nearby\" feature to locate healthcare facilities around your current location. You can filter by type (hospital, pharmacy, clinic)."
        )

        FAQItem(
            question = "Can I get directions to a facility?",
            answer = "Yes, tapping on a facility will provide a map and directions."
        )

        // Buying Medicines & Order Tracking FAQs
        FAQCategoryHeader(title = "Buying Medicines & Order Tracking")

        FAQItem(
            question = "How do I order medicines?",
            answer = "Browse available medicines in the e-pharmacy section, add to cart, and complete checkout using supported payment methods."
        )

        FAQItem(
            question = "Can I track my orders?",
            answer = "Yes, go to \"My Orders\" to see the status of your purchase in real-time."
        )

        FAQItem(
            question = "What payment methods are supported?",
            answer = "Mediqor supports all major online payments including cards, mobile wallets, and UPI (if available in your region)."
        )

        // Donor Services FAQs
        FAQCategoryHeader(title = "Donor Services")

        FAQItem(
            question = "How can I register as a blood or organ donor?",
            answer = "Go to the \"Donor List\" section and submit your details. You will be added to the registry securely."
        )

        FAQItem(
            question = "Can others see my donor information?",
            answer = "Only verified healthcare authorities can access your donor info when required. Your privacy is always protected."
        )

        // Technical & Support FAQs
        FAQCategoryHeader(title = "Technical & Support")

        FAQItem(
            question = "The app is not working correctly. What should I do?",
            answer = "Try updating the app to the latest version or restarting your device. If the problem persists, contact support via the app."
        )

        FAQItem(
            question = "How can I contact Mediqor support?",
            answer = "Use the \"Help & Support\" section in the app to send a message or chat with our support team."
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun FAQCategoryHeader(title: String) {
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = title,
        style = TextStyle(
            color = Color(0xFF0B8FAC),
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        ),
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
    )
}

@Composable
fun SupportOptionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
                    .size(48.dp)
                    .background(
                        Color(0xFF0B8FAC).copy(alpha = 0.12f),
                        RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color(0xFF0B8FAC),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = TextStyle(
                        color = Color(0xFF2D3748),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = subtitle,
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                )
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
fun FAQItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    style = TextStyle(
                        color = Color(0xFF2D3748),
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color(0xFF0B8FAC),
                    modifier = Modifier.size(24.dp)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = Color.Gray.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = answer,
                        style = TextStyle(
                            color = Color.Gray,
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        )
                    )
                }
            }
        }
    }
}