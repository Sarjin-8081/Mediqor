package com.example.mediqorog.view.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.mediqorog.repository.CartRepositoryImpl
import com.example.mediqorog.viewmodel.CartViewModel
import com.example.mediqorog.viewmodel.CartViewModelFactory
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen() {
    val context = LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // Initialize Cart ViewModel
    val cartRepo = remember { CartRepositoryImpl() }
    val cartViewModel: CartViewModel = viewModel(
        factory = CartViewModelFactory(cartRepo)
    )

    val cartUiState by cartViewModel.uiState.collectAsState()

    // Load cart when screen opens
    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            cartViewModel.loadCart(currentUserId)
        }
    }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Shopping Cart",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0B8FAC),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            if (cartUiState.cartItems.isNotEmpty()) {
                CartBottomBar(
                    total = cartUiState.cartTotal,
                    itemCount = cartUiState.itemCount,
                    onCheckout = {
                        Toast.makeText(context, "Checkout coming soon!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            when {
                currentUserId.isEmpty() -> {
                    EmptyCartView(
                        icon = "🔐",
                        title = "Please Login",
                        subtitle = "Login to view your cart"
                    )
                }

                cartUiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF0B8FAC))
                    }
                }

                cartUiState.cartItems.isEmpty() -> {
                    EmptyCartView(
                        icon = "🛒",
                        title = "Your Cart is Empty",
                        subtitle = "Add medicines to your cart"
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = cartUiState.cartItems,
                            key = { it.id }
                        ) { cartItem ->
                            CartItemCard(
                                cartItem = cartItem,
                                onQuantityChange = { newQuantity ->
                                    cartViewModel.updateQuantity(cartItem.id, newQuantity)
                                },
                                onRemove = {
                                    cartViewModel.removeFromCart(cartItem.id)
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemCard(
    cartItem: CartModel,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(cartItem.productImage),
                contentDescription = cartItem.productName,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Text(
                    text = cartItem.productName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Rs. ${cartItem.price.toInt()}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B8FAC)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (cartItem.quantity > 1) {
                                onQuantityChange(cartItem.quantity - 1)
                            }
                        },
                        modifier = Modifier.size(28.dp),
                        enabled = cartItem.quantity > 1
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            contentDescription = "Decrease",
                            tint = if (cartItem.quantity > 1) Color.Black else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF5F5F5)
                    ) {
                        Text(
                            text = cartItem.quantity.toString(),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = {
                            if (cartItem.quantity < cartItem.stock) {
                                onQuantityChange(cartItem.quantity + 1)
                            }
                        },
                        modifier = Modifier.size(28.dp),
                        enabled = cartItem.quantity < cartItem.stock
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Increase",
                            tint = if (cartItem.quantity < cartItem.stock) Color.Black else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Remove",
                            tint = Color.Red,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                if (cartItem.quantity >= cartItem.stock) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Max stock reached",
                        fontSize = 11.sp,
                        color = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
private fun CartBottomBar(
    total: Double,
    itemCount: Int,
    onCheckout: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = Color.White
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
                Column {
                    Text(
                        text = "Total ($itemCount items)",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Rs. ${total.toInt()}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0B8FAC)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onCheckout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0B8FAC)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "Proceed to Checkout",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun EmptyCartView(
    icon: String,
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = icon,
            fontSize = 64.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = subtitle,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}