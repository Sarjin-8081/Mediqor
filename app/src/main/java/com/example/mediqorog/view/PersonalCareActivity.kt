package com.example.mediqorog.view

import android.os.Bundle
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mediqorog.model.ProductModel
import com.example.mediqorog.repository.CommonRepoImpl
import com.example.mediqorog.repository.ProductRepositoryImpl
import com.example.mediqorog.utils.ProductGridItem
import com.example.mediqorog.viewmodel.ProductUiState
import com.example.mediqorog.viewmodel.ProductViewModel

class PersonalCareActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PersonalCareScreen(onBackClick = { finish() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalCareScreen(onBackClick: () -> Unit) {
    val productRepo = remember { ProductRepositoryImpl() }
    val commonRepo = remember { CommonRepoImpl() }
    val viewModel = remember { ProductViewModel(productRepo, commonRepo) }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProductsByCategory("Personal Care")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Personal Care", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
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
            when (uiState) {
                is ProductUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF0B8FAC)
                    )
                }
                is ProductUiState.Success -> {
                    val products = (uiState as ProductUiState.Success).products

                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .padding(16.dp)
                        ) {
                            Column {
                                Text("🧴 Personal Care Products", fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
                                        onProductClick = {},
                                        onAddToCartClick = {}
                                    )
                                }
                            }
                        }
                    }
                }
                is ProductUiState.Error -> {
                    val errorMessage = (uiState as ProductUiState.Error).message

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
                            onClick = { viewModel.loadProductsByCategory("Personal Care") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0B8FAC)
                            )
                        ) {
                            Text("Retry")
                        }
                    }
                }
                is ProductUiState.Idle -> {
                    // Initial state before data loading starts
                }
            }
        }
    }
}