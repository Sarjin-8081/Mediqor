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
import androidx.compose.ui.unit.sp
import com.example.mediqorog.ui.components.FeatureCard
import com.example.mediqorog.view.CategoriesFiltersActivity
import com.example.mediqorog.view.HealthPackagesActivity
import com.example.mediqorog.view.LabTestsActivity
import com.example.mediqorog.view.MedicineReminderActivity
import com.example.mediqorog.view.OffersActivity
import com.example.mediqorog.view.OrderInvoicesActivity
import com.example.mediqorog.view.OrderTrackingActivity
import com.example.mediqorog.view.PaymentMethodsActivity
import com.example.mediqorog.view.PrescriptionUploadActivity
import com.example.mediqorog.view.RepeatOrdersActivity
import com.example.mediqorog.view.SavedAddressesActivity
import com.example.mediqorog.view.TeleConsultationActivity

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
        Feature("Categories & Filters", Icons.Filled.Category, Color(0xFF4CAF50), CategoriesFiltersActivity::class.java),
        Feature("Discounts & Offers", Icons.Filled.LocalOffer, Color(0xFFFF9800), OffersActivity::class.java),
        Feature("Prescription Upload", Icons.Filled.UploadFile, Color(0xFF2196F3), PrescriptionUploadActivity::class.java),
        Feature("Repeat Orders", Icons.Filled.Autorenew, Color(0xFF9C27B0), RepeatOrdersActivity::class.java),
        Feature("Payment Methods", Icons.Filled.Payment, Color(0xFF00BCD4), PaymentMethodsActivity::class.java),
        Feature("Order Tracking", Icons.Filled.LocalShipping, Color(0xFF009688), OrderTrackingActivity::class.java),
        Feature("Order Invoices", Icons.Filled.Receipt, Color(0xFF795548), OrderInvoicesActivity::class.java),
        Feature("Tele-consultation", Icons.Filled.VideoCall, Color(0xFFE91E63), TeleConsultationActivity::class.java),
        Feature("Lab Test Booking", Icons.Filled.Biotech, Color(0xFF3F51B5), LabTestsActivity::class.java),
        Feature("Health Packages", Icons.Filled.HealthAndSafety, Color(0xFF8BC34A), HealthPackagesActivity::class.java),
        Feature("Medicine Reminder", Icons.Filled.Alarm, Color(0xFFFF5722), MedicineReminderActivity::class.java),
        Feature("Saved Addresses", Icons.Filled.LocationOn, Color(0xFF607D8B), SavedAddressesActivity::class.java)
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