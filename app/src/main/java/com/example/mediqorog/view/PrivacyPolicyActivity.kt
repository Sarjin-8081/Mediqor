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

class PrivacyPolicyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PrivacyPolicyScreen(
                onBackClick = { finish() }
            )
        }
    }
}

@Composable
fun PrivacyPolicyScreen(onBackClick: () -> Unit) {
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
                text = "Privacy Policy",
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

                PolicySection(
                    title = "1. Information We Collect",
                    content = """We collect information you provide directly to us, including:

• Personal information (name, email, phone number, date of birth)
• Health information (medical records, prescriptions, blood group)
• Emergency contact details
• Location data for emergency services
• Usage data and app interactions"""
                )

                PolicySection(
                    title = "2. How We Use Your Information",
                    content = """We use the collected information to:

• Provide and maintain our healthcare services
• Process appointments and consultations
• Store and manage your health records securely
• Send medication reminders and notifications
• Improve our app features and user experience
• Comply with legal obligations"""
                )

                PolicySection(
                    title = "3. Data Security",
                    content = """We take your data security seriously:

• All data is encrypted in transit and at rest
• We use industry-standard security protocols
• Regular security audits and updates
• Secure cloud storage infrastructure (Firebase)
• Limited access to authorized personnel only"""
                )

                PolicySection(
                    title = "4. Data Sharing",
                    content = """We do not sell your personal information. We may share data with:

• Healthcare providers you've authorized
• Emergency services when necessary
• Service providers who assist our operations
• Legal authorities when required by law"""
                )

                PolicySection(
                    title = "5. Your Rights",
                    content = """You have the right to:

• Access your personal data
• Correct inaccurate information
• Request deletion of your data
• Opt-out of marketing communications
• Download your health records
• Withdraw consent at any time"""
                )

                PolicySection(
                    title = "6. Data Retention",
                    content = """We retain your data for as long as your account is active or as needed to provide services. Medical records are retained according to healthcare regulations and legal requirements in Nepal."""
                )

                PolicySection(
                    title = "7. Children's Privacy",
                    content = """Our service is not intended for users under 18 years of age. We do not knowingly collect information from children. If you believe we have collected information from a child, please contact us immediately."""
                )

                PolicySection(
                    title = "8. Third-Party Services",
                    content = """We use third-party services for:

• Authentication and database (Firebase)
• Cloud storage and hosting
• Payment processing
• Analytics and crash reporting

These services have their own privacy policies."""
                )

                PolicySection(
                    title = "9. Contact Us",
                    content = """If you have questions about this Privacy Policy, contact us at:

Email: mediqor@gmail.com
Address: Kathmandu, Nepal"""
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "By using Mediqor, you agree to this Privacy Policy. We may update this policy periodically, and changes will be posted in the app.",
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
fun PolicySection(title: String, content: String) {
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