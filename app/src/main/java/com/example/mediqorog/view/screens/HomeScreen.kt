package com.example.mediqorog.view.screens

import android.content.Intent
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mediqorog.R
import com.example.mediqorog.ui.components.CategoryCard
import com.example.mediqorog.ui.components.ProductCard
import com.example.mediqorog.view.*
import com.example.mediqorog.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
import com.example.mediqorog.model.ProductModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onChatbotClick: () -> Unit,
    onAddProductClick: () -> Unit,
    isAdmin: Boolean = true
) {
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel()
    var searchQuery by remember { mutableStateOf("") }

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
                    // Company Logo (Bigger)
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        shadowElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            // Try to load your logo
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
            // Both FABs side by side
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                // Add Product FAB (Left)
                FloatingActionButton(
                    onClick = onAddProductClick,
                    containerColor = Color(0xFFB3E5FC),
                    contentColor = Color(0xFF01579B)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Product")
                }

                // Chat button (Right)
                FloatingActionButton(
                    onClick = onChatbotClick,
                    containerColor = Color(0xFF0B8FAC),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Chat, contentDescription = "Chatbot")
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {

            // Auto-Sliding Banner
            item { AutoSlidingBanner() }
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Categories Section
            item {
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
                    backgroundColor = Color(0xFFFFEBEE)
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Top Selling Section
            item {
                ProductSection(
                    title = "🏆 Top Selling",
                    subtitle = "Most popular this week",
                    products = homeViewModel.topSellingProducts,
                    backgroundColor = Color(0xFFF3E5F5)
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Sunscreens Section
            item {
                ProductSection(
                    title = "☀️ Sunscreens",
                    subtitle = "Protect your skin",
                    products = homeViewModel.sunscreenProducts,
                    backgroundColor = Color(0xFFFFF9C4)
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Body Lotions Section
            item {
                ProductSection(
                    title = "🧴 Body Lotions",
                    subtitle = "Moisturize & nourish",
                    products = homeViewModel.bodyLotionProducts,
                    backgroundColor = Color(0xFFE8F5E9)
                )
            }

            // Extra padding for FABs
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

@Composable
fun AutoSlidingBanner() {
    val banners = listOf(
        BannerData(
            title = "⚡ FLASH SALE",
            subtitle = "Up to 50% OFF on selected items",
            gradient = Brush.horizontalGradient(
                colors = listOf(Color(0xFFE91E63), Color(0xFFF06292))
            )
        ),
        BannerData(
            title = "🚀 60 MIN DELIVERY",
            subtitle = "Order now, delivered in 60 minutes",
            gradient = Brush.horizontalGradient(
                colors = listOf(Color(0xFF0B8FAC), Color(0xFF4FC3F7))
            )
        ),
        BannerData(
            title = "📅 BOOK APPOINTMENTS",
            subtitle = "Consult with doctors online",
            gradient = Brush.horizontalGradient(
                colors = listOf(Color(0xFF4CAF50), Color(0xFF81C784))
            )
        ),
        BannerData(
            title = "💊 FREE DELIVERY",
            subtitle = "On orders above NPR 1000",
            gradient = Brush.horizontalGradient(
                colors = listOf(Color(0xFFFF9800), Color(0xFFFFB74D))
            )
        )
    )

    var currentPage by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            currentPage = (currentPage + 1) % banners.size
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(banners[currentPage].gradient)
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = banners[currentPage].title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = banners[currentPage].subtitle,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(banners.size) { index ->
                Box(
                    modifier = Modifier
                        .size(if (index == currentPage) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (index == currentPage) Color(0xFF0B8FAC)
                            else Color.Gray.copy(alpha = 0.4f)
                        )
                )
                if (index < banners.size - 1) {
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }
        }
    }
}

@Composable
fun ProductSection(
    title: String,
    subtitle: String,
    products: List<com.example.mediqorog.model.ProductModel>,
    backgroundColor: Color
) {
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

            TextButton(onClick = { }) {
                Text("View All →", color = Color(0xFF0B8FAC))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(products) { product ->
                ProductCard(product)
            }
        }
    }
}

data class BannerData(
    val title: String,
    val subtitle: String,
    val gradient: Brush
)