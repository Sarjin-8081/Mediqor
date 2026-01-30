package com.example.mediqorog.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.mediqorog.model.PrescriptionData
import com.example.mediqorog.viewmodel.PrescriptionViewModelSimple
import java.text.SimpleDateFormat
import java.util.*

class PrescriptionsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                PrescriptionsScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionsScreen(
    viewModel: PrescriptionViewModelSimple = viewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadPrescriptions()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Prescriptions", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { (context as ComponentActivity).finish() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0B8FAC),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF0B8FAC)
            ) {
                Icon(Icons.Default.Add, "Add", tint = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Color(0xFF0B8FAC)
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Active") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Upcoming") })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("History") })
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF0B8FAC))
                }
            } else {
                if (selectedTab == 0 && state.prescriptions.isNotEmpty()) {
                    TodaysScheduleCard(state.prescriptions.filter { it.isActive })
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (selectedTab) {
                        0 -> {
                            val activePrescriptions = state.prescriptions.filter { it.isActive }
                            if (activePrescriptions.isEmpty()) {
                                item {
                                    Text(
                                        "No active prescriptions",
                                        modifier = Modifier.padding(16.dp),
                                        color = Color.Gray
                                    )
                                }
                            } else {
                                items(activePrescriptions, key = { it.id }) { prescription ->
                                    PrescriptionCard(
                                        prescription = prescription,
                                        onDelete = { viewModel.deletePrescription(prescription.id) },
                                        onUpdateQuantity = { newQty ->
                                            viewModel.updateQuantity(prescription.id, newQty)
                                        }
                                    )
                                }
                            }
                        }
                        else -> item {
                            Text("No items", Modifier.padding(16.dp), color = Color.Gray)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddPrescriptionDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { prescription ->
                viewModel.addPrescription(prescription)
                showAddDialog = false
            }
        )
    }

    state.error?.let { error ->
        LaunchedEffect(error) {
            // Show error (you can use a Snackbar here)
            viewModel.clearError()
        }
    }
}

@Composable
fun TodaysScheduleCard(prescriptions: List<PrescriptionData>) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, null, tint = Color(0xFF0B8FAC), modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text("Today's Schedule", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))
            prescriptions.forEach { rx ->
                rx.times.forEach { time ->
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(rx.medicineName, fontWeight = FontWeight.Medium)
                            Text(
                                "${rx.dosage} • ${if (rx.beforeMeal) "Before" else "After"} meal",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                        Text(
                            time,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF0B8FAC),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PrescriptionCard(
    prescription: PrescriptionData,
    onDelete: () -> Unit,
    onUpdateQuantity: (Int) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showQuantityDialog by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val progress = if (prescription.totalQuantity > 0) {
        prescription.quantityRemaining.toFloat() / prescription.totalQuantity.toFloat()
    } else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(prescription.medicineName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(prescription.dosage, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, "Menu")
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Update Quantity") },
                            onClick = { showMenu = false; showQuantityDialog = true },
                            leadingIcon = { Icon(Icons.Default.Edit, "Edit") }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = { showMenu = false; onDelete() },
                            leadingIcon = { Icon(Icons.Default.Delete, "Delete", tint = Color.Red) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(
                    onClick = { },
                    label = { Text(prescription.frequency, fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Outlined.AccessTime, null, modifier = Modifier.size(16.dp)) }
                )
                AssistChip(
                    onClick = { },
                    label = { Text(if (prescription.beforeMeal) "Before meal" else "After meal", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Outlined.Restaurant, null, modifier = Modifier.size(16.dp)) }
                )
            }

            Spacer(Modifier.height(12.dp))

            Text("Reminder Times:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = Color.Gray)
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                prescription.times.forEach { time ->
                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFE3F2FD)) {
                        Text(
                            time,
                            Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF0B8FAC),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Prescribed by", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text(prescription.doctorName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Valid until", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text(
                        dateFormat.format(Date(prescription.endDate)),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Column {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Quantity Remaining", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text(
                        "${prescription.quantityRemaining}/${prescription.totalQuantity}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = if (progress < 0.3f) Color.Red else Color(0xFF0B8FAC)
                    )
                }
                Spacer(Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = if (progress < 0.3f) Color.Red else Color(0xFF0B8FAC),
                    trackColor = Color(0xFFE0E0E0)
                )
                if (progress < 0.3f) {
                    Spacer(Modifier.height(4.dp))
                    Text("⚠️ Running low! Consider refilling", style = MaterialTheme.typography.bodySmall, color = Color.Red)
                }
            }
        }
    }

    if (showQuantityDialog) {
        UpdateQuantityDialog(
            currentQuantity = prescription.quantityRemaining,
            onDismiss = { showQuantityDialog = false },
            onUpdate = { newQty ->
                onUpdateQuantity(newQty)
                showQuantityDialog = false
            }
        )
    }
}

@Composable
fun UpdateQuantityDialog(
    currentQuantity: Int,
    onDismiss: () -> Unit,
    onUpdate: (Int) -> Unit
) {
    var quantity by remember { mutableStateOf(currentQuantity.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Quantity") },
        text = {
            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Quantity Remaining") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = {
                quantity.toIntOrNull()?.let { onUpdate(it) }
            }) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPrescriptionDialog(
    onDismiss: () -> Unit,
    onAdd: (PrescriptionData) -> Unit
) {
    var medicineName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("Once daily") }
    var beforeMeal by remember { mutableStateOf(false) }
    var doctorName by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("7") }

    val frequencies = listOf("Once daily", "Twice daily", "3 times daily", "4 times daily")
    var expandedFrequency by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Prescription") },
        text = {
            Column(
                Modifier.fillMaxWidth().heightIn(max = 500.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = medicineName,
                    onValueChange = { medicineName = it },
                    label = { Text("Medicine Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it },
                    label = { Text("Dosage (e.g., 500mg)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                ExposedDropdownMenuBox(
                    expanded = expandedFrequency,
                    onExpandedChange = { expandedFrequency = it }
                ) {
                    OutlinedTextField(
                        value = frequency,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Frequency") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFrequency) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedFrequency,
                        onDismissRequest = { expandedFrequency = false }
                    ) {
                        frequencies.forEach { freq ->
                            DropdownMenuItem(
                                text = { Text(freq) },
                                onClick = { frequency = freq; expandedFrequency = false }
                            )
                        }
                    }
                }

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Before meal")
                    Switch(checked = beforeMeal, onCheckedChange = { beforeMeal = it })
                }

                OutlinedTextField(
                    value = doctorName,
                    onValueChange = { doctorName = it },
                    label = { Text("Doctor Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Total Quantity") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Duration (days)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (medicineName.isNotBlank() && dosage.isNotBlank()) {
                        val times = when (frequency) {
                            "Once daily" -> listOf("09:00 AM")
                            "Twice daily" -> listOf("09:00 AM", "09:00 PM")
                            "3 times daily" -> listOf("08:00 AM", "02:00 PM", "08:00 PM")
                            "4 times daily" -> listOf("08:00 AM", "12:00 PM", "04:00 PM", "08:00 PM")
                            else -> listOf("09:00 AM")
                        }

                        val now = System.currentTimeMillis()
                        val durationMillis = (duration.toLongOrNull() ?: 7) * 24 * 60 * 60 * 1000

                        val prescription = PrescriptionData(
                            medicineName = medicineName,
                            dosage = dosage,
                            frequency = frequency,
                            times = times,
                            startDate = now,
                            endDate = now + durationMillis,
                            doctorName = doctorName.ifBlank { "Not specified" },
                            beforeMeal = beforeMeal,
                            quantityRemaining = quantity.toIntOrNull() ?: 30,
                            totalQuantity = quantity.toIntOrNull() ?: 30,
                            isActive = true
                        )
                        onAdd(prescription)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}