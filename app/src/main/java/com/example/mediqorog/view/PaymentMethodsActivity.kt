// ========== PaymentMethodsActivity.kt ==========
package com.example.mediqorog.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PaymentMethodsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PaymentMethodsScreen(onBackClick = { finish() })
        }
    }
}

data class PaymentMethod(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val enabled: Boolean = true
)

class PaymentMethodsViewModel : ViewModel() {
    private val _selectedMethod = MutableStateFlow("cod")
    val selectedMethod: StateFlow<String> = _selectedMethod

    private val _saving = MutableStateFlow(false)
    val saving: StateFlow<Boolean> = _saving

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    val paymentMethods = listOf(
        PaymentMethod(
            id = "cod",
            name = "Cash on Delivery",
            description = "Pay when your order arrives",
            icon = Icons.Filled.LocalShipping,
            enabled = true
        ),
        PaymentMethod(
            id = "upi",
            name = "UPI Payment",
            description = "Coming soon - PhonePe, GPay, Paytm",
            icon = Icons.Filled.QrCode,
            enabled = false
        ),
        PaymentMethod(
            id = "card",
            name = "Credit/Debit Card",
            description = "Coming soon - All major cards accepted",
            icon = Icons.Filled.CreditCard,
            enabled = false
        ),
        PaymentMethod(
            id = "wallet",
            name = "Digital Wallet",
            description = "Coming soon - Wallet payments",
            icon = Icons.Filled.Wallet,
            enabled = false
        )
    )

    fun selectMethod(methodId: String) {
        val method = paymentMethods.find { it.id == methodId }
        if (method?.enabled == true) {
            _selectedMethod.value = methodId
        }
    }

    suspend fun savePaymentMethod(): Boolean {
        return try {
            _saving.value = true
            val userId = auth.currentUser?.uid ?: throw Exception("Not logged in")

            firestore.collection("users")
                .document(userId)
                .update("preferredPaymentMethod", _selectedMethod.value)
                .await()

            _saving.value = false
            true
        } catch (e: Exception) {
            _saving.value = false
            false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodsScreen(onBackClick: () -> Unit) {
    val viewModel: PaymentMethodsViewModel = viewModel()
    val selectedMethod by viewModel.selectedMethod.collectAsState()
    val saving by viewModel.saving.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment Methods", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0B8FAC),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Select Payment Method",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Choose your preferred payment method for orders",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Payment methods list
            viewModel.paymentMethods.forEach { method ->
                PaymentMethodCard(
                    method = method,
                    isSelected = selectedMethod == method.id,
                    onSelect = { viewModel.selectMethod(method.id) }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Save button
            Button(
                onClick = {
                    scope.launch {
                        val success = viewModel.savePaymentMethod()
                        if (success) {
                            snackbarHostState.showSnackbar("Payment method saved!")
                        } else {
                            snackbarHostState.showSnackbar("Failed to save")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0B8FAC)
                ),
                enabled = !saving
            ) {
                if (saving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text("Save Payment Method", fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun PaymentMethodCard(
    method: PaymentMethod,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) Color(0xFF0B8FAC) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = method.enabled, onClick = onSelect),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (method.enabled) Color.White else Color(0xFFF5F5F5)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (method.enabled) Color(0xFF0B8FAC).copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f)
                ) {
                    Icon(
                        imageVector = method.icon,
                        contentDescription = method.name,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(12.dp),
                        tint = if (method.enabled) Color(0xFF0B8FAC) else Color.Gray
                    )
                }

                Column {
                    Text(
                        method.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (method.enabled) Color.Black else Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        method.description,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }

            RadioButton(
                selected = isSelected,
                onClick = if (method.enabled) onSelect else null,
                enabled = method.enabled,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color(0xFF0B8FAC)
                )
            )
        }
    }
}