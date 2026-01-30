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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class EmergencyContactsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                EmergencyContactsScreen(onNavigateBack = { finish() })
            }
        }
    }
}

data class EmergencyContact(
    val name: String,
    val number: String,
    val description: String,
    val icon: ImageVector,
    val backgroundColor: Color,
    val iconColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyContactsScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current

    val emergencyContacts = listOf(
        EmergencyContact(
            name = "Ambulance",
            number = "102",
            description = "Emergency medical assistance",
            icon = Icons.Default.LocalHospital,
            backgroundColor = Color(0xFFDCFCE7),
            iconColor = Color(0xFF16A34A)
        ),
        EmergencyContact(
            name = "Police",
            number = "100",
            description = "Police emergency hotline",
            icon = Icons.Default.Shield,
            backgroundColor = Color(0xFFDBEAFE),
            iconColor = Color(0xFF2563EB)
        ),
        EmergencyContact(
            name = "Fire Brigade",
            number = "101",
            description = "Fire emergency services",
            icon = Icons.Default.FireExtinguisher,
            backgroundColor = Color(0xFFFEE2E2),
            iconColor = Color(0xFFDC2626)
        ),
        EmergencyContact(
            name = "Nepal Police Emergency",
            number = "100",
            description = "Nepal Police control room",
            icon = Icons.Default.Security,
            backgroundColor = Color(0xFFE0E7FF),
            iconColor = Color(0xFF4F46E5)
        ),
        EmergencyContact(
            name = "Red Cross",
            number = "4228094",
            description = "Nepal Red Cross Society",
            icon = Icons.Default.Favorite,
            backgroundColor = Color(0xFFFCE7F3),
            iconColor = Color(0xFFDB2777)
        ),
        EmergencyContact(
            name = "Women Helpline",
            number = "1145",
            description = "Women and children helpline",
            icon = Icons.Default.SupportAgent,
            backgroundColor = Color(0xFFFEF3C7),
            iconColor = Color(0xFFCA8A04)
        ),
        EmergencyContact(
            name = "Poison Control",
            number = "4412802",
            description = "Drug and poison information center",
            icon = Icons.Default.Warning,
            backgroundColor = Color(0xFFFFEDD5),
            iconColor = Color(0xFFEA580C)
        ),
        EmergencyContact(
            name = "Child Helpline",
            number = "1098",
            description = "Child protection helpline",
            icon = Icons.Default.ChildCare,
            backgroundColor = Color(0xFFE0F2FE),
            iconColor = Color(0xFF0284C7)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Emergency Contacts", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFEF4444),
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Warning Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Emergency,
                            contentDescription = null,
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Emergency Services",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF991B1B)
                            )
                            Text(
                                text = "Tap any contact to call immediately",
                                fontSize = 13.sp,
                                color = Color(0xFFB91C1C)
                            )
                        }
                    }
                }
            }

            // Emergency Contacts List
            items(emergencyContacts) { contact ->
                EmergencyContactCard(
                    contact = contact,
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${contact.number}")
                        }
                        context.startActivity(intent)
                    }
                )
            }

            // Additional Info Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFF2563EB)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Important Information",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E40AF)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• Stay calm during emergencies\n• Speak clearly when calling\n• Provide your exact location\n• Follow operator instructions",
                            fontSize = 13.sp,
                            color = Color(0xFF1E40AF),
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmergencyContactCard(
    contact: EmergencyContact,
    onClick: () -> Unit
) {
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
            // Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(contact.backgroundColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    contact.icon,
                    contentDescription = null,
                    tint = contact.iconColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = contact.description,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFF3F4F6)
                ) {
                    Text(
                        text = contact.number,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0B8FAC),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Call Button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF0B8FAC), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Phone,
                    contentDescription = "Call",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}