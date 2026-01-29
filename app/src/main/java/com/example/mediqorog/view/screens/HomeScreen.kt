package com.example.mediqorog.view.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mediqorog.R
import com.example.mediqorog.repository.CartRepositoryImpl
import com.example.mediqorog.ui.components.CategoryCard
import com.example.mediqorog.utils.ProductGridItem
import com.example.mediqorog.view.*
import com.example.mediqorog.viewmodel.CartViewModel
import com.example.mediqorog.viewmodel.CartViewModelFactory
import com.example.mediqorog.viewmodel.HomeViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onChatbotClick: () -> Unit,
    navController: NavController? = null
) {
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel()
    var searchQuery by remember { mutableStateOf("") }

    // Collect products from StateFlow
    val products by homeViewModel.products.collectAsState()
    val isLoading by homeViewModel.isLoading.collectAsState()
    val error by homeViewModel.error.collectAsState()

    // Filter products based on search query
    val filteredProducts = remember(searchQuery, products) {
        if (searchQuery.isEmpty()) {
            products
        } else {
            products.filter { product ->
                product.name.contains(searchQuery, ignoreCase = true) ||
                        product.category.contains(searchQuery, ignoreCase = true) ||
                        product.description.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    // Initialize Cart ViewModel
    val cartRepository = CartRepositoryImpl()
    val cartViewModel: CartViewModel = viewModel(
        factory = CartViewModelFactory(cartRepository)
    )

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // Observe cart state for messages
    val cartUiState by cartViewModel.uiState.collectAsState()

    // Show toast messages
    LaunchedEffect(cartUiState.successMessage) {
        cartUiState.successMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            cartViewModel.clearMessages()
        }
    }

    LaunchedEffect(cartUiState.error) {
        cartUiState.error?.let { errorMsg ->
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
            cartViewModel.clearMessages()
        }
    }

    // Show error toast if product loading fails
    LaunchedEffect(error) {
        error?.let { errorMsg ->
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
            homeViewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0B8FAC))
                    .padding(vertical = 16.dp, horizontal = 16.dp)
            ) {
                // Logo + Search Bar Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Company Logo
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        shadowElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            val hasLogo = remember {
                                context.resources.getIdentifier(
                                    "new_mediqor",
                                    "drawable",
                                    context.packageName
                                ) != 0
                            }

                            if (hasLogo) {
                                Image(
                                    painter = painterResource(id = R.drawable.new_mediqor),
                                    contentDescription = "MediQor Logo",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp)
                                )
                            } else {
                                Text("🏥", fontSize = 32.sp)
                            }
                        }
                    }

                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                "Search medicines, products...",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.Gray
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = Color.Gray
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(28.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onChatbotClick,
                containerColor = Color(0xFF0B8FAC),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Chat, contentDescription = "Chatbot")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFFF8F0)) // Warm peachy background
        ) {
            // Categories Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFFFE5D9),
                                    Color(0xFFFFF8F0)
                                )
                            )
                        )
                        .padding(vertical = 20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Shop by Category",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2D3436)
                            )
                            Text(
                                "Find what you need",
                                fontSize = 13.sp,
                                color = Color(0xFF636E72)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .height(240.dp)
                            .padding(horizontal = 16.dp),
                        userScrollEnabled = false,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(homeViewModel.categories) { category ->
                            CategoryCard(category) {
                                when (category.title) {
                                    "Pharmacy" ->
                                        context.startActivity(Intent(context, PharmacyActivity::class.java))
                                    "Family Care" ->
                                        context.startActivity(Intent(context, FamilyCareActivity::class.java))
                                    "Personal Care" ->
                                        context.startActivity(Intent(context, PersonalCareActivity::class.java))
                                    "Surgical" ->
                                        context.startActivity(Intent(context, SurgicalActivity::class.java))
                                    "Devices" ->
                                        context.startActivity(Intent(context, DevicesActivity::class.java))
                                    "Supplements" ->
                                        context.startActivity(Intent(context, SupplementsActivity::class.java))
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // Just for you Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFFFF8F0),
                                    Color(0xFFFFE5D9)
                                )
                            )
                        )
                        .padding(vertical = 20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "✨ ",
                                    fontSize = 20.sp
                                )
                                Text(
                                    text = if (searchQuery.isEmpty()) "Just for you" else "Search Results",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2D3436)
                                )
                            }
                            Text(
                                text = if (searchQuery.isEmpty())
                                    "Handpicked products just for you"
                                else
                                    "${filteredProducts.size} products found",
                                fontSize = 13.sp,
                                color = Color(0xFF636E72)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Show loading indicator
                    if (isLoading) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF0B8FAC)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Loading products...",
                                fontSize = 14.sp,
                                color = Color(0xFF636E72)
                            )
                        }
                    }
                    // Show message if no products found
                    else if (filteredProducts.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🔍",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No products found",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF2D3436)
                            )
                            Text(
                                text = "Try a different search term",
                                fontSize = 13.sp,
                                color = Color(0xFF636E72)
                            )
                        }
                    } else {
                        // Calculate grid height dynamically
                        val gridHeight = remember(filteredProducts.size) {
                            val rows = (filteredProducts.size + 1) / 2 // 2 columns
                            (rows * 320).dp // Increased height per row for discount badge and ratings
                        }

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(gridHeight)
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            userScrollEnabled = false
                        ) {
                            items(filteredProducts) { product ->
                                ProductGridItem(
                                    product = product,
                                    onProductClick = {
                                        // Open ProductDetailActivity with product data
                                        val intent = Intent(context, ProductDetailActivity::class.java).apply {
                                            putExtra("PRODUCT_ID", product.id)
                                            putExtra("PRODUCT_NAME", product.name)
                                            putExtra("PRODUCT_PRICE", product.price)
                                            putExtra("PRODUCT_IMAGE", product.imageUrl)
                                            putExtra("PRODUCT_DESCRIPTION", product.description)
                                            putExtra("PRODUCT_CATEGORY", product.category)
                                            putExtra("PRODUCT_STOCK", product.stock)
                                        }
                                        context.startActivity(intent)
                                    },
                                    onAddToCartClick = {
                                        if (currentUserId.isNotEmpty()) {
                                            cartViewModel.addToCart(
                                                userId = currentUserId,
                                                productId = product.id,
                                                productName = product.name,
                                                productImage = product.imageUrl,
                                                price = product.price,
                                                quantity = 1,
                                                category = product.category,
                                                stock = product.stock
                                            )
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Please login to add items to cart",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Extra padding for FAB
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}