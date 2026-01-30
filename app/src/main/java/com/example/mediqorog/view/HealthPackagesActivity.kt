package com.example.mediqorog.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class HealthPackagesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                HealthPackagesScreen(onNavigateBack = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthPackagesScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Health Packages", fontWeight = FontWeight.SemiBold) },
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
            contentPadding = PaddingValues(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .background(
                                    Color(0xFF8B5CF6).copy(alpha = 0.1f),
                                    RoundedCornerShape(50.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.HealthAndSafety,
                                contentDescription = "Health Packages",
                                modifier = Modifier.size(50.dp),
                                tint = Color(0xFF8B5CF6)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Feature Under Development",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Comprehensive health checkup packages tailored for your needs",
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280),
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Divider(color = Color(0xFFE5E7EB))

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "What's Coming:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1F2937)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        HealthFeatureItem(
                            icon = Icons.Default.FitnessCenter,
                            text = "Complete health checkup packages"
                        )
                        HealthFeatureItem(
                            icon = Icons.Default.Favorite,
                            text = "Heart, diabetes, and thyroid profiles"
                        )
                        HealthFeatureItem(
                            icon = Icons.Default.Group,
                            text = "Family health packages at special rates"
                        )
                        HealthFeatureItem(
                            icon = Icons.Default.Assessment,
                            text = "Personalized health risk assessment"
                        )
                        HealthFeatureItem(
                            icon = Icons.Default.MedicalServices,
                            text = "Free doctor consultation with packages"
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = false,
                            colors = ButtonDefaults.buttonColors(
                                disabledContainerColor = Color(0xFFE5E7EB),
                                disabledContentColor = Color(0xFF9CA3AF)
                            )
                        ) {
                            Text("Coming Soon", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HealthFeatureItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF8B5CF6),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color(0xFF4B5563)
        )
    }
}