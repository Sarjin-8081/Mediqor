package com.mediqor.app.ui.screens.dashboard



import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mediqor.app.R

@Composable
fun DashboardHomeScreen() {

    val categories = listOf(
        CategoryItem("Pharmacy", R.drawable.mediqor),
        CategoryItem("Family Care", R.drawable.mediqor),
        CategoryItem("Personal Care", R.drawable.mediqor),
        CategoryItem("Supplements", R.drawable.mediqor),
        CategoryItem("Surgical", R.drawable.mediqor),
        CategoryItem("Devices", R.drawable.mediqor)
    )

    val products = listOf(
        ProductItem("Zandu Balm Ultra Power-8ml", R.drawable.mediqor),
        ProductItem("Vicks Vaporub", R.drawable.mediqor),
        ProductItem("The Derma Co 10% Niacinamide Serum - 30ml", R.drawable.mediqor),
        ProductItem("Minimalist 02% Alpha Arbutin Face Serum | 30ml", R.drawable.mediqor)
    )

    Scaffold(
        bottomBar = { BottomNavBar() },
        modifier = Modifier.fillMaxSize()
    ) { padding ->

        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // === TOP BAR ===
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = R.drawable.mediqor),
                    contentDescription = "Logo",
                    modifier = Modifier.size(50.dp)
                )

                Icon(
                    painter = painterResource(id = R.drawable.baseline_search_24),
                    contentDescription = "Search",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable { },
                    tint = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // === CATEGORIES GRID ===
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.height(260.dp),
                userScrollEnabled = false
            ) {
                items(categories) { category ->
                    CategoryCard(category)
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            // === SHOP BUTTON ===
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Color(0xFFAAD9D1), RoundedCornerShape(25.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("SHOP", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // === PRODUCT GRID ===
            LazyVerticalGrid(columns = GridCells.Fixed(2), content = {
                items(products) { product ->
                    ProductCard(product)
                }
            })
        }
    }
}

@Composable
fun CategoryCard(item: CategoryItem) {
    Column(
        modifier = Modifier
            .padding(6.dp)
            .background(Color(0xFFF6EDDF), RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .height(110.dp)
            .clickable {},
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = item.icon),
            contentDescription = item.title,
            modifier = Modifier.size(45.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(item.title, fontSize = 13.sp)
    }
}

@Composable
fun ProductCard(item: ProductItem) {
    Column(
        Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            shape = RoundedCornerShape(15.dp)
        ) {
            Image(
                painter = painterResource(id = item.image),
                contentDescription = item.title,
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(item.title, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun BottomNavBar() {
    NavigationBar(
        containerColor = Color.White
    ) {
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") }
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Cart") }
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Default.LocationOn, contentDescription = "Location") }
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") }
        )
    }
}

// === MODELS ===
data class CategoryItem(val title: String, val icon: Int)
data class ProductItem(val title: String, val image: Int)