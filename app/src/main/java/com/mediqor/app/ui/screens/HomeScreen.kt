package com.mediqor.app.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mediqor.app.model.CategoryModel
import com.mediqor.app.model.ProductModel
import com.mediqor.app.viewmodel.HomeViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel, navController: NavController? = null) {
    val categories by viewModel.categories.collectAsState()
    val featuredProducts by viewModel.featuredProducts.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                viewModel.searchProducts(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Search medicines, products...") },
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = "Search")
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color(0xFF0B8FAC),
                unfocusedBorderColor = Color.LightGray
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Categories Section
        Text(
            text = "Categories",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .height(280.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categories) { category ->
                CategoryCard(
                    category = category,
                    onClick = {
                        navController?.navigate(category.route)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Shop Button
        Button(
            onClick = { /* Handle shop click */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF5FABA5)
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = "SHOP",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Featured Products Section
        Text(
            text = "Featured Products",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Products Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .height(600.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(featuredProducts) { product ->
                ProductCard(product = product)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun CategoryCard(
    category: CategoryModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF8E1)
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = category.iconRes),
                contentDescription = category.name,
                modifier = Modifier.size(48.dp),
                tint = Color(0xFF0B8FAC)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = category.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
fun ProductCard(product: ProductModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clickable { /* Handle product click */ },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Product Image - Using placeholder since imageUrl is empty
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                // If you have image URLs, use Coil or Glide here
                // For now using a placeholder icon
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                    contentDescription = product.name,
                    modifier = Modifier.size(80.dp),
                    tint = Color(0xFF0B8FAC).copy(alpha = 0.3f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Product Name
            Text(
                text = product.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            // Product Price
            Text(
                text = "Rs. ${product.price}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0B8FAC)
            )
        }
    }
}