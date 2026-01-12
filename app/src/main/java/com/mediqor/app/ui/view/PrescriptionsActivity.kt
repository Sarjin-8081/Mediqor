package com.mediqor.app.ui.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import java.text.SimpleDateFormat
import java.util.*

// Data Model
data class Prescription(
    val id: String = UUID.randomUUID().toString(),
    val medicineName: String,
    val dosage: String,
    val frequency: String,
    val times: List<String>,
    val startDate: Date,
    val endDate: Date,
    val doctorName: String,
    val beforeMeal: Boolean,
    val quantityRemaining: Int,
    val totalQuantity: Int,
    val isActive: Boolean = true
)

class PrescriptionsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrescriptionsScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionsScreen() {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }

    // Sample data (replace with Firebase later)
    val prescriptions = remember {
        mutableStateListOf(
            Prescription(
                medicineName = "Paracetamol",
                dosage = "500mg",
                frequency = "3 times daily",
                times = listOf("08:00 AM", "02:00 PM", "08:00 PM"),
                startDate = Date(),
                endDate = Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000),
                doctorName = "Dr. Sharma",
                beforeMeal = false,
                quantityRemaining = 15,
                totalQuantity = 21,
                isActive = true
            ),
            Prescription(
                medicineName = "Vitamin D3",
                dosage = "2000 IU",
                frequency = "Once daily",
                times = listOf("09:00 AM"),
                startDate = Date(),
                endDate = Date(System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000),
                doctorName = "Dr. Patel",
                beforeMeal = true,
                quantityRemaining = 25,
                totalQuantity = 30,
                isActive = true
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Prescriptions") },
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
                Icon(Icons.Default.Add, "Add Prescription", tint = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {

            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Color(0xFF0B8FAC)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Active") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Upcoming") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("History") }
                )
            }

            // Today's Schedule Card
            if (selectedTab == 0) {
                TodaysScheduleCard(prescriptions)
            }

            // Prescriptions List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        items(prescriptions.filter { it.isActive }) { prescription ->
                            PrescriptionCard(
                                prescription = prescription,
                                onEdit = { /* TODO */ },
                                onDelete = { prescriptions.remove(prescription) }
                            )
                        }
                    }
                    1 -> {
                        item {
                            Text(
                                "No upcoming reminders",
                                modifier = Modifier.padding(16.dp),
                                color = Color.Gray
                            )
                        }
                    }
                    2 -> {
                        item {
                            Text(
                                "No prescription history",
                                modifier = Modifier.padding(16.dp),
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Prescription Dialog
    if (showAddDialog) {
        AddPrescriptionDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { prescription ->
                prescriptions.add(prescription)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun TodaysScheduleCard(prescriptions: List<Prescription>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = "Schedule",
                    tint = Color(0xFF0B8FAC),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Today's Schedule",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            prescriptions.forEach { prescription ->
                prescription.times.forEach { time ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = prescription.medicineName,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${prescription.dosage} • ${if (prescription.beforeMeal) "Before" else "After"} meal",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                        Text(
                            text = time,
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
    prescription: Prescription,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val progress = prescription.quantityRemaining.toFloat() / prescription.totalQuantity.toFloat()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = prescription.medicineName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = prescription.dosage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, "Menu")
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                showMenu = false
                                onEdit()
                            },
                            leadingIcon = { Icon(Icons.Default.Edit, "Edit") }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                showMenu = false
                                onDelete()
                            },
                            leadingIcon = { Icon(Icons.Default.Delete, "Delete", tint = Color.Red) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Info Chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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

            Spacer(modifier = Modifier.height(12.dp))

            // Times
            Text(
                "Reminder Times:",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                prescription.times.forEach { time ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE3F2FD)
                    ) {
                        Text(
                            text = time,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF0B8FAC),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Doctor & Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Prescribed by",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        prescription.doctorName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "Valid until",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        dateFormat.format(prescription.endDate),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Quantity Remaining",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        "${prescription.quantityRemaining}/${prescription.totalQuantity}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = if (progress < 0.3f) Color.Red else Color(0xFF0B8FAC)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = if (progress < 0.3f) Color.Red else Color(0xFF0B8FAC),
                    trackColor = Color(0xFFE0E0E0),
                )
                if (progress < 0.3f) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "⚠️ Running low! Consider refilling",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Red
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPrescriptionDialog(
    onDismiss: () -> Unit,
    onAdd: (Prescription) -> Unit
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
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .verticalScroll(rememberScrollState()),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedFrequency,
                        onDismissRequest = { expandedFrequency = false }
                    ) {
                        frequencies.forEach { freq ->
                            DropdownMenuItem(
                                text = { Text(freq) },
                                onClick = {
                                    frequency = freq
                                    expandedFrequency = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Before meal")
                    Switch(
                        checked = beforeMeal,
                        onCheckedChange = { beforeMeal = it }
                    )
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

                        val prescription = Prescription(
                            medicineName = medicineName,
                            dosage = dosage,
                            frequency = frequency,
                            times = times,
                            startDate = Date(),
                            endDate = Date(System.currentTimeMillis() + (duration.toLongOrNull() ?: 7) * 24 * 60 * 60 * 1000),
                            doctorName = doctorName.ifBlank { "Not specified" },
                            beforeMeal = beforeMeal,
                            quantityRemaining = quantity.toIntOrNull() ?: 30,
                            totalQuantity = quantity.toIntOrNull() ?: 30
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