package com.example.mediqorog.view.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.mediqorog.view.CalorieScannerActivity
import com.example.mediqorog.view.NutritionTrackerActivity
import com.example.mediqorog.view.MyOrdersActivity
import com.example.mediqorog.view.NearbyMedicalActivity
import com.example.mediqorog.view.EmergencyContactsActivity
import com.example.mediqorog.view.HealthPackagesActivity
import com.example.mediqorog.view.LabTestsActivity
import com.example.mediqorog.view.PrescriptionsActivity

data class FeatureItem(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val activityClass: Class<*>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureScreen() {
    val context = LocalContext.current

    val features = listOf(
        FeatureItem(
            title = "Calorie Scanner",
            icon = Icons.Default.PhotoCamera,
            color = Color(0xFF4CAF50),
            activityClass = CalorieScannerActivity::class.java
        ),
        FeatureItem(
            title = "Nutrition Tracker",
            icon = Icons.Default.Restaurant,
            color = Color(0xFF2196F3),
            activityClass = NutritionTrackerActivity::class.java
        ),
        FeatureItem(
            title = "My Orders",
            icon = Icons.Default.ShoppingBag,
            color = Color(0xFFFF9800),
            activityClass = MyOrdersActivity::class.java
        ),
        FeatureItem(
            title = "Nearby Medical",
            icon = Icons.Default.Place,
            color = Color(0xFF9C27B0),
            activityClass = NearbyMedicalActivity::class.java
        ),
        FeatureItem(
            title = "Emergency Contacts",
            icon = Icons.Default.Warning,
            color = Color(0xFFEF4444),
            activityClass = EmergencyContactsActivity::class.java
        ),
        FeatureItem(
            title = "Lab Tests",
            icon = Icons.Default.Science,
            color = Color(0xFF00BCD4),
            activityClass = LabTestsActivity::class.java
        ),
        FeatureItem(
            title = "Prescriptions",
            icon = Icons.Default.Receipt,
            color = Color(0xFF673AB7),
            activityClass = PrescriptionsActivity::class.java
        ),
        FeatureItem(
            title = "Health Packages",
            icon = Icons.Default.Favorite,
            color = Color(0xFFE91E63),
            activityClass = HealthPackagesActivity::class.java
        ),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Services",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0B8FAC),
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(features) { feature ->
                FeatureCard(
                    feature = feature,
                    onClick = {
                        val intent = Intent(context, feature.activityClass)
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun FeatureCard(
    feature: FeatureItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = feature.color,
                modifier = Modifier.size(56.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = feature.icon,
                        contentDescription = feature.title,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = feature.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        }
    }
}