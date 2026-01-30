package com.example.mediqorog.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mediqorog.model.MedicineReminder
import com.example.mediqorog.model.Prescription
import com.example.mediqorog.model.PrescriptionStatus
import com.example.mediqorog.viewmodel.PrescriptionViewModel
import java.text.SimpleDateFormat
import java.util.*

class PrescriptionsActivity : ComponentActivity() {
    private val viewModel: PrescriptionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                PrescriptionsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionsScreen(
    viewModel: PrescriptionViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showReminderDialog by remember { mutableStateOf(false) }
    var selectedPrescription: Int by remember { mutableStateOf<Prescription?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadPrescriptions()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Prescriptions",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8FAFC))
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 4.dp
            ) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = Color(0xFF0B8FAC),
                    indicator = { tabPositions ->
                        if (tabPositions.isNotEmpty() && selectedTab < tabPositions.size) {
                            Box(
                                Modifier
                                    .tabIndicatorOffset(tabPositions[selectedTab])
                                    .height(3.dp)
                                    .background(Color(0xFF0B8FAC))
                            )
                        }
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "Active",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "Pending",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text(
                                "History",
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF0B8FAC)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        when (selectedTab) {
                            0 -> {
                                val active = uiState.prescriptions.filter {
                                    it.status == PrescriptionStatus.VERIFIED && it.reminders.isNotEmpty()
                                }
                                if (active.isEmpty()) {
                                    item { EmptyStateCard("No active prescriptions with reminders") }
                                } else {
                                    items(active) { prescription ->
                                        ProfessionalPrescriptionCard(
                                            prescription = prescription,
                                            onSetReminder = {
                                                selectedPrescription = prescription
                                                showReminderDialog = true
                                            },
                                            onToggleReminder = { reminderId ->
                                                viewModel.toggleReminder(prescription.id, reminderId)
                                            }
                                        )
                                    }
                                }
                            }
                            1 -> {
                                val pending = uiState.prescriptions.filter {
                                    it.status == PrescriptionStatus.PENDING
                                }
                                if (pending.isEmpty()) {
                                    item { EmptyStateCard("No pending prescriptions") }
                                } else {
                                    items(pending) { prescription ->
                                        PendingPrescriptionCard(prescription = prescription)
                                    }
                                }
                            }
                            2 -> {
                                val history = uiState.prescriptions.filter {
                                    it.status == PrescriptionStatus.REJECTED ||
                                            (it.reminders.isNotEmpty() && it.reminders.all { r -> r.endDate.before(Date()) })
                                }
                                if (history.isEmpty()) {
                                    item { EmptyStateCard("No prescription history") }
                                } else {
                                    items(history) { prescription ->
                                        HistoryPrescriptionCard(prescription)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showReminderDialog && selectedPrescription != null) {
        AddReminderDialog(
            prescription = selectedPrescription!!,
            onDismiss = {
                showReminderDialog = false
                selectedPrescription = null
            },
            onSave = { reminder ->
                viewModel.addReminderToPrescription(selectedPrescription!!.id, reminder)
                showReminderDialog = false
                selectedPrescription = null
            }
        )
    }
}

@Composable
fun ProfessionalPrescriptionCard(
    prescription: Int,
    onSetReminder: () -> Unit,
    onToggleReminder: (String) -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AsyncImage(
                    model = prescription.imageUrl,
                    contentDescription = "Prescription",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF1F5F9))
                )

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = prescription.patientName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFFD1FAE5)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF10B981))
                                )
                                Text(
                                    "Verified",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF059669)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Uploaded ${dateFormat.format(prescription.uploadDate)}",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )

                    if (prescription.verifiedBy != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                "Verified by ${prescription.verifiedBy}",
                                fontSize = 12.sp,
                                color = Color(0xFF059669),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            if (prescription.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Pharmacist Notes:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF475569)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    prescription.notes,
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 20.sp
                )
            }

            if (prescription.reminders.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Color(0xFF0B8FAC),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Medicine Reminders (${prescription.reminders.size})",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    }

                    IconButton(
                        onClick = onSetReminder,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Reminder",
                            tint = Color(0xFF0B8FAC),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                prescription.reminders.forEach { reminder ->
                    ReminderItemCard(
                        reminder = reminder,
                        onToggle = { onToggleReminder(reminder.medicineName) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onSetReminder,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0B8FAC)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AddAlert, "Add", modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Set Up Medicine Reminder", fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
fun ReminderItemCard(
    reminder: MedicineReminder,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (reminder.enabled) Color(0xFFF0F9FF) else Color(0xFFF8FAFC),
        border = BorderStroke(
            1.dp,
            if (reminder.enabled) Color(0xFF0B8FAC).copy(alpha = 0.3f) else Color(0xFFE2E8F0)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (reminder.enabled) Color(0xFF0B8FAC).copy(alpha = 0.1f) else Color(0xFFE2E8F0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.MedicalServices,
                        contentDescription = null,
                        tint = if (reminder.enabled) Color(0xFF0B8FAC) else Color(0xFF94A3B8),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reminder.medicineName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = reminder.dosage,
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (reminder.enabled) Color(0xFF0B8FAC).copy(alpha = 0.1f) else Color(0xFFE2E8F0)
                        ) {
                            Text(
                                text = reminder.frequency,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = if (reminder.enabled) Color(0xFF0B8FAC) else Color(0xFF64748B),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        if (reminder.times.isNotEmpty()) {
                            reminder.times.take(2).forEach { time ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (reminder.enabled) Color(0xFF10B981).copy(alpha = 0.1f) else Color(0xFFE2E8F0)
                                ) {
                                    Text(
                                        text = formatTime(time),
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        color = if (reminder.enabled) Color(0xFF059669) else Color(0xFF64748B),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            if (reminder.times.size > 2) {
                                Text(
                                    "+${reminder.times.size - 2}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }
            }

            Switch(
                checked = reminder.enabled,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF10B981),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFCBD5E1)
                )
            )
        }
    }
}

@Composable
fun PendingPrescriptionCard(prescription: Prescription) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AsyncImage(
                    model = prescription.imageUrl,
                    contentDescription = "Prescription",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF1F5F9))
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = prescription.patientName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Uploaded ${dateFormat.format(prescription.uploadDate)}",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFFEF3C7)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                "Pending Verification",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFD97706)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFFF7ED)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        "Your prescription is being reviewed by our pharmacist. You can set up reminders once it's verified.",
                        fontSize = 13.sp,
                        color = Color(0xFF92400E),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryPrescriptionCard(prescription: Prescription) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = prescription.imageUrl,
                contentDescription = "Prescription",
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFE2E8F0))
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = prescription.patientName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF475569)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dateFormat.format(prescription.uploadDate),
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = when (prescription.status) {
                    PrescriptionStatus.REJECTED -> Color(0xFFFEE2E2)
                    else -> Color(0xFFE2E8F0)
                }
            ) {
                Text(
                    text = when (prescription.status) {
                        PrescriptionStatus.REJECTED -> "Rejected"
                        else -> "Expired"
                    },
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    color = when (prescription.status) {
                        PrescriptionStatus.REJECTED -> Color(0xFFDC2626)
                        else -> Color(0xFF64748B)
                    },
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderDialog(
    prescription: Prescription,
    onDismiss: () -> Unit,
    onSave: (MedicineReminder) -> Unit
) {
    var medicineName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("Once daily") }
    var duration by remember { mutableStateOf("7") }
    var selectedTimes by remember { mutableStateOf(listOf("09:00")) }
    var expandedFrequency by remember { mutableStateOf(false) }

    val frequencies = listOf("Once daily", "Twice daily", "3 times daily", "4 times daily")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Set Medicine Reminder",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = medicineName,
                    onValueChange = { medicineName = it },
                    label = { Text("Medicine Name") },
                    placeholder = { Text("e.g., Paracetamol") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(Icons.Default.MedicalServices, null, tint = Color(0xFF0B8FAC))
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0B8FAC),
                        focusedLabelColor = Color(0xFF0B8FAC)
                    )
                )

                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it },
                    label = { Text("Dosage") },
                    placeholder = { Text("e.g., 500mg or 1 tablet") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(Icons.Default.LocalPharmacy, null, tint = Color(0xFF0B8FAC))
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0B8FAC),
                        focusedLabelColor = Color(0xFF0B8FAC)
                    )
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
                        leadingIcon = {
                            Icon(Icons.Default.Schedule, null, tint = Color(0xFF0B8FAC))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0B8FAC),
                            focusedLabelColor = Color(0xFF0B8FAC)
                        )
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
                                    selectedTimes = when (freq) {
                                        "Once daily" -> listOf("09:00")
                                        "Twice daily" -> listOf("09:00", "21:00")
                                        "3 times daily" -> listOf("08:00", "14:00", "20:00")
                                        "4 times daily" -> listOf("08:00", "12:00", "16:00", "20:00")
                                        else -> listOf("09:00")
                                    }
                                    expandedFrequency = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Duration (days)") },
                    placeholder = { Text("e.g., 7") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(Icons.Default.CalendarToday, null, tint = Color(0xFF0B8FAC))
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0B8FAC),
                        focusedLabelColor = Color(0xFF0B8FAC)
                    )
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF0F9FF)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = Color(0xFF0B8FAC),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                "Reminder Times:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0B8FAC)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            selectedTimes.forEach { time ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF0B8FAC)
                                ) {
                                    Text(
                                        text = formatTime(time),
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (medicineName.isNotBlank() && dosage.isNotBlank()) {
                        val reminder = MedicineReminder(
                            medicineName = medicineName,
                            dosage = dosage,
                            frequency = frequency,
                            times = selectedTimes,
                            startDate = Date(),
                            endDate = Date(System.currentTimeMillis() + (duration.toLongOrNull() ?: 7) * 24 * 60 * 60 * 1000),
                            enabled = true
                        )
                        onSave(reminder)
                    }
                },
                enabled = medicineName.isNotBlank() && dosage.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0B8FAC)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Reminder", fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF64748B))
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun EmptyStateCard(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color(0xFFCBD5E1)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                fontSize = 16.sp,
                color = Color(0xFF94A3B8),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

fun formatTime(time: String): String {
    return try {
        val parts = time.split(":")
        val hour = parts[0].toInt()
        val minute = parts[1]
        val amPm = if (hour < 12) "AM" else "PM"
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        String.format("%02d:%s %s", displayHour, minute, amPm)
    } catch (e: Exception) {
        time
    }
}