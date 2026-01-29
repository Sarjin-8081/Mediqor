package com.example.mediqorog.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class OffersActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                OffersScreen(onNavigateBack = { finish() })
            }
        }
    }
}

data class Offer(
    val title: String,
    val description: String,
    val code: String,
    val discount: String,
    val validUntil: String,
    val minOrder: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OffersScreen(onNavigateBack: () -> Unit) {
    val offers = listOf(
        Offer(
            "First Order Special",
            "Get extra discount on your first medicine order",
            "FIRST50",
            "50% OFF",
            "Feb 15, 2026",
            "₹500",
            Color(0xFF10B981)
        ),
        Offer(
            "Weekend Sale",
            "Special weekend discount on all medicines",
            "WEEKEND30",
            "30% OFF",
            "Feb 28, 2026",
            "₹300",
            Color(0xFF3B82F6)
        ),
        Offer(
            "Health Plus",
            "Discount on vitamins and supplements",
            "HEALTH25",
            "25% OFF",
            "Mar 10, 2026",
            "₹400",
            Color(0xFFF59E0B)
        ),
        Offer(
            "Super Saver",
            "Flat discount on orders above ₹1000",
            "SAVE100",
            "₹100 OFF",
            "Mar 20, 2026",
            "₹1000",
            Color(0xFFEC4899)
        ),
        Offer(
            "Prescription Bonus",
            "Extra off on prescription medicines",
            "PRESC20",
            "20% OFF",
            "Apr 05, 2026",
            "₹600",
            Color(0xFF8B5CF6)
        ),
        Offer(
            "Combo Deal",
            "Buy 3 or more items and save big",
            "COMBO15",
            "15% OFF",
            "Apr 15, 2026",
            "₹250",
            Color(0xFFEF4444)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Special Offers", fontWeight = FontWeight.SemiBold) },
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
            items(offers) { offer ->
                OfferCard(offer = offer)
            }
        }
    }
}

@Composable
fun OfferCard(offer: Offer) {
    var copied by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = offer.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = offer.description,
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280)
                    )

                    Box(
                        modifier = Modifier
                            .background(offer.color.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = offer.discount,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = offer.color
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Divider(color = Color(0xFFE5E7EB))

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Use Code",
                            fontSize = 11.sp,
                            color = Color(0xFF9CA3AF)
                        )
                        Text(
                            text = offer.code,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                    }

                    OutlinedButton(
                        onClick = { copied = true },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (copied) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (copied) "Copied" else "Copy")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoChip(
                        icon = Icons.Default.CalendarToday,
                        text = "Valid till ${offer.validUntil}"
                    )
                    InfoChip(
                        icon = Icons.Default.ShoppingCart,
                        text = "Min order ${offer.minOrder}"
                    )
                }
            }
        }
    }

    @Composable
    fun InfoChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(Color(0xFFF3F4F6), RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = Color(0xFF6B7280)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                fontSize = 11.sp,
                color = Color(0xFF6B7280)
            )
        }
    }
}

@Composable
fun InfoChip(icon: ImageVector, text: String) {
    TODO("Not yet implemented")
}