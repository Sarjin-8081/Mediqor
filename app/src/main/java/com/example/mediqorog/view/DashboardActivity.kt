package com.example.mediqorog.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mediqorog.model.ProductModel
import com.example.mediqorog.repository.CommonRepoImpl
import com.example.mediqorog.repository.ProductRepositoryImpl
import com.example.mediqorog.viewmodel.ProductUiState
import com.example.mediqorog.viewmodel.ProductViewModel
import com.example.mediqorog.viewmodel.ProductViewModelFactory

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                HomeScreen(
                    onChatbotClick = {
                        // TODO: Handle chatbot click
                    }
                )
            }
        }
    }
}

data class Category(
    val name: String,
    val icon: String
)

@Composable
fun HomeScreen(
    onChatbotClick: () -> Unit
) {
    // Create ViewModel
    val viewModel: ProductViewModel = viewModel(
        factory = ProductViewModelFactory(
            ProductRepositoryImpl(),
            CommonRepoImpl()
        )
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showProductDetail by remember { mutableStateOf<ProductModel?>(null) }
    var showCategoryProducts by remember { mutableStateOf<String?>(null) }

    // Load all products on first composition
    LaunchedEffect(Unit) {
        viewModel.loadAllProducts()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Main Home Content
        if (showProductDetail == null && showCategoryProducts == null) {
            HomeContent(
                uiState = uiState,
                viewModel = viewModel,
                onProductClick = { product -> showProductDetail = product },
                onCategoryClick = { category -> showCategoryProducts = category },
                onChatbotClick = onChatbotClick
            )
        }

        // Category Products Screen
        if (showCategoryProducts != null) {
            CategoryProductsScreen(
                category = showCategoryProducts!!,
                viewModel = viewModel,
                onBackClick = { showCategoryProducts = null },
                onProductClick = { product -> showProductDetail = product }
            )
        }

        // Product Detail Screen
        if (showProductDetail != null) {
            ProductDetailScreenSimple(
                product = showProductDetail!!,
                onBackClick = { showProductDetail = null },
                onAddToCart = { product, quantity ->
                    // TODO: Add to cart logic
                    showProductDetail = null
                }
            )
        }

        // Chatbot FAB
        if (showProductDetail == null && showCategoryProducts == null) {
            FloatingActionButton(
                onClick = onChatbotClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = Color(0xFF0B8FAC)
            ) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = "Chat",
                    tint = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    uiState: ProductUiState,
    viewModel: ProductViewModel,
    onProductClick: (ProductModel) -> Unit,
    onCategoryClick: (String) -> Unit,
    onChatbotClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Top Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF0B8FAC),
            shadowElevation = 4.dp
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                        .clickable { /* TODO: Open search */ },
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

        // Scrollable Content
        when (val state = uiState) {
            is ProductUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
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
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { CategoriesSection(onCategoryClick = onCategoryClick) }

                    if (bestSellers.isNotEmpty()) {
                        item {
                            ProductSection(
                                title = "Best Sellers",
                                subtitle = "Most popular this week",
                                icon = "⚡",
                                products = bestSellers,
                                onProductClick = onProductClick,
                                onViewAllClick = { onCategoryClick("All") }
                            )
                        }
                    }

                    if (featuredProducts.isNotEmpty()) {
                        item {
                            ProductSection(
                                title = "Featured Brand Of Week",
                                subtitle = "Exclusive deals",
                                icon = "🏆",
                                products = featuredProducts,
                                onProductClick = onProductClick,
                                showDiscount = true,
                                onViewAllClick = { onCategoryClick("Featured") }
                            )
                        }
                    }

                    item {
                        ProductSection(
                            title = "Popular Products",
                            subtitle = "Customer Likes",
                            icon = "🔥",
                            products = allProducts.take(10),
                            onProductClick = onProductClick,
                            onViewAllClick = { onCategoryClick("All") }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }

            is ProductUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
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
private fun CategoriesSection(onCategoryClick: (String) -> Unit) {
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
            Text(text = category.icon, fontSize = 32.sp)
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
    icon: String,
    products: List<ProductModel>,
    onProductClick: (ProductModel) -> Unit,
    showDiscount: Boolean = false,
    onViewAllClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "$icon ", fontSize = 20.sp)
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                Text(text = subtitle, fontSize = 12.sp, color = Color.Gray)
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

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products.take(10)) { product ->
                ProductCard(
                    product = product,
                    onClick = { onProductClick(product) },
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
        Column(modifier = Modifier.padding(12.dp)) {
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

                IconButton(
                    onClick = { },
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

            Button(
                onClick = { /* TODO: Add to cart */ },
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

// Simple Category Products Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryProductsScreen(
    category: String,
    viewModel: ProductViewModel,
    onBackClick: () -> Unit,
    onProductClick: (ProductModel) -> Unit
) {
    LaunchedEffect(category) {
        if (category == "All") {
            viewModel.loadAllProducts()
        } else if (category == "Featured") {
            viewModel.loadFeaturedProducts()
        } else {
            viewModel.loadProductsByCategory(category)
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(category) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
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
        when (val state = uiState) {
            is ProductUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF0B8FAC))
                }
            }

            is ProductUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Color(0xFFF5F5F5)),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.products) { product ->
                        ProductListItemSimple(
                            product = product,
                            onClick = { onProductClick(product) }
                        )
                    }
                }
            }

            is ProductUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message)
                }
            }

            else -> {}
        }
    }
}

@Composable
private fun ProductListItemSimple(
    product: ProductModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = product.imageUrl.ifEmpty { null },
                contentDescription = product.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = product.category,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "रु ${product.price.toInt()}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B8FAC)
                )
            }
        }
    }
}

// Simple Product Detail Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDetailScreenSimple(
    product: ProductModel,
    onBackClick: () -> Unit,
    onAddToCart: (ProductModel, Int) -> Unit
) {
    var quantity by remember { mutableStateOf(1) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0B8FAC),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomAppBar(containerColor = Color.White) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total Price", fontSize = 12.sp, color = Color.Gray)
                        Text(
                            "रु ${(product.price * quantity).toInt()}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0B8FAC)
                        )
                    }

                    Button(
                        onClick = { onAddToCart(product, quantity) },
                        enabled = product.inStock,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0B8FAC)
                        )
                    ) {
                        Text("Add to Cart")
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                AsyncImage(
                    model = product.imageUrl.ifEmpty { null },
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color(0xFFF5F5F5)),
                    contentScale = ContentScale.Fit
                )
            }

            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = product.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = product.category,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "रु ${product.price.toInt()}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0B8FAC)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (product.inStock) {
                        Text("Quantity", fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            IconButton(
                                onClick = { if (quantity > 1) quantity-- },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF5F5F5))
                            ) {
                                Icon(Icons.Default.Remove, "Decrease")
                            }
                            Text(
                                text = quantity.toString(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(
                                onClick = { if (quantity < product.stock) quantity++ },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0B8FAC))
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    "Increase",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Description", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = product.description.ifEmpty { "No description available" },
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}