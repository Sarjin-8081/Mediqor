package com.mediqor.app.ui.screens.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mediqor.app.model.ProductModel

// ==================== PHARMACY SCREEN ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PharmacyScreen(onBackClick: () -> Unit = {}) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val products = remember {
        listOf(
            ProductModel(
                id = "1",
                name = "Zandu Balm Ultra Power-8ml",
                price = 120.0,
                description = "Pain relief balm",
                imageUrl = "",
                category = "Balms"
            ),
            ProductModel(
                id = "2",
                name = "Vicks Vaporub",
                price = 85.0,
                description = "Cold and cough relief",
                imageUrl = "",
                category = "Cold & Cough"
            ),
            ProductModel(
                id = "3",
                name = "Paracetamol 500mg - 15 tablets",
                price = 25.0,
                description = "Fever and pain relief",
                imageUrl = "",
                category = "Pain Relief"
            ),
            ProductModel(
                id = "4",
                name = "Cetrizine 10mg - 10 tablets",
                price = 35.0,
                description = "Allergy relief",
                imageUrl = "",
                category = "Allergy"
            ),
            ProductModel(
                id = "5",
                name = "Cough Syrup 100ml",
                price = 95.0,
                description = "Cough relief",
                imageUrl = "",
                category = "Cold & Cough"
            ),
            ProductModel(
                id = "6",
                name = "Vitamin C Tablets",
                price = 150.0,
                description = "Immunity booster",
                imageUrl = "",
                category = "Vitamins"
            )
        )
    }

    val filters = listOf("All", "Balms", "Cold & Cough", "Pain Relief", "Allergy")

    CategoryScreenTemplate(
        title = "Pharmacy",
        onBackClick = onBackClick,
        searchQuery = searchQuery,
        onSearchChange = { searchQuery = it },
        selectedFilter = selectedFilter,
        onFilterChange = { selectedFilter = it },
        filters = filters,
        products = products
    )
}

// ==================== FAMILY CARE SCREEN ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyCareScreen(onBackClick: () -> Unit = {}) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val products = remember {
        listOf(
            ProductModel("1", "Baby Diapers - Medium (50 pcs)", 899.0, "Soft and comfortable", "", "Baby Care"),
            ProductModel("2", "Baby Wipes - 80 sheets", 199.0, "Gentle on skin", "", "Baby Care"),
            ProductModel("3", "Baby Shampoo 200ml", 250.0, "Tear-free formula", "", "Baby Care"),
            ProductModel("4", "Baby Lotion 300ml", 320.0, "Moisturizing lotion", "", "Baby Care"),
            ProductModel("5", "Feeding Bottle - 250ml", 299.0, "BPA free", "", "Baby Products"),
            ProductModel("6", "Baby Powder 200g", 180.0, "Keeps baby fresh", "", "Baby Care")
        )
    }

    val filters = listOf("All", "Baby Care", "Baby Products", "Mom Care")

    CategoryScreenTemplate(
        title = "Family Care",
        onBackClick = onBackClick,
        searchQuery = searchQuery,
        onSearchChange = { searchQuery = it },
        selectedFilter = selectedFilter,
        onFilterChange = { selectedFilter = it },
        filters = filters,
        products = products
    )
}

// ==================== PERSONAL CARE SCREEN ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalCareScreen(onBackClick: () -> Unit = {}) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val products = remember {
        listOf(
            ProductModel("1", "The Derma Co 10% Niacinamide Serum - 30ml", 599.0, "Face serum", "", "Skincare"),
            ProductModel("2", "Minimalist 02% Alpha Arbutin Face Serum | 30 ml", 699.0, "Brightening serum", "", "Skincare"),
            ProductModel("3", "Face Wash - Oil Control", 299.0, "Deep cleansing", "", "Skincare"),
            ProductModel("4", "Sunscreen SPF 50 - 100ml", 499.0, "Sun protection", "", "Skincare"),
            ProductModel("5", "Body Lotion - Moisturizing 250ml", 350.0, "Hydrating lotion", "", "Body Care"),
            ProductModel("6", "Hair Oil - Anti Dandruff 200ml", 399.0, "Nourishing oil", "", "Hair Care")
        )
    }

    val filters = listOf("All", "Skincare", "Hair Care", "Body Care", "Oral Care")

    CategoryScreenTemplate(
        title = "Personal Care",
        onBackClick = onBackClick,
        searchQuery = searchQuery,
        onSearchChange = { searchQuery = it },
        selectedFilter = selectedFilter,
        onFilterChange = { selectedFilter = it },
        filters = filters,
        products = products
    )
}

