package com.example.mediqorog.ui.theme

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.mediqorog.model.CartModel
import com.example.mediqorog.repository.CheckoutRepositoryImpl
import com.example.mediqorog.viewmodel.CheckoutViewModel
import com.example.mediqorog.viewmodel.CheckoutViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class CheckoutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cartTotal = intent.getDoubleExtra("CART_TOTAL", 0.0)
        val itemCount = intent.getIntExtra("ITEM_COUNT", 0)

        setContent {
            MaterialTheme {
                CheckoutScreenContent(
                    cartTotal = cartTotal,
                    itemCount = itemCount,
                    onBackClick = { finish() },
                    onOrderPlaced = {
                        Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_LONG).show()
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreenContent(
    cartTotal: Double,
    itemCount: Int,
    onBackClick: () -> Unit,
    onOrderPlaced: () -> Unit
) {
    val context = LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // Initialize Checkout ViewModel
    val checkoutRepo = remember { CheckoutRepositoryImpl() }
    val checkoutViewModel: CheckoutViewModel = viewModel(
        factory = CheckoutViewModelFactory(checkoutRepo)
    )

    val checkoutUiState by checkoutViewModel.uiState.collectAsState()

    // Load cart items when screen opens
    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            checkoutViewModel.loadCartItems(currentUserId)
        } else {
            Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show()
        }
    }

    // Show error messages
    LaunchedEffect(checkoutUiState.error) {
        checkoutUiState.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            checkoutViewModel.clearMessages()
        }
    }

    // Show success messages
    LaunchedEffect(checkoutUiState.successMessage) {
        checkoutUiState.successMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            checkoutViewModel.clearMessages()
        }
    }

    // Navigate back after successful order
    LaunchedEffect(checkoutUiState.orderPlaced) {
        if (checkoutUiState.orderPlaced) {
            onOrderPlaced()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Checkout",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0B8FAC),
                    titleContentColor = Color.White
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
            if (checkoutUiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF0B8FAC))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Delivery Information Section
                    item {
                        DeliveryInformationCard(
                            fullName = checkoutUiState.fullName,
                            phoneNumber = checkoutUiState.phoneNumber,
                            address = checkoutUiState.address,
                            colonyLandmark = checkoutUiState.colonyLandmark,
                            onFullNameChange = { checkoutViewModel.updateFullName(it) },
                            onPhoneNumberChange = { checkoutViewModel.updatePhoneNumber(it) },
                            onAddressChange = { checkoutViewModel.updateAddress(it) },
                            onColonyLandmarkChange = { checkoutViewModel.updateColonyLandmark(it) }
                        )
                    }

                    // Order Details Section
                    item {
                        OrderDetailCard(
                            itemsTotal = checkoutUiState.itemsTotal,
                            deliveryFee = checkoutUiState.deliveryFee,
                            total = checkoutUiState.total,
                            itemCount = checkoutUiState.cartItems.size
                        )
                    }

                    // Package Section
                    item {
                        PackageCard(
                            cartItems = checkoutUiState.cartItems,
                            deliveryFee = checkoutUiState.deliveryFee
                        )
                    }

                    // Proceed to Pay Button
                    item {
                        Button(
                            onClick = {
                                if (currentUserId.isNotEmpty()) {
                                    checkoutViewModel.placeOrder(currentUserId)
                                } else {
                                    Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0B8FAC)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !checkoutUiState.isLoading && checkoutUiState.cartItems.isNotEmpty()
                        ) {
                            if (checkoutUiState.isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text(
                                    "Proceed to Pay",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeliveryInformationCard(
    fullName: String,
    phoneNumber: String,
    address: String,
    colonyLandmark: String,
    onFullNameChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onColonyLandmarkChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Delivery Information",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Full Name
            OutlinedTextField(
                value = fullName,
                onValueChange = onFullNameChange,
                label = { Text("Full name") },
                placeholder = { Text("Enter your first and last name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0B8FAC),
                    focusedLabelColor = Color(0xFF0B8FAC)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Phone Number
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = onPhoneNumberChange,
                label = { Text("Phone Number") },
                placeholder = { Text("Please enter your phone number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0B8FAC),
                    focusedLabelColor = Color(0xFF0B8FAC)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Colony/Landmark
            OutlinedTextField(
                value = colonyLandmark,
                onValueChange = onColonyLandmarkChange,
                label = { Text("Colony / Suburb / Locality / Landmark") },
                placeholder = { Text("Please enter") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0B8FAC),
                    focusedLabelColor = Color(0xFF0B8FAC)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Address
            OutlinedTextField(
                value = address,
                onValueChange = onAddressChange,
                label = { Text("Address") },
                placeholder = { Text("For Example: House# 123, Street# 123, ABC Road") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0B8FAC),
                    focusedLabelColor = Color(0xFF0B8FAC)
                )
            )
        }
    }
}

@Composable
private fun OrderDetailCard(
    itemsTotal: Double,
    deliveryFee: Double,
    total: Double,
    itemCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Order Detail",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Items Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Items Total ($itemCount Items)",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Rs. ${itemsTotal.toInt()}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Delivery Fee
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Delivery Fee",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Rs. ${deliveryFee.toInt()}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = Color.LightGray)

            Spacer(modifier = Modifier.height(12.dp))

            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Rs. ${total.toInt()}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF6B35)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "All taxes included",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
private fun PackageCard(
    cartItems: List<CartModel>,
    deliveryFee: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Package 1 of 1",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Fulfilled by MediQorog",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Delivery Info
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFE3F2FD),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0B8FAC))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF0B8FAC),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Rs. ${deliveryFee.toInt()}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Standard Delivery",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Get by 1-2 Feb",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Cart Items
            if (cartItems.isEmpty()) {
                Text(
                    text = "No items in cart",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                cartItems.forEach { item ->
                    PackageItemRow(item)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun PackageItemRow(item: CartModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(item.productImage),
            contentDescription = item.productName,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF5F5F5)),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.productName,
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Rs. ${item.price.toInt()}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B8FAC)
                )

                if (item.quantity > 1) {
                    Text(
                        text = "Qty: ${item.quantity}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}