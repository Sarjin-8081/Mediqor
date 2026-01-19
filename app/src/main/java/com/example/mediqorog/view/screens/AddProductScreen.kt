// ============================================
// FILE 2: AddProductScreen.kt - FIXED VERSION
// ============================================
package com.example.mediqorog.view.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()

    var productName by remember { mutableStateOf("") }
    var productDescription by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Pharmacy") }
    var expanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val categories = listOf(
        "Pharmacy", "Family Care", "Personal Care",
        "Supplements", "Surgical", "Devices"
    )

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

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    if (!isLoading) expanded = !expanded
                }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
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
                            text = { Text(category) },
                            onClick = {
                                selectedCategory = category
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    when {
                        productName.isBlank() -> {
                            Toast.makeText(
                                context,
                                "Please enter product name",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        productPrice.isBlank() -> {
                            Toast.makeText(
                                context,
                                "Please enter product price",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        productPrice.toDoubleOrNull() == null -> {
                            Toast.makeText(
                                context,
                                "Please enter a valid price",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        productPrice.toDoubleOrNull()!! <= 0 -> {
                            Toast.makeText(
                                context,
                                "Price must be greater than 0",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {
                            isLoading = true
                            val product = hashMapOf(
                                "name" to productName,
                                "description" to productDescription,
                                "price" to productPrice.toDouble(),
                                "category" to selectedCategory,
                                "inStock" to true,
                                "createdAt" to System.currentTimeMillis()
                            )

                            firestore.collection("products")
                                .add(product)
                                .addOnSuccessListener {
                                    isLoading = false
                                    Toast.makeText(
                                        context,
                                        "✅ Product added successfully!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onBackClick()
                                }
                                .addOnFailureListener { e ->
                                    isLoading = false
                                    Toast.makeText(
                                        context,
                                        "❌ Error: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
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
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("ADD PRODUCT", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}