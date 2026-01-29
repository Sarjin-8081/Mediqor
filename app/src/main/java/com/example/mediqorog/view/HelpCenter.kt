package com.example.mediqorog.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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

class HelpCenterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                HelpCenterScreen(onNavigateBack = { finish() })
            }
        }
    }
}

data class FAQItem(
    val question: String,
    val answer: String
)

data class ContactOption(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val action: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current

    val faqItems = listOf(
        FAQItem(
            "How do I place an order?",
            "Browse medicines, add to cart, upload prescription if required, and proceed to checkout. You can pay online or choose cash on delivery."
        ),
        FAQItem(
            "Do I need a prescription for all medicines?",
            "Prescription medicines require a valid prescription from a registered doctor. OTC medicines and health products can be purchased without prescription."
        ),
        FAQItem(
            "What is the delivery time?",
            "Standard delivery takes 24-48 hours. Express delivery is available in select cities with delivery within 2-4 hours."
        ),
        FAQItem(
            "How can I track my order?",
            "Go to Order Tracking section from the Features menu. Enter your order ID to see real-time tracking information."
        ),
        FAQItem(
            "What payment methods are accepted?",
            "We accept UPI, credit/debit cards, net banking, digital wallets, and cash on delivery."
        ),
        FAQItem(
            "Can I cancel or return an order?",
            "Orders can be cancelled before dispatch. Returns are accepted for damaged or wrong products within 7 days of delivery with valid prescription."
        ),
        FAQItem(
            "How do I upload a prescription?",
            "Go to Prescription Upload from Features menu. You can upload an image from gallery or take a photo. Our pharmacist will verify it."
        ),
        FAQItem(
            "Is my health data secure?",
            "Yes, we use bank-grade encryption to protect your data. All information is stored securely and never shared with third parties without consent."
        )
    )

    val contactOptions = listOf(
        ContactOption(
            Icons.Default.Email,
            "Email Support",
            "support@mediqor.com",
            "mailto:support@mediqor.com",
            Color(0xFF3B82F6)
        ),
        ContactOption(
            Icons.Default.Phone,
            "Call Us",
            "+91-11-4567-8900",
            "tel:+911145678900",
            Color(0xFF10B981)
        ),
        ContactOption(
            Icons.Default.Chat,
            "Live Chat",
            "Available 24/7",
            "chat",
            Color(0xFF8B5CF6)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help Center", fontWeight = FontWeight.SemiBold) },
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = "Support",
                            modifier = Modifier.size(48.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "How can we help you?",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "We're here to assist you 24/7",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Contact Us",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
            }

            items(contactOptions) { option ->
                ContactOptionCard(
                    option = option,
                    onClick = {
                        when (option.action) {
                            "chat" -> {
                                // Open chat functionality
                            }
                            else -> {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(option.action))
                                context.startActivity(intent)
                            }
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Frequently Asked Questions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
            }

            items(faqItems) { faqItem ->
                FAQCard(faqItem = faqItem)
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Still need help?",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E40AF)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Our customer support team is available 24/7 to assist you with any questions or concerns.",
                            fontSize = 14.sp,
                            color = Color(0xFF1E3A8A),
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:support@mediqor.com"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Email, null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Email Us")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContactOptionCard(option: ContactOption, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(option.color.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = option.title,
                    tint = option.color,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = option.subtitle,
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280)
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Go",
                tint = Color(0xFF9CA3AF)
            )
        }
    }
}

@Composable
fun FAQCard(faqItem: FAQItem) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = faqItem.question,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Color(0xFFE5E7EB))
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = faqItem.answer,
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    lineHeight = 20.sp
                )
            }
        }
    }
}