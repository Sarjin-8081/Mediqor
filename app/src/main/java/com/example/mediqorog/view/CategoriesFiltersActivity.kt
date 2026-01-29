package com.example.mediqorog.view

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class CategoriesFiltersActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                CategoriesFiltersScreen(onNavigateBack = { finish() })
            }
        }
    }
}

data class MedicineCategory(
    val name: String,
    val icon: ImageVector,
    val itemCount: Int,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesFiltersScreen(onNavigateBack: () -> Unit) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var priceRange by remember { mutableStateOf(0f..5000f) }
    var selectedBrands by remember { mutableStateOf(setOf<String>()) }

    val categories = listOf(
        MedicineCategory("Pain Relief", Icons.Default.Healing, 142, Color(0xFFEF4444)),
        MedicineCategory("Cold & Flu", Icons.Default.AcUnit, 98, Color(0xFF3B82F6)),
        MedicineCategory("Diabetes Care", Icons.Default.Bloodtype, 76, Color(0xFF10B981)),
        MedicineCategory("Vitamins", Icons.Default.LocalPharmacy, 215, Color(0xFFF59E0B)),
        MedicineCategory("Heart Health", Icons.Default.Favorite, 54, Color(0xFFEC4899)),
        MedicineCategory("Digestive", Icons.Default.Restaurant, 89, Color(0xFF8B5CF6)),
        MedicineCategory("Skin Care", Icons.Default.Face, 167, Color(0xFF06B6D4)),
        MedicineCategory("Women's Health", Icons.Default.Female, 123, Color(0xFFF97316))
    )

    val brands = listOf("Sun Pharma", "Cipla", "Dr. Reddy's", "Lupin", "Mankind", "Alkem")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categories & Filters", fontWeight = FontWeight.SemiBold) },
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
                Text(
                    text = "Categories",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
            }

            items(categories) { category ->
                CategoryCard(
                    category = category,
                    isSelected = selectedCategory == category.name,
                    onClick = {
                        selectedCategory = if (selectedCategory == category.name) null else category.name
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Price Range",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "₹${priceRange.start.toInt()} - ₹${priceRange.endInclusive.toInt()}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        RangeSlider(
                            value = priceRange,
                            onValueChange = { priceRange = it },
                            valueRange = 0f..5000f,
                            steps = 49
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Brands",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
            }

            items(brands) { brand ->
                BrandFilterItem(
                    brand = brand,
                    isSelected = selectedBrands.contains(brand),
                    onToggle = {
                        selectedBrands = if (selectedBrands.contains(brand)) {
                            selectedBrands - brand
                        } else {
                            selectedBrands + brand
                        }
                    }
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            selectedCategory = null
                            priceRange = 0f..5000f
                            selectedBrands = setOf()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Clear All")
                    }

                    Button(
                        onClick = { /* Apply filters */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Apply Filters")
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryCard(
    category: MedicineCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) category.color.copy(alpha = 0.1f) else Color.White
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, category.color) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(category.color.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.name,
                    tint = category.color,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937)
                )
                Text(
                    text = "${category.itemCount} items",
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280)
                )
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = category.color
                )
            }
        }
    }
}

@Composable
fun BrandFilterItem(
    brand: String,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle() }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = brand,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1F2937)
            )
        }
    }
}