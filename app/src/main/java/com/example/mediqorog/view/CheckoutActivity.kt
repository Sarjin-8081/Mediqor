package com.example.mediqorog.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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

    private val selectAddressLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedAddress = result.data?.getParcelableExtra<Address>("SELECTED_ADDRESS")
            selectedAddress?.let {
                // Store selected address to be picked up by the composable
                intent.putExtra("SELECTED_ADDRESS", it)
                recreate() // Recreate to update UI with selected address
            }
        }
    }

    // Payment Activity Launcher
    private val processToPayLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Toast.makeText(this, "Payment successful! Order placed.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val selectedAddress = intent.getParcelableExtra<Address>("SELECTED_ADDRESS")

        setContent {
            MaterialTheme {
                CheckoutScreenContent(
                    preSelectedAddress = selectedAddress,
                    onBackClick = { finish() },
                    onOrderPlaced = {
                        Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_LONG).show()
                        finish()
                    },
                    onSelectAddress = {
                        val intent = Intent(this, SavedAddressesActivity::class.java).apply {
                            putExtra("SELECT_MODE", true)
                        }
                        selectAddressLauncher.launch(intent)
                    },
                    onProceedToPay = { userId, total, deliveryFee, itemsTotal, itemCount ->
                        // Launch ProcessToPayActivity with address data
                        val intent = Intent(this, ProcessToPayActivity::class.java).apply {
                            putExtra(ProcessToPayActivity.EXTRA_TOTAL_AMOUNT, total)
                            putExtra(ProcessToPayActivity.EXTRA_DELIVERY_FEE, deliveryFee)
                            putExtra(ProcessToPayActivity.EXTRA_ITEMS_TOTAL, itemsTotal)
                            putExtra(ProcessToPayActivity.EXTRA_ITEM_COUNT, itemCount)
                            putExtra(ProcessToPayActivity.EXTRA_USER_ID, userId)

                            // Pass address data
                            selectedAddress?.let { addr ->
                                putExtra(ProcessToPayActivity.EXTRA_ADDRESS_NAME, addr.name)
                                putExtra(ProcessToPayActivity.EXTRA_ADDRESS_PHONE, addr.phone)
                                putExtra(ProcessToPayActivity.EXTRA_ADDRESS_LINE, addr.addressLine)
                                putExtra(ProcessToPayActivity.EXTRA_ADDRESS_LANDMARK, addr.landmark)
                            }
                        }
                        processToPayLauncher.launch(intent)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreenContent(
    preSelectedAddress: Address?,
    onBackClick: () -> Unit,
    onOrderPlaced: () -> Unit,
    onSelectAddress: () -> Unit,
    onProceedToPay: (String, Double, Double, Double, Int) -> Unit
) {
    val context = LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // Initialize Checkout ViewModel
    val checkoutRepo = remember { CheckoutRepositoryImpl() }
    val checkoutViewModel: CheckoutViewModel = viewModel(
        factory = CheckoutViewModelFactory(checkoutRepo)
    )

    val checkoutUiState by checkoutViewModel.uiState.collectAsState()
    var selectedAddress by remember { mutableStateOf(preSelectedAddress) }

    // Load cart items when screen opens
    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            checkoutViewModel.loadCartItems(currentUserId)
        } else {
            Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show()
        }
    }

    // Auto-populate fields when address is selected
    LaunchedEffect(selectedAddress) {
        selectedAddress?.let { addr ->
            checkoutViewModel.updateFullName(addr.name)
            checkoutViewModel.updatePhoneNumber(addr.phone)
            checkoutViewModel.updateAddress(addr.addressLine)
            checkoutViewModel.updateColonyLandmark(addr.landmark)
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
                    // Delivery Address Selection Card
                    item {
                        DeliveryAddressSelectionCard(
                            selectedAddress = selectedAddress,
                            onSelectAddress = onSelectAddress
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

                    // Proceed to Pay Button - UPDATED
                    item {
                        Button(
                            onClick = {
                                if (selectedAddress == null) {
                                    Toast.makeText(
                                        context,
                                        "Please select a delivery address",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else if (currentUserId.isNotEmpty()) {
                                    // Launch ProcessToPayActivity
                                    onProceedToPay(
                                        currentUserId,
                                        checkoutUiState.total,
                                        checkoutUiState.deliveryFee,
                                        checkoutUiState.itemsTotal,
                                        checkoutUiState.cartItems.size
                                    )
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
                            enabled = !checkoutUiState.isLoading &&
                                    checkoutUiState.cartItems.isNotEmpty() &&
                                    selectedAddress != null
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

@Composable
private fun DeliveryAddressSelectionCard(
    selectedAddress: Address?,
    onSelectAddress: () -> Unit
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
                    text = "Delivery Address",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                TextButton(
                    onClick = onSelectAddress
                ) {
                    Text(
                        text = if (selectedAddress == null) "Select" else "Change",
                        color = Color(0xFF0B8FAC),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (selectedAddress != null) {
                // Display selected address
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectAddress() },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF0F9FF)
                    ),
                    border = BorderStroke(1.dp, Color(0xFF0B8FAC))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                selectedAddress.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (selectedAddress.isDefault) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF10B981)
                                ) {
                                    Text(
                                        "DEFAULT",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontSize = 9.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.Phone,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                selectedAddress.phone,
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            selectedAddress.addressLine,
                            fontSize = 14.sp,
                            color = Color(0xFF374151),
                            lineHeight = 20.sp
                        )

                        if (selectedAddress.landmark.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Near ${selectedAddress.landmark}",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (selectedAddress.city.isNotEmpty()) {
                                Text(
                                    selectedAddress.city,
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                            if (selectedAddress.pincode.isNotEmpty()) {
                                Text(
                                    "- ${selectedAddress.pincode}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            } else {
                // No address selected - show prompt
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectAddress() },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFEF3C7)
                    ),
                    border = BorderStroke(1.dp, Color(0xFFF59E0B))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(24.dp)
                        )
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "No address selected",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF92400E)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "Tap to select a delivery address",
                                fontSize = 12.sp,
                                color = Color(0xFFA16207)
                            )
                        }
                        Icon(
                            Icons.Default.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B)
                        )
                    }
                }
            }
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
                border = BorderStroke(1.dp, Color(0xFF0B8FAC))
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