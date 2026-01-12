package com.mediqor.app.ui.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class HelpSupportActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HelpSupportScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help & Support") },
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

            // 🚨 Emergency Contact Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Emergency,
                        contentDescription = "Emergency",
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Emergency Contact",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F)
                    )
                    Text(
                        text = "Call ambulance or emergency services",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:102") // Nepal Ambulance
                            }
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD32F2F)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Phone, "Call", modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Call 102 (Ambulance)", fontSize = 16.sp)
                    }
                }
            }

            // 📞 Quick Contact Options
            SectionTitle("Quick Contact")

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                QuickContactItem(
                    icon = Icons.Default.Phone,
                    title = "Call Customer Support",
                    subtitle = "+977 9800000000",
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:+9779800000000")
                        }
                        context.startActivity(intent)
                    }
                )

                HorizontalDivider()

                QuickContactItem(
                    icon = Icons.Default.Email,
                    title = "Email Us",
                    subtitle = "support@mediqor.com",
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:support@mediqor.com")
                            putExtra(Intent.EXTRA_SUBJECT, "Support Request")
                        }
                        context.startActivity(intent)
                    }
                )

                HorizontalDivider()

                QuickContactItem(
                    icon = Icons.Filled.MarkUnreadChatAlt,
                    title = "Live Chat",
                    subtitle = "Chat with our support team",
                    onClick = {
                        Toast.makeText(context, "Live chat coming soon!", Toast.LENGTH_SHORT).show()
                    }
                )

                HorizontalDivider()

                QuickContactItem(
                    icon = Icons.Filled.ChatBubble,
                    title = "WhatsApp Support",
                    subtitle = "Get help via WhatsApp",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://wa.me/9779800000000")
                        }
                        context.startActivity(intent)
                    }
                )
            }

            // ❓ FAQs Section
            SectionTitle("Frequently Asked Questions")

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column {
                    FAQItem(
                        question = "How do I order medicines?",
                        answer = "Browse products, add to cart, proceed to checkout, select delivery address and payment method, then confirm your order."
                    )
                    HorizontalDivider()

                    FAQItem(
                        question = "How do I set medicine reminders?",
                        answer = "Go to Prescriptions → Add Prescription → Set medicine name, dosage, and timing. You'll receive notifications at scheduled times."
                    )
                    HorizontalDivider()

                    FAQItem(
                        question = "What are the payment options?",
                        answer = "We accept credit/debit cards, eSewa, Khalti, bank transfers, and cash on delivery for eligible orders."
                    )
                    HorizontalDivider()

                    FAQItem(
                        question = "How can I track my order?",
                        answer = "Go to My Orders → Select your order → Click Track Order. You'll see real-time delivery status and estimated arrival time."
                    )
                    HorizontalDivider()

                    FAQItem(
                        question = "What is your refund policy?",
                        answer = "Full refund within 7 days if product is unopened and in original packaging. Defective items replaced immediately."
                    )
                    HorizontalDivider()

                    FAQItem(
                        question = "How long does delivery take?",
                        answer = "Within Kathmandu: 1-2 days. Outside valley: 3-5 days. Express delivery available for urgent orders."
                    )
                    HorizontalDivider()

                    FAQItem(
                        question = "Do I need a prescription to order?",
                        answer = "Prescription medicines require valid prescription upload. OTC medicines can be ordered without prescription."
                    )
                    HorizontalDivider()

                    FAQItem(
                        question = "How do I upload my prescription?",
                        answer = "During checkout, click 'Upload Prescription' → Take photo or select from gallery → Submit. Our pharmacist will verify before processing."
                    )
                }
            }

            // 📝 Report & Feedback Section
            SectionTitle("Report & Feedback")

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                ReportItem(
                    icon = Icons.Outlined.ReportProblem,
                    title = "Report a Problem",
                    subtitle = "Order, payment, or app issues",
                    onClick = { /* TODO: Open Report Form */ }
                )

                HorizontalDivider()

                ReportItem(
                    icon = Icons.Outlined.Star,
                    title = "Rate Our Service",
                    subtitle = "Help us improve",
                    onClick = { /* TODO: Open Rating Dialog */ }
                )

                HorizontalDivider()

                ReportItem(
                    icon = Icons.Outlined.Feedback,
                    title = "Give Feedback",
                    subtitle = "Share your suggestions",
                    onClick = { /* TODO: Open Feedback Form */ }
                )

                HorizontalDivider()

                ReportItem(
                    icon = Icons.Outlined.BugReport,
                    title = "Report a Bug",
                    subtitle = "Found something wrong?",
                    onClick = { /* TODO: Open Bug Report */ }
                )
            }

            // 📚 Guides & Tutorials
            SectionTitle("Guides & Tutorials")

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                GuideItem(
                    icon = Icons.Outlined.PlayCircle,
                    title = "How to Use the App",
                    subtitle = "Video tutorial",
                    onClick = { /* TODO: Open Video */ }
                )

                HorizontalDivider()

                GuideItem(
                    icon = Icons.Outlined.Medication,
                    title = "Setting Up Prescriptions",
                    subtitle = "Step-by-step guide",
                    onClick = { /* TODO: Open Guide */ }
                )

                HorizontalDivider()

                GuideItem(
                    icon = Icons.Outlined.Map,
                    title = "Finding Nearby Pharmacies",
                    subtitle = "Use the map feature",
                    onClick = { /* TODO: Open Guide */ }
                )

                HorizontalDivider()

                GuideItem(
                    icon = Icons.Outlined.ShoppingBag,
                    title = "Placing Your First Order",
                    subtitle = "Quick start guide",
                    onClick = { /* TODO: Open Guide */ }
                )
            }

            // ℹ️ Contact Information
            SectionTitle("Contact Information")

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ContactInfoRow(Icons.Default.Phone, "Customer Service", "+977 9800000000")
                    Spacer(modifier = Modifier.height(12.dp))
                    ContactInfoRow(Icons.Default.Email, "Email", "support@mediqor.com")
                    Spacer(modifier = Modifier.height(12.dp))
                    ContactInfoRow(Icons.Default.LocationOn, "Office", "Kathmandu, Nepal")
                    Spacer(modifier = Modifier.height(12.dp))
                    ContactInfoRow(Icons.Default.Schedule, "Business Hours", "24/7 Available")
                }
            }

            // 📄 Legal Section
            SectionTitle("Legal")

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                LegalItem(
                    title = "Terms of Service",
                    onClick = { /* TODO: Open Terms */ }
                )
                HorizontalDivider()
                LegalItem(
                    title = "Privacy Policy",
                    onClick = { /* TODO: Open Privacy */ }
                )
                HorizontalDivider()
                LegalItem(
                    title = "Return & Refund Policy",
                    onClick = { /* TODO: Open Refund Policy */ }
                )
                HorizontalDivider()
                LegalItem(
                    title = "Shipping Policy",
                    onClick = { /* TODO: Open Shipping Policy */ }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
fun QuickContactItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFE3F2FD)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF0B8FAC),
                modifier = Modifier
                    .padding(12.dp)
                    .size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Navigate",
            tint = Color.Gray
        )
    }
}

@Composable
fun FAQItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = question,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = Color(0xFF0B8FAC)
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Text(
                text = answer,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun ReportItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
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
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Navigate",
            tint = Color.Gray
        )
    }
}

@Composable
fun GuideItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
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
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Navigate",
            tint = Color.Gray
        )
    }
}

@Composable
fun ContactInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFF0B8FAC),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun LegalItem(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Navigate",
            tint = Color.Gray
        )
    }
}