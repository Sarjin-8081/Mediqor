package com.example.mediqorog.view.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mediqorog.ui.components.FeatureCard
import com.example.mediqorog.view.PrescriptionUploadActivity
import com.example.mediqorog.view.MyOrdersActivity
import com.example.mediqorog.view.NearbyMedicalActivity
import com.example.mediqorog.view.EmergencyContactsActivity

data class Feature(
    val title: String,
    val icon: ImageVector,
    val backgroundColor: Color,
    val activityClass: Class<*>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureScreen() {
    val context = LocalContext.current

    val features = listOf(
        Feature(
            "Prescription Upload",
            Icons.Filled.UploadFile,
            Color(0xFF2196F3),
            PrescriptionUploadActivity::class.java
        ),
        Feature(
            "My Orders",
            Icons.Filled.ShoppingBag,
            Color(0xFF4CAF50),
            MyOrdersActivity::class.java
        ),
        Feature(
            "Nearby Medical",
            Icons.Filled.LocalHospital,
            Color(0xFF9C27B0),
            NearbyMedicalActivity::class.java
        ),
        Feature(
            "Emergency Contacts",
            Icons.Filled.Emergency,
            Color(0xFFEF4444),
            EmergencyContactsActivity::class.java
        )
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
                    title = feature.title,
                    icon = feature.icon,
                    backgroundColor = feature.backgroundColor,
                    onClick = {
                        context.startActivity(Intent(context, feature.activityClass))
                    }
                )
            }
        }
    }
}