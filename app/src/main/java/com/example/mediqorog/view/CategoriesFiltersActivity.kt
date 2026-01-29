// ========== CategoriesFiltersActivity.kt ==========
package com.example.mediqorog.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CategoriesFiltersActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CategoriesFiltersScreen(onBackClick = { finish() })
        }
    }
}

data class Category(
    val id: String,
    val name: String,
    val count: Int
)

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val category: String,
    val inStock: Boolean = true
)

class CategoriesFiltersViewModel : ViewModel() {
    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory

    private val _filteredProducts = MutableStateFlow<List<Product>>(emptyList())
    val filteredProducts: StateFlow<List<Product>> = _filteredProducts

    val categories = listOf(
        Category("all", "All", 45),
        Category("medicines", "Medicines", 25),
        Category("vitamins", "Vitamins", 10),
        Category("surgical", "Surgical", 5),
        Category("personal", "Personal Care", 5)
    )

    private val allProducts = listOf(
        Product("1", "Paracetamol 500mg", 45.0, "medicines"),
        Product("2", "Vitamin C Tablets", 120.0, "vitamins"),
        Product("3", "Cough Syrup", 85.0, "medicines"),
        Product("4", "Antiseptic Cream", 55.0, "personal"),
        Product("5", "Digital Thermometer", 350.0, "surgical"),
        Product("6", "Vitamin D3", 180.0, "vitamins"),
        Product("7", "Pain Relief Gel", 95.0, "medicines"),
        Product("8", "Hand Sanitizer", 40.0, "personal"),
        Product("9", "Bandage Roll", 25.0, "surgical"),
        Product("10", "Multivitamin", 250.0, "vitamins"),
        Product("11", "Ibuprofen 400mg", 60.0, "medicines"),
        Product("12", "Face Wash", 125.0, "personal"),
        Product("13", "Blood Pressure Monitor", 1500.0, "surgical"),
        Product("14", "Omega-3 Capsules", 350.0, "vitamins"),
        Product("15", "Cough Drops", 30.0, "medicines")
    )

    init {
        filterProducts("All")
    }

    fun filterProducts(category: String) {
        _selectedCategory.value = category
        _filteredProducts.value = if (category == "All") {
            allProducts
        } else {
            allProducts.filter { it.category == category.lowercase() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesFiltersScreen(onBackClick: () -> Unit) {
    val viewModel: CategoriesFiltersViewModel = viewModel()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val filteredProducts by viewModel.filteredProducts.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categories & Filters", fontWeight = FontWeight.Bold) },
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Categories horizontal scroll
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.categories) { category ->
                    CategoryChip(
                        category = category,
                        isSelected = selectedCategory == category.name,
                        onClick = { viewModel.filterProducts(category.name) }
                    )
                }
            }

            HorizontalDivider()

            // Products list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${filteredProducts.size} Products",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                Icons.Filled.FilterList,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = Color(0xFF0B8FAC)
                            )
                            Text(
                                "Sort by Price",
                                fontSize = 14.sp,
                                color = Color(0xFF0B8FAC)
                            )
                        }
                    }
                }

                items(filteredProducts) { product ->
                    ProductCard(product = product)
                }
            }
        }
    }
}

@Composable
fun CategoryChip(category: Category, isSelected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(category.name)
                Text(
                    "(${category.count})",
                    fontSize = 12.sp,
                    color = if (isSelected) Color(0xFF0B8FAC) else Color.Gray
                )
            }
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFF0B8FAC).copy(alpha = 0.2f),
            selectedLabelColor = Color(0xFF0B8FAC)
        )
    )
}

@Composable
fun ProductCard(product: Product) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0B8FAC).copy(alpha = 0.1f),
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        Icons.Filled.Medication,
                        contentDescription = null,
                        modifier = Modifier.padding(12.dp),
                        tint = Color(0xFF0B8FAC)
                    )
                }

                Column {
                    Text(
                        product.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "₹${product.price}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0B8FAC)
                    )
                    if (!product.inStock) {
                        Text(
                            "Out of Stock",
                            fontSize = 12.sp,
                            color = Color(0xFFE53935)
                        )
                    }
                }
            }

            Button(
                onClick = { /* Add to cart */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0B8FAC)
                ),
                enabled = product.inStock
            ) {
                Icon(
                    Icons.Filled.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add")
            }
        }
    }
}