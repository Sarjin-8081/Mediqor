package com.example.mediqorog.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mediqorog.components.*
import com.example.mediqorog.ui.components.EditProductDialog
import com.example.mediqorog.viewmodel.AdminProductsViewModel
import com.example.mediqorog.view.screens.AddProductScreen

@Composable
fun AdminProductsScreenContent() {
    val context = LocalContext.current
    val viewModel: AdminProductsViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    // State to control navigation to AddProductScreen
    var showAddProductScreen by remember { mutableStateOf(false) }

    // Handle messages
    LaunchedEffect(uiState.error, uiState.successMessage) {
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
        uiState.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    // Show AddProductScreen if requested
    if (showAddProductScreen) {
        AddProductScreen(
            onBackClick = {
                showAddProductScreen = false
            }
        )
    } else {
        // Main products list screen
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddProductScreen = true },
                    containerColor = Color(0xFF0B8FAC),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Product")
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF5F5F5))
            ) {
                // Header
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Products Management",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0B8FAC)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Search Bar
                        SearchBar(
                            query = uiState.searchQuery,
                            onQueryChange = { viewModel.searchProducts(it) },
                            placeholder = "Search products..."
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Category Filter
                        val categories = listOf(
                            "All",
                            "Pharmacy",
                            "Family Care",
                            "Personal Care",
                            "Surgical",
                            "Devices",
                            "Supplements"
                        )
                        FilterDropdown(
                            selectedValue = uiState.selectedCategory,
                            options = categories,
                            onValueChange = { viewModel.filterByCategory(it) },
                            label = "Category"
                        )
                    }
                }

                // Products List
                if (uiState.isLoading) {
                    LoadingIndicator()
                } else if (uiState.filteredProducts.isEmpty()) {
                    EmptyState(
                        icon = Icons.Default.Inventory,
                        message = "No products found"
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.filteredProducts) { product ->
                            var showEditDialog by remember { mutableStateOf(false) }
                            var showDeleteDialog by remember { mutableStateOf(false) }

                            ProductCard(
                                product = product,
                                onEdit = { showEditDialog = true },
                                onDelete = { showDeleteDialog = true }
                            )

                            if (showEditDialog) {
                                EditProductDialog(
                                    product = product,
                                    onDismiss = { showEditDialog = false },
                                    onSave = { updatedProduct, imageUri ->
                                        viewModel.updateProduct(context, updatedProduct, imageUri)
                                        showEditDialog = false
                                    }
                                )
                            }

                            if (showDeleteDialog) {
                                AlertDialog(
                                    onDismissRequest = { showDeleteDialog = false },
                                    title = { Text("Delete Product") },
                                    text = { Text("Are you sure you want to delete ${product.name}?") },
                                    confirmButton = {
                                        Button(
                                            onClick = {
                                                viewModel.deleteProduct(product.id)
                                                showDeleteDialog = false
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color.Red
                                            )
                                        ) {
                                            Text("Delete")
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { showDeleteDialog = false }) {
                                            Text("Cancel")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}