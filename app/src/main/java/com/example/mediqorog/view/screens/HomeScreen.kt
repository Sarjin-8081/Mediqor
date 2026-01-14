package com.example.mediqorog.view.screens

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mediqorog.view.*
import com.example.mediqorog.viewmodel.HomeViewModel

@Composable
fun DashboardHomeScreen(
    onChatbotClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel()
    Scaffold(
        floatingActionButton = {
            FloatingChatbotButton(
                onClick = {
                    Log.d("DashboardHome", "Chatbot button clicked!")
                    onChatbotClick()
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.height(260.dp),
                    userScrollEnabled = false
                ) {
                    items(homeViewModel.categories) { category ->
                        CategoryCard(category) {
                            when (category.title) {
                                "Pharmacy" ->
                                    context.startActivity(
                                        Intent(context, PharmacyActivity::class.java)
                                    )
                                "Family Care" ->
                                    context.startActivity(
                                        Intent(context, FamilyCareActivity::class.java)
                                    )
                                "Personal Care" ->
                                    context.startActivity(
                                        Intent(context, PersonalCareActivity::class.java)
                                    )
                                "Supplements" ->
                                    context.startActivity(
                                        Intent(context, SupplementsActivity::class.java)
                                    )
                                "Surgical" ->
                                    context.startActivity(
                                        Intent(context, SurgicalActivity::class.java)
                                    )
                                "Devices" ->
                                    context.startActivity(
                                        Intent(context, DevicesActivity::class.java)
                                    )
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(Color(0xFFAAD9D1), RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("SHOP", fontSize = 20.sp, color = Color.White)
                }
            }
            item { Spacer(modifier = Modifier.height(20.dp)) }
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.height(520.dp),
                    userScrollEnabled = false
                ) {
                    items(homeViewModel.products.value) { product ->
                        ProductCard(
                            product = product,
                            onAddToCart = {
                                homeViewModel.addToCart(product)
                                Log.d("DashboardHome", "Added ${product.name} to cart")
                            },
                            onFavoriteClick = {
                                homeViewModel.toggleFavorite(product.id)
                                Log.d("DashboardHome", "Toggled favorite for ${product.name}")
                            }
                        )
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}