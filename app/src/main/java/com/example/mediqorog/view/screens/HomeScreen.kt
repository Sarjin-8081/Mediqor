package com.example.mediqorog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.mediqorog.model.ProductModel
import com.example.mediqorog.viewmodel.ProductUiState
import com.example.mediqorog.viewmodel.ProductViewModel

// Category data class
data class Category(
    val name: String,
    val icon: String = "" // You can use emoji or icons
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ProductViewModel,
    onProductClick: (ProductModel) -> Unit,
    onAddToCart: (ProductModel) -> Unit,
    onCategoryClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Load all products on first composition
    LaunchedEffect(Unit) {
        viewModel.loadAllProducts()
    }

    Scaffold(
        topBar = {
            // Custom Top Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF0B8FAC),
                shadowElevation = 4.dp
            ) {
                Column {
                    // Top section with logo and profile
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Logo
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF075E6E)
                        ) {
                            Text(
                                text = "🎵 MediQorog",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        // Profile Icon
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                modifier = Modifier.padding(8.dp),
                                tint = Color(0xFF0B8FAC)
                            )
                        }
                    }

                    // Search Bar
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clickable { onSearchClick() },
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Search medicines, products...",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is ProductUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF0B8FAC))
                }
            }

            is ProductUiState.Success -> {
                val allProducts = state.products
                val featuredProducts = allProducts.filter { it.isFeatured }
                val bestSellers = allProducts.sortedByDescending { it.stock }.take(10)

                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(Color(0xFFF5F5F5)),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Categories Section
                    item {
                        CategoriesSection(onCategoryClick = onCategoryClick)
                    }

                    // Best Sellers Section
                    if (bestSellers.isNotEmpty()) {
                        item {
                            ProductSection(
                                title = "Best Sellers",
                                subtitle = "Most popular this week",
                                products = bestSellers,
                                onProductClick = onProductClick,
                                onAddToCart = onAddToCart,
                                onViewAllClick = { onCategoryClick("All") }
                            )
                        }
                    }

                    // Featured Products Section
                    if (featuredProducts.isNotEmpty()) {
                        item {
                            ProductSection(
                                title = "Featured Brand Of Week",
                                subtitle = "Exclusive deals",
                                products = featuredProducts,
                                onProductClick = onProductClick,
                                onAddToCart = onAddToCart,
                                showDiscount = true,
                                onViewAllClick = { onCategoryClick("Featured") }
                            )
                        }
                    }

                    // Popular Products Section
                    item {
                        ProductSection(
                            title = "Popular Products",
                            subtitle = "Customer Likes",
                            products = allProducts.take(10),
                            onProductClick = onProductClick,
                            onAddToCart = onAddToCart,
                            onViewAllClick = { onCategoryClick("All") }
                        )
                    }

                    // Bottom spacing
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            is ProductUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = "Error",
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(state.message, color = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadAllProducts() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0B8FAC)
                            )
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }

            else -> {}
        }
    }
}

@Composable
private fun CategoriesSection(
    onCategoryClick: (String) -> Unit
) {
    val categories = listOf(
        Category("Pharmacy", "💊"),
        Category("Family Care", "👨‍👩‍👧"),
        Category("Personal Care", "🧴"),
        Category("Supplements", "💪"),
        Category("Surgical", "✂️"),
        Category("Devices", "🩺")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "Shop by Category",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Grid of categories (2 rows, 3 columns)
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            categories.chunked(3).forEach { rowCategories ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    rowCategories.forEach { category ->
                        CategoryItem(
                            category = category,
                            onClick = { onCategoryClick(category.name) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Fill empty spaces in the last row
                    repeat(3 - rowCategories.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryItem(
    category: Category,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF5F5F5)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = category.icon,
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = category.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun ProductSection(
    title: String,
    subtitle: String,
    products: List<ProductModel>,
    onProductClick: (ProductModel) -> Unit,
    onAddToCart: (ProductModel) -> Unit,
    showDiscount: Boolean = false,
    onViewAllClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 16.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (title.contains("Featured")) {
                        Text(text = "🏆 ", fontSize = 20.sp)
                    } else if (title.contains("Best")) {
                        Text(text = "⚡ ", fontSize = 20.sp)
                    }
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            TextButton(onClick = onViewAllClick) {
                Text(
                    text = "View All →",
                    color = Color(0xFF0B8FAC),
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Horizontal scrolling products
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products.take(10)) { product ->
                ProductCard(
                    product = product,
                    onClick = { onProductClick(product) },
                    onAddToCart = { onAddToCart(product) },
                    showDiscount = showDiscount
                )
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: ProductModel,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    showDiscount: Boolean = false
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Product Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                AsyncImage(
                    model = product.imageUrl.ifEmpty { null },
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF5F5F5)),
                    contentScale = ContentScale.Crop
                )

                // Discount Badge
                if (showDiscount) {
                    Surface(
                        modifier = Modifier.align(Alignment.TopStart),
                        shape = RoundedCornerShape(bottomEnd = 8.dp),
                        color = Color(0xFF4CAF50)
                    ) {
                        Text(
                            text = "10%",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // ePoints Badge
                if (product.isFeatured) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF4DB6AC)
                    ) {
                        Text(
                            text = "${(product.price * 0.1).toInt()} ePoints",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Product Name
            Text(
                text = product.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Color.Black,
                minLines = 2
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Price Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "रु ${product.price.toInt()}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0B8FAC)
                    )

                    if (showDiscount) {
                        Text(
                            text = "रु ${(product.price * 1.1).toInt()}",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                        )
                    }
                }

                // Heart icon
                IconButton(
                    onClick = { /* Add to favorites */ },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Add to Cart Button
            Button(
                onClick = onAddToCart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                enabled = product.inStock,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4DB6AC),
                    disabledContainerColor = Color.Gray
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = if (product.inStock) "Add To Cart" else "Out of Stock",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}