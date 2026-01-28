// ========== MedicineReminderActivity.kt ==========
package com.example.mediqorog.view

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class MedicineReminderActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            // Permission denied - show message
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            MedicineReminderScreen(onBackClick = { finish() })
        }
    }
}

data class Reminder(
    val id: String = "",
    val medicineName: String = "",
    val dosage: String = "",
    val time: String = "",
    val frequency: String = "daily",
    val enabled: Boolean = true
)

class MedicineReminderViewModel : ViewModel() {
    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    init {
        loadReminders()
    }

    private fun loadReminders() {
        _loading.value = true
        val userId = auth.currentUser?.uid

        if (userId != null) {
            firestore.collection("users")
                .document(userId)
                .collection("reminders")
                .get()
                .addOnSuccessListener { documents ->
                    _reminders.value = documents.mapNotNull { it.toObject(Reminder::class.java) }
                    _loading.value = false
                }
                .addOnFailureListener {
                    _loading.value = false
                }
        } else {
            _loading.value = false
        }
    }

    suspend fun saveReminder(reminder: Reminder, context: Context): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("Not logged in")
            val reminderId = if (reminder.id.isEmpty()) UUID.randomUUID().toString() else reminder.id

            firestore.collection("users")
                .document(userId)
                .collection("reminders")
                .document(reminderId)
                .set(reminder.copy(id = reminderId))
                .await()

            // Schedule notification
            scheduleReminder(context, reminder.copy(id = reminderId))

            loadReminders()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteReminder(reminderId: String, context: Context): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("Not logged in")

            firestore.collection("users")
                .document(userId)
                .collection("reminders")
                .document(reminderId)
                .delete()
                .await()

            // Cancel notification
            cancelReminder(context, reminderId)

            loadReminders()
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun scheduleReminder(context: Context, reminder: Reminder) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("medicineName", reminder.medicineName)
            putExtra("dosage", reminder.dosage)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Parse time and schedule
        val timeParts = reminder.time.split(":")
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
            set(Calendar.MINUTE, timeParts[1].toInt())
            set(Calendar.SECOND, 0)
        }

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun cancelReminder(context: Context, reminderId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineReminderScreen(onBackClick: () -> Unit) {
    val viewModel: MedicineReminderViewModel = viewModel()
    val reminders by viewModel.reminders.collectAsState()
    val loading by viewModel.loading.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medicine Reminder", fontWeight = FontWeight.Bold) },
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF0B8FAC)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Reminder", tint = Color.White)
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (reminders.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Filled.Alarm,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No reminders set", fontSize = 18.sp, color = Color.Gray)
                    Text("Add a reminder to never miss your medicine", fontSize = 14.sp, color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(reminders) { reminder ->
                        ReminderCard(
                            reminder = reminder,
                            onDelete = {
                                scope.launch {
                                    val success = viewModel.deleteReminder(reminder.id, context)
                                    if (success) {
                                        snackbarHostState.showSnackbar("Reminder deleted")
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddReminderDialog(
            onDismiss = { showAddDialog = false },
            onSave = { reminder ->
                scope.launch {
                    val success = viewModel.saveReminder(reminder, context)
                    if (success) {
                        snackbarHostState.showSnackbar("Reminder set!")
                        showAddDialog = false
                    }
                }
            }
        )
    }
}

@Composable
fun ReminderCard(reminder: Reminder, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                    color = Color(0xFFFF5722).copy(alpha = 0.1f)
                ) {
                    Icon(
                        Icons.Filled.Alarm,
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(12.dp),
                        tint = Color(0xFFFF5722)
                    )
                }

                Column {
                    Text(
                        reminder.medicineName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Dosage: ${reminder.dosage}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            Icons.Filled.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                        Text(
                            reminder.time,
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                        Text(
                            "• ${reminder.frequency}",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color(0xFFE53935))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderDialog(onDismiss: () -> Unit, onSave: (Reminder) -> Unit) {
    var medicineName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var selectedHour by remember { mutableStateOf(9) }
    var selectedMinute by remember { mutableStateOf(0) }
    var frequency by remember { mutableStateOf("daily") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Add Reminder", fontSize = 20.sp, fontWeight = FontWeight.Bold)

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

                Text("Reminder Time", fontWeight = FontWeight.Medium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = selectedHour.toString(),
                        onValueChange = { selectedHour = it.toIntOrNull() ?: 0 },
                        label = { Text("Hour") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = selectedMinute.toString(),
                        onValueChange = { selectedMinute = it.toIntOrNull() ?: 0 },
                        label = { Text("Minute") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Text("Frequency", fontWeight = FontWeight.Medium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = frequency == "daily",
                        onClick = { frequency = "daily" },
                        label = { Text("Daily") }
                    )
                    FilterChip(
                        selected = frequency == "weekly",
                        onClick = { frequency = "weekly" },
                        label = { Text("Weekly") }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val time = String.format("%02d:%02d", selectedHour, selectedMinute)
                            val reminder = Reminder(
                                medicineName = medicineName,
                                dosage = dosage,
                                time = time,
                                frequency = frequency
                            )
                            onSave(reminder)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B8FAC))
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

