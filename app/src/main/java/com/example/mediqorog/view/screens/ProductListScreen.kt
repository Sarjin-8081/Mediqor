package com.example.mediqorog.view.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mediqorog.repository.CartRepositoryImpl
import com.example.mediqorog.ui.components.ProductCard
import com.example.mediqorog.utils.ProductGridItem
import com.example.mediqorog.view.ProductDetailActivity
import com.example.mediqorog.viewmodel.CartViewModel
import com.example.mediqorog.viewmodel.CartViewModelFactory
import com.example.mediqorog.viewmodel.ProductUiState
import com.example.mediqorog.viewmodel.ProductViewModel
import com.google.firebase.auth.FirebaseAuth

/**
 * UNIVERSAL Product List Screen
 * This screen is used inside your category activities
 * It shows the products grid with cart functionality
 */
@Composable
fun ProductListScreen(
    productUiState: ProductUiState, // Pass the state from activity
    category: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // Initialize Cart ViewModel
    val cartRepo = remember { CartRepositoryImpl() }
    val cartViewModel: CartViewModel = viewModel(
        factory = CartViewModelFactory(cartRepo)
    )

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

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        when (productUiState) {
            is ProductUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF0B8FAC)
                )
            }

            is ProductUiState.Success -> {
                val products = productUiState.products

                if (products.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No products available in this category",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(products) { product ->
                            ProductGridItem(
                                product = product,
                                onProductClick = {
                                    // Open Product Detail Activity
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

            is ProductUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Error: ${productUiState.message}",
                        fontSize = 16.sp,
                        color = Color.Red
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0B8FAC)
                        )
                    ) {
                        Text("Retry")
                    }
                }
            }

            is ProductUiState.Idle -> {
                // Initial state - show nothing or placeholder
            }
        }
    }
}