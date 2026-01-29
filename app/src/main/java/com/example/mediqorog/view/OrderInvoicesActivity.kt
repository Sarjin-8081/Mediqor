// ========== OrderInvoicesActivity.kt ==========
package com.example.mediqorog.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*

class OrderInvoicesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OrderInvoicesScreen(onBackClick = { finish() })
        }
    }
}

data class Invoice(
    val id: String = "",
    val invoiceNumber: String = "",
    val orderNumber: String = "",
    val date: Long = 0,
    val totalAmount: Double = 0.0,
    val items: List<InvoiceItem> = emptyList(),
    val tax: Double = 0.0,
    val discount: Double = 0.0
)

data class InvoiceItem(
    val name: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0
)

class OrderInvoicesViewModel : ViewModel() {
    private val _invoices = MutableStateFlow<List<Invoice>>(emptyList())
    val invoices: StateFlow<List<Invoice>> = _invoices

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    init {
        loadInvoices()
    }

    private fun loadInvoices() {
        _loading.value = true
        val userId = auth.currentUser?.uid

        if (userId != null) {
            firestore.collection("invoices")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    _invoices.value = documents.mapNotNull { it.toObject(Invoice::class.java) }
                        .sortedByDescending { it.date }
                    _loading.value = false
                }
                .addOnFailureListener {
                    _invoices.value = getMockInvoices()
                    _loading.value = false
                }
        } else {
            _invoices.value = getMockInvoices()
            _loading.value = false
        }
    }

    private fun getMockInvoices(): List<Invoice> {
        return listOf(
            Invoice(
                id = "INV001",
                invoiceNumber = "MDQ/2024/001",
                orderNumber = "ORD2024001",
                date = System.currentTimeMillis() - 5*24*60*60*1000,
                items = listOf(
                    InvoiceItem("Paracetamol 500mg", 2, 45.0),
                    InvoiceItem("Vitamin C Tablets", 1, 120.0)
                ),
                totalAmount = 210.0,
                tax = 18.0,
                discount = 20.0
            ),
            Invoice(
                id = "INV002",
                invoiceNumber = "MDQ/2024/002",
                orderNumber = "ORD2024002",
                date = System.currentTimeMillis() - 15*24*60*60*1000,
                items = listOf(
                    InvoiceItem("Blood Pressure Monitor", 1, 1500.0)
                ),
                totalAmount = 1500.0,
                tax = 270.0,
                discount = 100.0
            ),
            Invoice(
                id = "INV003",
                invoiceNumber = "MDQ/2024/003",
                orderNumber = "ORD2024003",
                date = System.currentTimeMillis() - 30*24*60*60*1000,
                items = listOf(
                    InvoiceItem("Cough Syrup", 1, 85.0),
                    InvoiceItem("Antiseptic Cream", 1, 55.0),
                    InvoiceItem("Band-Aid Box", 1, 25.0)
                ),
                totalAmount = 165.0,
                tax = 15.0,
                discount = 10.0
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderInvoicesScreen(onBackClick: () -> Unit) {
    val viewModel: OrderInvoicesViewModel = viewModel()
    val invoices by viewModel.invoices.collectAsState()
    val loading by viewModel.loading.collectAsState()
    var selectedInvoice by remember { mutableStateOf<Invoice?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Invoices", fontWeight = FontWeight.Bold) },
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
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (invoices.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Filled.Receipt,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No invoices found", fontSize = 18.sp, color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(invoices) { invoice ->
                        InvoiceCard(
                            invoice = invoice,
                            onClick = { selectedInvoice = invoice }
                        )
                    }
                }
            }
        }
    }

    // Invoice Detail Dialog
    selectedInvoice?.let { invoice ->
        InvoiceDetailDialog(
            invoice = invoice,
            onDismiss = { selectedInvoice = null }
        )
    }
}

@Composable
fun InvoiceCard(invoice: Invoice, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0B8FAC).copy(alpha = 0.1f)
                ) {
                    Icon(
                        Icons.Filled.Receipt,
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(12.dp),
                        tint = Color(0xFF0B8FAC)
                    )
                }

                Column {
                    Text(
                        invoice.invoiceNumber,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(invoice.date)),
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Text(
                        "₹${invoice.totalAmount}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0B8FAC)
                    )
                }
            }

            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = "View",
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun InvoiceDetailDialog(invoice: Invoice, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Invoice Details",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                DetailRow("Invoice No:", invoice.invoiceNumber)
                DetailRow("Order No:", invoice.orderNumber)
                DetailRow("Date:", SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(invoice.date)))

                Spacer(modifier = Modifier.height(16.dp))
                Text("Items:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))

                invoice.items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${item.name} x${item.quantity}", fontSize = 13.sp)
                        Text("₹${item.price * item.quantity}", fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                val subtotal = invoice.items.sumOf { it.price * it.quantity }
                DetailRow("Subtotal:", "₹$subtotal")
                DetailRow("Tax:", "₹${invoice.tax}")
                DetailRow("Discount:", "-₹${invoice.discount}", Color(0xFF4CAF50))

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total:", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("₹${invoice.totalAmount}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B8FAC))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { /* Share/Download logic */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B8FAC))
                ) {
                    Icon(Icons.Filled.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share Invoice")
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String, valueColor: Color = Color.Black) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = valueColor)
    }
}