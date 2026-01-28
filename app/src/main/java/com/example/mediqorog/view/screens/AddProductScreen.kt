package com.example.mediqorog.view.screens

import android.app.Activity
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mediqorog.R
import com.example.mediqorog.model.ProductModel
import com.example.mediqorog.repository.CommonRepoImpl
import com.example.mediqorog.repository.ProductRepositoryImpl
import com.example.mediqorog.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val activity = context as? Activity

    var productName by remember { mutableStateOf("") }
    var productDescription by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var productStock by remember { mutableStateOf("") }
    var selectedCategories by remember { mutableStateOf<Set<String>>(emptySet()) }
    var expanded by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val categories = listOf("Pharmacy", "Family Care", "Personal Care", "Supplements", "Surgical", "Devices")

    val productRepo = remember { ProductRepositoryImpl() }
    val commonRepo = remember { CommonRepoImpl() }
    val viewModel = remember { ProductViewModel(productRepo, commonRepo) }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    // Function to reset form
    fun resetForm() {
        productName = ""
        productDescription = ""
        productPrice = ""
        productStock = ""
        selectedCategories = emptySet()
        selectedImageUri = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Product", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0B8FAC),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Image Picker Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        imagePickerLauncher.launch("image/*")
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF5F5F5)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Selected Product Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.new_mediqor),
                                contentDescription = "Placeholder",
                                modifier = Modifier.size(80.dp),
                                alpha = 0.5f
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tap to select product image",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = productName,
                onValueChange = { productName = it },
                label = { Text("Product Name") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            OutlinedTextField(
                value = productDescription,
                onValueChange = { productDescription = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                enabled = !isLoading
            )

            OutlinedTextField(
                value = productPrice,
                onValueChange = { productPrice = it },
                label = { Text("Price (NPR)") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            OutlinedTextField(
                value = productStock,
                onValueChange = { productStock = it },
                label = { Text("Stock Quantity") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            // Multiple Category Selection
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Categories (Select multiple)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )

                // Selected Categories Display
                if (selectedCategories.isNotEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        selectedCategories.chunked(2).forEach { rowCategories ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowCategories.forEach { category ->
                                    AssistChip(
                                        onClick = {
                                            if (!isLoading) {
                                                selectedCategories = selectedCategories - category
                                            }
                                        },
                                        label = { Text(category, fontSize = 12.sp) },
                                        trailingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remove",
                                                modifier = Modifier.size(16.dp)
                                            )
                                        },
                                        colors = AssistChipDefaults.assistChipColors(
                                            containerColor = Color(0xFF0B8FAC),
                                            labelColor = Color.White,
                                            trailingIconContentColor = Color.White
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                // Fill remaining space if odd number
                                if (rowCategories.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                // Category Selection Dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded && !isLoading }
                ) {
                    OutlinedTextField(
                        value = if (selectedCategories.isEmpty()) "Select categories" else "${selectedCategories.size} selected",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Add Categories") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        enabled = !isLoading
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(category)
                                        if (selectedCategories.contains(category)) {
                                            Icon(
                                                painter = painterResource(android.R.drawable.checkbox_on_background),
                                                contentDescription = "Selected",
                                                tint = Color(0xFF0B8FAC),
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    selectedCategories = if (selectedCategories.contains(category)) {
                                        selectedCategories - category
                                    } else {
                                        selectedCategories + category
                                    }
                                    // Don't close dropdown, allow multiple selection
                                }
                            )
                        }

                        Divider()

                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Done",
                                    color = Color(0xFF0B8FAC),
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            onClick = { expanded = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Add Product Button
            Button(
                onClick = {
                    if (productName.isBlank() || productPrice.isBlank()) {
                        Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (selectedImageUri == null) {
                        Toast.makeText(context, "Please select a product image", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (selectedCategories.isEmpty()) {
                        Toast.makeText(context, "Please select at least one category", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val priceValue = productPrice.toDoubleOrNull()
                    if (priceValue == null || priceValue <= 0) {
                        Toast.makeText(context, "Please enter a valid price", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val stockValue = productStock.toIntOrNull() ?: 0

                    isLoading = true

                    // Join categories with comma separator
                    val categoriesString = selectedCategories.joinToString(",")

                    val product = ProductModel(
                        id = "",
                        name = productName,
                        price = priceValue,
                        description = productDescription,
                        imageUrl = "",
                        imagePublicId = "",
                        category = categoriesString, // Multiple categories
                        stock = stockValue,
                        isFeatured = false,
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )

                    viewModel.addProduct(
                        context = context,
                        imageUri = selectedImageUri,
                        product = product
                    ) { success, message ->
                        isLoading = false
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        if (success) {
                            // Reset form instead of closing
                            resetForm()
                            Toast.makeText(context, "Product added! You can add another one.", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0B8FAC)
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text("ADD PRODUCT", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Optional: Add a "Save & Close" button
            OutlinedButton(
                onClick = {
                    if (productName.isBlank() || productPrice.isBlank()) {
                        Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                        return@OutlinedButton
                    }

                    if (selectedImageUri == null) {
                        Toast.makeText(context, "Please select a product image", Toast.LENGTH_SHORT).show()
                        return@OutlinedButton
                    }

                    if (selectedCategories.isEmpty()) {
                        Toast.makeText(context, "Please select at least one category", Toast.LENGTH_SHORT).show()
                        return@OutlinedButton
                    }

                    val priceValue = productPrice.toDoubleOrNull()
                    if (priceValue == null || priceValue <= 0) {
                        Toast.makeText(context, "Please enter a valid price", Toast.LENGTH_SHORT).show()
                        return@OutlinedButton
                    }

                    val stockValue = productStock.toIntOrNull() ?: 0

                    isLoading = true

                    val categoriesString = selectedCategories.joinToString(",")

                    val product = ProductModel(
                        id = "",
                        name = productName,
                        price = priceValue,
                        description = productDescription,
                        imageUrl = "",
                        imagePublicId = "",
                        category = categoriesString,
                        stock = stockValue,
                        isFeatured = false,
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )

                    viewModel.addProduct(
                        context = context,
                        imageUri = selectedImageUri,
                        product = product
                    ) { success, message ->
                        isLoading = false
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        if (success) {
                            // Close after saving
                            activity?.finish() ?: onBackClick()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF0B8FAC)
                ),
                enabled = !isLoading
            ) {
                Text("SAVE & CLOSE", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}