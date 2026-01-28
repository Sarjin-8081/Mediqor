// ========== HealthPackagesActivity.kt ==========
package com.example.mediqorog.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class HealthPackagesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HealthPackagesScreen(onBackClick = { finish() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthPackagesScreen(onBackClick: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var packageInterest by remember { mutableStateOf("") }
    var submitting by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Health Packages", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0B8FAC),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Coming Soon Badge
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF8BC34A).copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.LocalHospital,
                        contentDescription = null,
                        tint = Color(0xFF8BC34A),
                        modifier = Modifier.size(32.dp)
                    )
                    Column {
                        Text(
                            "Launching Soon",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8BC34A)
                        )
                        Text(
                            "Comprehensive health checkup packages",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Feature Description
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.HealthAndSafety,
                            contentDescription = null,
                            tint = Color(0xFF8BC34A)
                        )
                        Text(
                            "Health Packages",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        "Comprehensive health checkup packages tailored for different age groups and health concerns. Preventive care made simple and affordable.",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Upcoming Packages:", fontWeight = FontWeight.Medium)
                    FeaturePoint("• Basic Health Checkup")
                    FeaturePoint("• Senior Citizen Package")
                    FeaturePoint("• Women's Health Package")
                    FeaturePoint("• Diabetes Care Package")
                    FeaturePoint("• Heart Health Package")
                }
            }

            // Interest Form
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Get Notified",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Share your details to receive updates about health packages!",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Your Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = age,
                        onValueChange = { age = it },
                        label = { Text("Age") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = packageInterest,
                        onValueChange = { packageInterest = it },
                        label = { Text("Package Interest") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            scope.launch {
                                submitting = true
                                val success = submitInterest("healthpackages", name, age, packageInterest)
                                submitting = false
                                if (success) {
                                    snackbarHostState.showSnackbar("Subscribed! We'll keep you updated.")
                                    name = ""
                                    age = ""
                                    packageInterest = ""
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A)),
                        enabled = !submitting && name.isNotEmpty()
                    ) {
                        if (submitting) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Icon(Icons.Filled.Notifications, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Subscribe")
                        }
                    }
                }
            }
        }
    }
}
// ========== Shared Components ==========
@Composable
fun FeaturePoint(text: String) {
    Text(
        text,
        fontSize = 14.sp,
        color = Color.Gray,
        modifier = Modifier.padding(vertical = 2.dp)
    )
}

// Shared Firebase function for interest submissions
suspend fun submitInterest(featureType: String, name: String, age: String, details: String): Boolean {
    return try {
        val firestore = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous"

        val data = hashMapOf(
            "featureType" to featureType,
            "userId" to userId,
            "name" to name,
            "age" to age,
            "details" to details,
            "timestamp" to System.currentTimeMillis(),
            "status" to "pending"
        )

        firestore.collection("feature_interests")
            .document(UUID.randomUUID().toString())
            .set(data)
            .await()

        true
    } catch (e: Exception) {
        false
    }
}