package com.example.mediqorog.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class TermsOfServiceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TermsOfServiceScreen(
                onBackClick = { finish() }
            )
        }
    }
}

@Composable
fun TermsOfServiceScreen(onBackClick: () -> Unit) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFB))
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
                text = "Terms of Service",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Content Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = "Last Updated: January 22, 2026",
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                TermsSection(
                    title = "1. Acceptance of Terms",
                    content = """By accessing and using Mediqor, you accept and agree to be bound by these Terms of Service. If you do not agree to these terms, please do not use our service."""
                )

                TermsSection(
                    title = "2. Service Description",
                    content = """Mediqor is a healthcare management platform that provides:

• Digital health records management
• Doctor consultation booking
• Medication reminders
• Health information access
• Emergency contact features

We reserve the right to modify or discontinue services at any time."""
                )

                TermsSection(
                    title = "3. User Accounts",
                    content = """You are responsible for:

• Maintaining the confidentiality of your account
• All activities under your account
• Providing accurate information
• Updating your information when necessary
• Notifying us of unauthorized access

You must be at least 18 years old to create an account."""
                )

                TermsSection(
                    title = "4. Acceptable Use",
                    content = """You agree NOT to:

• Use the service for illegal purposes
• Share false or misleading health information
• Interfere with the service's operation
• Attempt to access unauthorized areas
• Share your account credentials
• Upload malicious software or code
• Harass or abuse other users or staff"""
                )

                TermsSection(
                    title = "5. Medical Disclaimer",
                    content = """IMPORTANT: Mediqor is not a substitute for professional medical advice, diagnosis, or treatment. Always seek the advice of qualified healthcare providers with questions regarding medical conditions. Do not disregard professional medical advice based on information from this app."""
                )

                TermsSection(
                    title = "6. Health Information",
                    content = """You acknowledge that:

• You have the right to upload your health data
• Information provided should be accurate
• We store data securely but cannot guarantee 100% security
• You can request data deletion at any time
• Emergency services may access your data when necessary"""
                )

                TermsSection(
                    title = "7. Consultations and Appointments",
                    content = """• Consultations are subject to doctor availability
• You must provide accurate health information
• Cancellation policies may apply
• Payment terms are specified at booking
• We are not responsible for third-party doctor services"""
                )

                TermsSection(
                    title = "8. Intellectual Property",
                    content = """All content, features, and functionality of Mediqor are owned by us and protected by international copyright, trademark, and other intellectual property laws. You may not reproduce, distribute, or create derivative works without permission."""
                )

                TermsSection(
                    title = "9. Limitation of Liability",
                    content = """Mediqor is provided "as is" without warranties of any kind. We are not liable for:

• Service interruptions or errors
• Data loss or security breaches
• Actions of third-party healthcare providers
• Decisions made based on app information
• Technical issues or device compatibility"""
                )

                TermsSection(
                    title = "10. Termination",
                    content = """We may suspend or terminate your account if:

• You violate these terms
• Your account shows suspicious activity
• Required by law
• Service is discontinued

You may delete your account at any time through Settings."""
                )

                TermsSection(
                    title = "11. Changes to Terms",
                    content = """We reserve the right to modify these terms at any time. Continued use of the service after changes constitutes acceptance of new terms. We will notify you of significant changes."""
                )

                TermsSection(
                    title = "12. Governing Law",
                    content = """These Terms are governed by the laws of Nepal. Any disputes will be resolved in the courts of Kathmandu, Nepal."""
                )

                TermsSection(
                    title = "13. Contact Information",
                    content = """For questions about these Terms:

Email: mediqor@gmail.com
Address: Kathmandu, Nepal"""
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "By using Mediqor, you acknowledge that you have read, understood, and agree to be bound by these Terms of Service.",
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        lineHeight = 18.sp
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun TermsSection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
        Text(
            text = title,
            style = TextStyle(
                color = Color(0xFF2D3748),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = content,
            style = TextStyle(
                color = Color.Gray,
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
        )
    }
}