// ==================== SUPPLEMENTS SCREEN ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplementsScreen(onBackClick: () -> Unit = {}) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val products = remember {
        listOf(
            ProductModel("1", "Multivitamin Tablets - 60 Count", 599.0, "Daily vitamins", "", "Vitamins"),
            ProductModel("2", "Omega-3 Fish Oil - 90 Capsules", 899.0, "Heart health", "", "Supplements"),
            ProductModel("3", "Protein Powder - Chocolate 1kg", 1999.0, "Muscle building", "", "Protein"),
            ProductModel("4", "Vitamin D3 - 60 Tablets", 399.0, "Bone health", "", "Vitamins"),
            ProductModel("5", "Calcium + Magnesium - 120 Tablets", 499.0, "Bone strength", "", "Minerals"),
            ProductModel("6", "Vitamin B Complex - 90 Tablets", 349.0, "Energy boost", "", "Vitamins")
        )
    }

    val filters = listOf("All", "Vitamins", "Protein", "Minerals", "Supplements")

    CategoryScreenTemplate(
        title = "Supplements",
        onBackClick = onBackClick,
        searchQuery = searchQuery,
        onSearchChange = { searchQuery = it },
        selectedFilter = selectedFilter,
        onFilterChange = { selectedFilter = it },
        filters = filters,
        products = products
    )
}

// ==================== SURGICAL SCREEN ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurgicalScreen(onBackClick: () -> Unit = {}) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val products = remember {
        listOf(
            ProductModel("1", "Surgical Mask - 50 pcs Box", 299.0, "3-ply protection", "", "Masks"),
            ProductModel("2", "Disposable Gloves - 100 pcs", 399.0, "Latex free", "", "Gloves"),
            ProductModel("3", "Bandage Roll - 5cm x 4.5m", 45.0, "Elastic bandage", "", "Bandages"),
            ProductModel("4", "Cotton Gauze - Sterile 100g", 89.0, "Medical grade", "", "Gauze"),
            ProductModel("5", "Surgical Tape - 2.5cm x 5m", 65.0, "Hypoallergenic", "", "Tapes"),
            ProductModel("6", "Sterile Syringes - 5ml (10 pcs)", 120.0, "Single use", "", "Syringes")
        )
    }

    val filters = listOf("All", "Masks", "Gloves", "Bandages", "Syringes")

    CategoryScreenTemplate(
        title = "Surgical",
        onBackClick = onBackClick,
        searchQuery = searchQuery,
        onSearchChange = { searchQuery = it },
        selectedFilter = selectedFilter,
        onFilterChange = { selectedFilter = it },
        filters = filters,
        products = products
    )
}

// ==================== DEVICES SCREEN ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicesScreen(onBackClick: () -> Unit = {}) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val products = remember {
        listOf(
            ProductModel("1", "Digital Thermometer", 299.0, "Fast reading", "", "Thermometers"),
            ProductModel("2", "Blood Pressure Monitor", 1599.0, "Automatic", "", "BP Monitors"),
            ProductModel("3", "Glucometer with 25 Strips", 899.0, "Blood sugar monitor", "", "Diabetes Care"),
            ProductModel("4", "Pulse Oximeter", 799.0, "SpO2 monitor", "", "Oximeters"),
            ProductModel("5", "Nebulizer Machine", 1999.0, "Respiratory care", "", "Respiratory"),
            ProductModel("6", "Weighing Scale Digital", 699.0, "Body weight scale", "", "Scales")
        )
    }

    val filters = listOf("All", "BP Monitors", "Thermometers", "Diabetes Care")

    CategoryScreenTemplate(
        title = "Devices",
        onBackClick = onBackClick,
        searchQuery = searchQuery,
        onSearchChange = { searchQuery = it },
        selectedFilter = selectedFilter,
        onFilterChange = { selectedFilter = it },
        filters = filters,
        products = products
    )
}

// ==================== REUSABLE TEMPLATE ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryScreenTemplate(
    title: String,
    onBackClick: () -> Unit,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    filters: List<String>,
    products: List<ProductModel>
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
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
                .background(Color(0xFFF5F5F5))
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search $title products...") },
                leadingIcon = { Icon(Icons.Filled.Search, "Search") },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.take(3).forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { onFilterChange(filter) },
                        label = { Text(filter) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF0B8FAC),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Products Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(products.filter {
                    (selectedFilter == "All" || it.category == selectedFilter) &&
                            (searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true))
                }) { product ->
                    CategoryProductCard(product = product)
                }
            }
        }
    }
}

@Composable
private fun CategoryProductCard(product: ProductModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clickable { /* Handle product click */ },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                    contentDescription = product.name,
                    modifier = Modifier.size(80.dp),
                    tint = Color(0xFF0B8FAC).copy(alpha = 0.3f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rs. ${product.price}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B8FAC)
                )
                Button(
                    onClick = { /* Add to cart */ },
                    modifier = Modifier.height(32.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0B8FAC)
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("Add", fontSize = 12.sp)
                }
            }
        }
    }
}