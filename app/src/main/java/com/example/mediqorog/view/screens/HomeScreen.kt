package com.example.mediqorog.view.screens

import android.content.Intent
import android.util.Log
import androidx.compose.animation.core.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mediqorog.ui.components.CategoryCard
import com.example.mediqorog.ui.components.ProductCard
import com.example.mediqorog.view.AddProductActivity
import com.example.mediqorog.view.DevicesActivity
import com.example.mediqorog.view.FamilyCareActivity
import com.example.mediqorog.view.PersonalCareActivity
import com.example.mediqorog.view.PharmacyActivity
import com.example.mediqorog.view.SurgicalActivity
//import com.example.mediqorog.view.SupplementsActivity
import com.example.mediqorog.viewmodel.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlin.jvm.java

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onChatbotClick: () -> Unit) {
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel()
    val currentUser = FirebaseAuth.getInstance().currentUser

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "MediQorOG",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            "Deliver to: Kathmandu",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                actions = {
                    // Admin: Add Product Button
                    if (currentUser?.email?.contains("admin") == true) {
                        IconButton(
                            onClick = {
                                context.startActivity(Intent(context, AddProductActivity::class.java))
                            }
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add Product",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0B8FAC),
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingChatbotButton(
                onClick = {
                    Log.d("HomeScreen", "Chatbot button clicked!")
                    onChatbotClick()
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {

            // 🎯 Auto-Sliding Banner
            item {
                AutoSlidingBanner()
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // 📦 Categories Section
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
                        .height(150.dp)
                        .padding(horizontal = 16.dp),
                    userScrollEnabled = false,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
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
//                                "Supplements" ->
//                                    context.startActivity(Intent(context, SupplementsActivity::class.java))
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

            // 🔥 Flash Sale Section
            item {
                ProductSection(
                    title = "⚡ Flash Sale",
                    subtitle = "Ends in 2 hours!",
                    products = homeViewModel.flashSaleProducts,
                    backgroundColor = Color(0xFFFFEBEE)
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // 🏆 Top Selling Section
            item {
                ProductSection(
                    title = "🏆 Top Selling",
                    subtitle = "Most popular this week",
                    products = homeViewModel.topSellingProducts,
                    backgroundColor = Color(0xFFF3E5F5)
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // ☀️ Sunscreens Section
            item {
                ProductSection(
                    title = "☀️ Sunscreens",
                    subtitle = "Protect your skin",
                    products = homeViewModel.sunscreenProducts,
                    backgroundColor = Color(0xFFFFF9C4)
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // 🧴 Body Lotions Section
            item {
                ProductSection(
                    title = "🧴 Body Lotions",
                    subtitle = "Moisturize & nourish",
                    products = homeViewModel.bodyLotionProducts,
                    backgroundColor = Color(0xFFE8F5E9)
                )
            }

            // Extra padding for FAB
            item { Spacer(modifier = Modifier.height(80.dp)) }
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
            delay(3000) // Auto-slide every 3 seconds
            currentPage = (currentPage + 1) % banners.size
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(banners[currentPage].gradient)
                .clickable { /* Handle banner click */ },
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

        // Indicator Dots
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
                            if (index == currentPage) Color(0xFF0B8FAC) else Color.Gray.copy(alpha = 0.4f)
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
        // Section Header
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

            TextButton(onClick = { /* View All */ }) {
                Text("View All →", color = Color(0xFF0B8FAC))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Horizontal Scrollable Products
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

@Composable
fun FloatingChatbotButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = Color(0xFF0B8FAC),
        contentColor = Color.White,
        modifier = Modifier.size(64.dp)
    ) {
        Text("💬", fontSize = 28.sp)
    }
}

data class BannerData(
    val title: String,
    val subtitle: String,
    val gradient: Brush
)