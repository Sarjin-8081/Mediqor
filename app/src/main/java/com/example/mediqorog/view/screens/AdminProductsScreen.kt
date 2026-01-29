package com.example.mediqorog.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import coil.compose.AsyncImage
import com.example.mediqorog.components.*
import com.example.mediqorog.model.ProductModel
import com.example.mediqorog.viewmodel.AdminProductsViewModel

@Composable
fun AdminProductsScreenContent() {
    val context = LocalContext.current
    val viewModel: AdminProductsViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

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

    Column(
        modifier = Modifier
            .fillMaxSize()
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
                    "All", "Pharmacy", "Family Care", "Personal Care",
                    "Surgical", "Devices", "Supplements"
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

@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onProductAdded: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: AdminProductsViewModel = viewModel()

    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Pharmacy") }
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> imageUri = uri }

    val categories = listOf(
        "Pharmacy", "Family Care", "Personal Care",
        "Surgical", "Devices", "Supplements"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Product") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Image Picker
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Image, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (imageUri == null) "Select Image" else "Image Selected")
                }

                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Product Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    )
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Product Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                FilterDropdown(
                    selectedValue = category,
                    options = categories,
                    onValueChange = { category = it },
                    label = "Category"
                )

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price (₹)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Stock Quantity") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank() || price.isBlank()) {
                        Toast.makeText(
                            context,
                            "Please fill all required fields",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    val product = ProductModel(
                        name = name,
                        category = category,
                        price = price.toDoubleOrNull() ?: 0.0,
                        stock = stock.toIntOrNull() ?: 0,
                        description = description
                    )

                    viewModel.addProduct(context, product, imageUri)
                    onProductAdded()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0B8FAC)
                )
            ) {
                Text("Add Product")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditProductDialog(
    product: ProductModel,
    onDismiss: () -> Unit,
    onSave: (ProductModel, Uri?) -> Unit
) {
    var name by remember { mutableStateOf(product.name) }
    var category by remember { mutableStateOf(product.category) }
    var price by remember { mutableStateOf(product.price.toString()) }
    var stock by remember { mutableStateOf(product.stock.toString()) }
    var description by remember { mutableStateOf(product.description) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> imageUri = uri }

    val categories = listOf(
        "Pharmacy", "Family Care", "Personal Care",
        "Surgical", "Devices", "Supplements"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Product") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Image, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (imageUri == null) "Change Image" else "New Image Selected")
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Product Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                FilterDropdown(
                    selectedValue = category,
                    options = categories,
                    onValueChange = { category = it },
                    label = "Category"
                )

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price (₹)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Stock Quantity") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updatedProduct = product.copy(
                        name = name,
                        category = category,
                        price = price.toDoubleOrNull() ?: 0.0,
                        stock = stock.toIntOrNull() ?: 0,
                        description = description
                    )
                    onSave(updatedProduct, imageUri)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0B8FAC)
                )
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}