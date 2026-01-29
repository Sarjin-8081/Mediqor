package com.example.mediqorog.view

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*

class MedicineReminderActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MedicineReminderScreen(onNavigateBack = { finish() })
            }
        }
    }
}

data class MedicineReminder(
    val id: String,
    val medicineName: String,
    val dosage: String,
    val time: String,
    val frequency: String,
    val isActive: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineReminderScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var reminders by remember {
        mutableStateOf(
            listOf(
                MedicineReminder(
                    "1",
                    "Paracetamol 500mg",
                    "1 tablet",
                    "09:00 AM",
                    "Daily",
                    true
                ),
                MedicineReminder(
                    "2",
                    "Vitamin D3",
                    "1 capsule",
                    "08:00 AM",
                    "Daily",
                    true
                ),
                MedicineReminder(
                    "3",
                    "Blood Pressure Medication",
                    "1 tablet",
                    "07:30 PM",
                    "Daily",
                    false
                )
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medicine Reminders", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, "Add Reminder", tint = Color.White)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F7FA)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFDBEAFE))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Info",
                            tint = Color(0xFF1E40AF)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Never miss your medication with timely reminders",
                            fontSize = 13.sp,
                            color = Color(0xFF1E3A8A)
                        )
                    }
                }
            }

            if (reminders.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.AlarmAdd,
                                contentDescription = "No reminders",
                                modifier = Modifier.size(80.dp),
                                tint = Color(0xFFD1D5DB)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No Reminders Yet",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6B7280)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Add your first reminder to get started",
                                fontSize = 14.sp,
                                color = Color(0xFF9CA3AF)
                            )
                        }
                    }
                }
            } else {
                items(reminders) { reminder ->
                    ReminderCard(
                        reminder = reminder,
                        onToggle = {
                            reminders = reminders.map {
                                if (it.id == reminder.id) it.copy(isActive = !it.isActive) else it
                            }
                            Toast.makeText(
                                context,
                                if (!reminder.isActive) "Reminder enabled" else "Reminder disabled",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onDelete = {
                            reminders = reminders.filter { it.id != reminder.id }
                            Toast.makeText(context, "Reminder deleted", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddReminderDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, dosage, time, frequency ->
                val newReminder = MedicineReminder(
                    id = System.currentTimeMillis().toString(),
                    medicineName = name,
                    dosage = dosage,
                    time = time,
                    frequency = frequency,
                    isActive = true
                )
                reminders = reminders + newReminder
                showAddDialog = false
                scheduleReminder(context, newReminder)
                Toast.makeText(context, "Reminder added successfully", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun ReminderCard(
    reminder: MedicineReminder,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        if (reminder.isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        else Color(0xFFF3F4F6),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MedicalServices,
                    contentDescription = "Medicine",
                    tint = if (reminder.isActive) MaterialTheme.colorScheme.primary else Color(0xFF9CA3AF),
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reminder.medicineName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = reminder.dosage,
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Time",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${reminder.time} • ${reminder.frequency}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Switch(
                    checked = reminder.isActive,
                    onCheckedChange = { onToggle() }
                )
                Spacer(modifier = Modifier.height(4.dp))
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Reminder") },
            text = { Text("Are you sure you want to delete this reminder?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String) -> Unit
) {
    val context = LocalContext.current
    var medicineName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("09:00 AM") }
    var selectedFrequency by remember { mutableStateOf("Daily") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Medicine Reminder") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = medicineName,
                    onValueChange = { medicineName = it },
                    label = { Text("Medicine Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it },
                    label = { Text("Dosage (e.g., 1 tablet)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = selectedTime,
                    onValueChange = {},
                    label = { Text("Time") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val calendar = Calendar.getInstance()
                            TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    val amPm = if (hour < 12) "AM" else "PM"
                                    val displayHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
                                    selectedTime = String.format("%02d:%02d %s", displayHour, minute, amPm)
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                false
                            ).show()
                        },
                    readOnly = true,
                    trailingIcon = {
                        Icon(Icons.Default.Schedule, "Pick time")
                    }
                )

                var expandedFrequency by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedFrequency,
                    onExpandedChange = { expandedFrequency = it }
                ) {
                    OutlinedTextField(
                        value = selectedFrequency,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Frequency") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedFrequency) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedFrequency,
                        onDismissRequest = { expandedFrequency = false }
                    ) {
                        listOf("Daily", "Twice a day", "Three times a day", "Weekly").forEach { freq ->
                            DropdownMenuItem(
                                text = { Text(freq) },
                                onClick = {
                                    selectedFrequency = freq
                                    expandedFrequency = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (medicineName.isNotBlank() && dosage.isNotBlank()) {
                        onAdd(medicineName, dosage, selectedTime, selectedFrequency)
                    }
                },
                enabled = medicineName.isNotBlank() && dosage.isNotBlank()
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

fun scheduleReminder(context: Context, reminder: MedicineReminder) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, MedicineReminderReceiver::class.java).apply {
        putExtra("medicine_name", reminder.medicineName)
        putExtra("dosage", reminder.dosage)
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        reminder.id.toInt(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 9)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }

    alarmManager.setRepeating(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        AlarmManager.INTERVAL_DAY,
        pendingIntent
    )
}