package com.example.mediqorog.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.mediqorog.repository.CommonRepoImpl
import com.example.mediqorog.repository.ProductRepositoryImpl
import com.example.mediqorog.utils.ProductGridItem
import com.example.mediqorog.viewmodel.CartViewModel
import com.example.mediqorog.viewmodel.CartViewModelFactory
import com.example.mediqorog.viewmodel.ProductUiState
import com.example.mediqorog.viewmodel.ProductViewModel
import com.example.mediqorog.viewmodel.ProductViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class SurgicalActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SurgicalScreen(onBackClick = { finish() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurgicalScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val productRepo = remember { ProductRepositoryImpl() }
    val commonRepo = remember { CommonRepoImpl() }
    val productViewModel: ProductViewModel = viewModel(
        factory = ProductViewModelFactory(productRepo, commonRepo)
    )

    val cartRepo = remember { CartRepositoryImpl() }
    val cartViewModel: CartViewModel = viewModel(
        factory = CartViewModelFactory(cartRepo)
    )

    val productUiState by productViewModel.uiState.collectAsState()
    val cartUiState by cartViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        productViewModel.loadProductsByCategory("Surgical")
    }

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
            TopAppBar(
                title = { Text("Surgical", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0B8FAC),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            when (productUiState) {
                is ProductUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF0B8FAC)
                    )
                }
                is ProductUiState.Success -> {
                    val products = (productUiState as ProductUiState.Success).products

                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .padding(16.dp)
                        ) {
                            Column {
                                Text("⚕️ Surgical Products", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("${products.size} products available", fontSize = 14.sp, color = Color.Gray)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        if (products.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No products available", fontSize = 16.sp, color = Color.Gray)
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
                is ProductUiState.Error -> {
                    val errorMessage = (productUiState as ProductUiState.Error).message
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Error: $errorMessage",
                            fontSize = 16.sp,
                            color = Color.Red
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { productViewModel.loadProductsByCategory("Surgical") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0B8FAC)
                            )
                        ) {
                            Text("Retry")
                        }
                    }
                }
                is ProductUiState.Idle -> {}
            }
        }
    }
}