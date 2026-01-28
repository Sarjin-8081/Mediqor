package com.example.mediqorog.view.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mediqorog.R
import com.example.mediqorog.model.ProductModel
import com.example.mediqorog.repository.CartRepositoryImpl
import com.example.mediqorog.ui.components.CategoryCard
import com.example.mediqorog.ui.components.ProductCard
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
        cartUiState.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            cartViewModel.clearMessages()
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
                .background(Color(0xFFF5F5F5))
        ) {
            // Categories Section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Shop by Category",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            item { Spacer(modifier = Modifier.height(12.dp)) }

            item {
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

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // Flash Sale Section
            item {
                ProductSection(
                    title = "⚡ Flash Sale",
                    subtitle = "Ends in 2 hours!",
                    products = homeViewModel.flashSaleProducts,
                    backgroundColor = Color(0xFFFFEBEE),
                    navController = navController,
                    cartViewModel = cartViewModel,
                    currentUserId = currentUserId
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Top Selling Section
            item {
                ProductSection(
                    title = "🏆 Top Selling",
                    subtitle = "Most popular this week",
                    products = homeViewModel.topSellingProducts,
                    backgroundColor = Color(0xFFF3E5F5),
                    navController = navController,
                    cartViewModel = cartViewModel,
                    currentUserId = currentUserId
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Sunscreens Section
            item {
                ProductSection(
                    title = "☀️ Sunscreens",
                    subtitle = "Protect your skin",
                    products = homeViewModel.sunscreenProducts,
                    backgroundColor = Color(0xFFFFF9C4),
                    navController = navController,
                    cartViewModel = cartViewModel,
                    currentUserId = currentUserId
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Body Lotions Section
            item {
                ProductSection(
                    title = "🧴 Body Lotions",
                    subtitle = "Moisturize & nourish",
                    products = homeViewModel.bodyLotionProducts,
                    backgroundColor = Color(0xFFE8F5E9),
                    navController = navController,
                    cartViewModel = cartViewModel,
                    currentUserId = currentUserId
                )
            }

            // Extra padding for FAB
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun ProductSection(
    title: String,
    subtitle: String,
    products: List<ProductModel>,
    backgroundColor: Color,
    navController: NavController?,
    cartViewModel: CartViewModel,
    currentUserId: String
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
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
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            TextButton(onClick = { /* Navigate to category */ }) {
                Text("View All →", color = Color(0xFF0B8FAC))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(products) { product ->
                ProductCard(
                    product = product,
                    onProductClick = {
                        // Navigate to product detail screen
                        navController?.navigate("product_detail/${product.id}")
                            ?: Toast.makeText(
                                context,
                                "Navigation not available",
                                Toast.LENGTH_SHORT
                            ).show()
                    },
                    onAddToCartClick = {
                        // Add to cart
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