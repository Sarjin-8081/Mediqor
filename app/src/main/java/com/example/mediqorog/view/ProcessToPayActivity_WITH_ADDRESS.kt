package com.example.mediqorog.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mediqorog.R
import com.example.mediqorog.repository.CheckoutRepositoryImpl
import com.example.mediqorog.viewmodel.CheckoutViewModel
import com.example.mediqorog.viewmodel.CheckoutViewModelFactory
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class ProcessToPayActivity : ComponentActivity() {

    companion object {
        const val EXTRA_TOTAL_AMOUNT = "EXTRA_TOTAL_AMOUNT"
        const val EXTRA_DELIVERY_FEE = "EXTRA_DELIVERY_FEE"
        const val EXTRA_ITEMS_TOTAL = "EXTRA_ITEMS_TOTAL"
        const val EXTRA_ITEM_COUNT = "EXTRA_ITEM_COUNT"
        const val EXTRA_USER_ID = "EXTRA_USER_ID"
        const val EXTRA_ADDRESS_NAME = "EXTRA_ADDRESS_NAME"
        const val EXTRA_ADDRESS_PHONE = "EXTRA_ADDRESS_PHONE"
        const val EXTRA_ADDRESS_LINE = "EXTRA_ADDRESS_LINE"
        const val EXTRA_ADDRESS_LANDMARK = "EXTRA_ADDRESS_LANDMARK"
        private const val TAG = "ProcessToPayActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val totalAmount = intent.getDoubleExtra(EXTRA_TOTAL_AMOUNT, 0.0)
        val deliveryFee = intent.getDoubleExtra(EXTRA_DELIVERY_FEE, 0.0)
        val itemsTotal = intent.getDoubleExtra(EXTRA_ITEMS_TOTAL, 0.0)
        val itemCount = intent.getIntExtra(EXTRA_ITEM_COUNT, 0)
        val userId = intent.getStringExtra(EXTRA_USER_ID) ?: ""
        val addressName = intent.getStringExtra(EXTRA_ADDRESS_NAME) ?: ""
        val addressPhone = intent.getStringExtra(EXTRA_ADDRESS_PHONE) ?: ""
        val addressLine = intent.getStringExtra(EXTRA_ADDRESS_LINE) ?: ""
        val addressLandmark = intent.getStringExtra(EXTRA_ADDRESS_LANDMARK) ?: ""

        Log.d(TAG, "═══════════════════════════════════════")
        Log.d(TAG, "ProcessToPayActivity Created")
        Log.d(TAG, "User ID: $userId")
        Log.d(TAG, "Total: Rs. $totalAmount")
        Log.d(TAG, "Address Name: $addressName")
        Log.d(TAG, "Address Phone: $addressPhone")
        Log.d(TAG, "Address Line: $addressLine")
        Log.d(TAG, "═══════════════════════════════════════")

        setContent {
            MaterialTheme {
                ProcessToPayScreen(
                    totalAmount = totalAmount,
                    deliveryFee = deliveryFee,
                    itemsTotal = itemsTotal,
                    itemCount = itemCount,
                    userId = userId,
                    addressName = addressName,
                    addressPhone = addressPhone,
                    addressLine = addressLine,
                    addressLandmark = addressLandmark,
                    onBackClick = { finish() },
                    onPaymentSuccess = {
                        setResult(RESULT_OK)
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProcessToPayScreen(
    totalAmount: Double,
    deliveryFee: Double,
    itemsTotal: Double,
    itemCount: Int,
    userId: String,
    addressName: String,
    addressPhone: String,
    addressLine: String,
    addressLandmark: String,
    onBackClick: () -> Unit,
    onPaymentSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedPaymentMethod by remember { mutableStateOf<PaymentMethod?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    // Get CheckoutViewModel
    val checkoutRepo = remember { CheckoutRepositoryImpl() }
    val checkoutViewModel: CheckoutViewModel = viewModel(
        factory = CheckoutViewModelFactory(checkoutRepo)
    )

    // Load cart items and set address when screen opens
    LaunchedEffect(userId) {
        Log.d("ProcessToPayScreen", "Loading cart and setting address...")
        checkoutViewModel.loadCartItems(userId)
        checkoutViewModel.updateFullName(addressName)
        checkoutViewModel.updatePhoneNumber(addressPhone)
        checkoutViewModel.updateAddress(addressLine)
        checkoutViewModel.updateColonyLandmark(addressLandmark)
        Log.d("ProcessToPayScreen", "Address set: $addressName, $addressPhone, $addressLine")
    }

    val TAG = "ProcessToPayScreen"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Select Payment Method",
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
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Payment Methods Section
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Select Payment Method",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Payment Method Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // eSewa Payment
                        PaymentMethodCard(
                            modifier = Modifier.weight(1f),
                            title = "eSewa Mobile Wallet",
                            subtitle = "eSewa Mobile Wallet",
                            iconRes = R.drawable.ic_esewa,
                            isSelected = selectedPaymentMethod == PaymentMethod.ESEWA,
                            onClick = {
                                selectedPaymentMethod = PaymentMethod.ESEWA
                                Log.d(TAG, "Selected: eSewa")
                            }
                        )

                        // Cash on Delivery
                        PaymentMethodCard(
                            modifier = Modifier.weight(1f),
                            title = "Cash on Delivery",
                            subtitle = "Cash on Delivery",
                            iconRes = R.drawable.ic_cash,
                            isSelected = selectedPaymentMethod == PaymentMethod.CASH_ON_DELIVERY,
                            onClick = {
                                selectedPaymentMethod = PaymentMethod.CASH_ON_DELIVERY
                                Log.d(TAG, "Selected: Cash on Delivery")
                            }
                        )
                    }

                    // Payment Method Information
                    when (selectedPaymentMethod) {
                        PaymentMethod.ESEWA -> {
                            ESewaPaymentInfo()
                        }
                        PaymentMethod.CASH_ON_DELIVERY -> {
                            CashOnDeliveryInfo(totalAmount = totalAmount)
                        }
                        null -> {}
                    }
                }

                // Order Summary and Confirm Button
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    // Order Summary
                    OrderSummarySection(
                        itemsTotal = itemsTotal,
                        deliveryFee = deliveryFee,
                        totalAmount = totalAmount,
                        itemCount = itemCount
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Confirm Button
                    Button(
                        onClick = {
                            Log.d(TAG, "═══════════════════════════════════════")
                            Log.d(TAG, "CONFIRM BUTTON CLICKED")
                            Log.d(TAG, "Payment Method: $selectedPaymentMethod")
                            Log.d(TAG, "═══════════════════════════════════════")

                            when (selectedPaymentMethod) {
                                PaymentMethod.ESEWA -> {
                                    scope.launch {
                                        isProcessing = true
                                        Log.d(TAG, "Placing eSewa order...")
                                        checkoutViewModel.placeOrder(userId, "eSewa")
                                        kotlinx.coroutines.delay(1000)

                                        initiateESewaPayment(
                                            context = context as ComponentActivity,
                                            amount = totalAmount,
                                            userId = userId
                                        )

                                        Toast.makeText(
                                            context,
                                            "Order placed. Redirecting to eSewa...",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        isProcessing = false
                                        onPaymentSuccess()
                                    }
                                }
                                PaymentMethod.CASH_ON_DELIVERY -> {
                                    scope.launch {
                                        isProcessing = true
                                        Log.d(TAG, "Placing COD order...")
                                        checkoutViewModel.placeOrder(userId, "Cash on Delivery")
                                        kotlinx.coroutines.delay(1500)

                                        Toast.makeText(
                                            context,
                                            "Order placed successfully!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        isProcessing = false
                                        onPaymentSuccess()
                                    }
                                }
                                null -> {
                                    Toast.makeText(
                                        context,
                                        "Please select a payment method",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedPaymentMethod != null && !isProcessing) {
                                Color(0xFFFF6B35)
                            } else {
                                Color.Gray
                            }
                        ),
                        shape = RoundedCornerShape(8.dp),
                        enabled = selectedPaymentMethod != null && !isProcessing
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = when (selectedPaymentMethod) {
                                    PaymentMethod.ESEWA -> "Pay with eSewa"
                                    PaymentMethod.CASH_ON_DELIVERY -> "Confirm Order"
                                    null -> "Select Payment Method"
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentMethodCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    iconRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE3F2FD) else Color.White
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) Color(0xFF0B8FAC) else Color.LightGray
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                modifier = Modifier.size(48.dp),
                tint = if (isSelected) Color(0xFF0B8FAC) else Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
fun ESewaPaymentInfo() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "You will be redirected to your eSewa account to complete your payment:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            InfoPoint(text = "1. Login to your eSewa account using your eSewa ID and your Password")
            InfoPoint(text = "2. Ensure your eSewa account is active and has sufficient balance")
            InfoPoint(text = "3. Enter OTP (one time password) sent to your registered mobile number")

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "***Login with your eSewa mobile and PASSWORD (not MPin)***",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF6B35)
            )
        }
    }
}

@Composable
fun CashOnDeliveryInfo(totalAmount: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            InfoPoint(text = "• You may pay in cash to our courier upon receiving your parcel at the doorstep.")
            Spacer(modifier = Modifier.height(8.dp))

            InfoPoint(text = "• Cash Payment Fee (2%), with a maximum cap of Rs. 20 applies only to Cash on Delivery payment method.")
            Spacer(modifier = Modifier.height(8.dp))

            val cashFee = (totalAmount * 0.02).coerceAtMost(20.0)
            Text(
                text = "Cash Payment Fee: Rs. ${cashFee.toInt()}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF6B35),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun InfoPoint(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        lineHeight = 20.sp,
        color = Color(0xFF374151)
    )
}

@Composable
fun OrderSummarySection(
    itemsTotal: Double,
    deliveryFee: Double,
    totalAmount: Double,
    itemCount: Int
) {
    Column {
        Text(
            text = "Order Summary",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Subtotal($itemCount items)",
                fontSize = 13.sp,
                color = Color.Gray
            )
            Text(
                text = "Rs. ${itemsTotal.toInt()}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Total Amount",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Rs. ${totalAmount.toInt()}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF6B35)
            )
        }
    }
}

enum class PaymentMethod {
    ESEWA,
    CASH_ON_DELIVERY
}

fun initiateESewaPayment(
    context: ComponentActivity,
    amount: Double,
    userId: String
) {
    val merchantCode = "EPAYTEST"
    val transactionId = "MEDIQ-${System.currentTimeMillis()}"

    try {
        val esewaUrl = "https://uat.esewa.com.np/epay/main"

        val params = mapOf(
            "amt" to amount.toString(),
            "psc" to "0",
            "pdc" to "0",
            "txAmt" to "0",
            "tAmt" to amount.toString(),
            "pid" to transactionId,
            "scd" to merchantCode,
            "su" to "https://yourwebsite.com/esewa/success",
            "fu" to "https://yourwebsite.com/esewa/failure"
        )

        val urlBuilder = StringBuilder(esewaUrl).append("?")
        params.forEach { (key, value) ->
            urlBuilder.append(URLEncoder.encode(key, StandardCharsets.UTF_8.toString()))
            urlBuilder.append("=")
            urlBuilder.append(URLEncoder.encode(value, StandardCharsets.UTF_8.toString()))
            urlBuilder.append("&")
        }

        val finalUrl = urlBuilder.toString().removeSuffix("&")
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl))
        context.startActivity(intent)

    } catch (e: Exception) {
        Toast.makeText(context, "Failed to initiate payment: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}