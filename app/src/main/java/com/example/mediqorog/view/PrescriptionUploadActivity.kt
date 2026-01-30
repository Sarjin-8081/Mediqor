package com.example.mediqorog.view

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.example.mediqorog.model.Prescription
import com.example.mediqorog.model.PrescriptionStatus
import com.example.mediqorog.utils.NotificationHelper
import com.example.mediqorog.viewmodel.PrescriptionViewModel
import java.text.SimpleDateFormat
import java.util.*

class PrescriptionUploadActivity : ComponentActivity() {
    private val viewModel: PrescriptionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create notification channel
        NotificationHelper.createNotificationChannel(this)

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
            }
        }

        setContent {
            MaterialTheme {
                PrescriptionUploadScreen(
                    viewModel = viewModel,
                    onNavigateBack = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionUploadScreen(
    viewModel: PrescriptionViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var patientName by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var showReminderDialog by remember { mutableStateOf(false) }
    var selectedPrescription by remember { mutableStateOf<Prescription?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    // Handle errors
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    // Handle upload success
    LaunchedEffect(uiState.uploadSuccess) {
        if (uiState.uploadSuccess) {
            Toast.makeText(context, "Prescription uploaded successfully!", Toast.LENGTH_SHORT).show()
            selectedImageUri = null
            patientName = ""
            notes = ""
            viewModel.clearUploadSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upload Prescription", fontWeight = FontWeight.SemiBold) },
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
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF5F7FA)),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Info Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info",
                                tint = Color(0xFF3B82F6)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Upload a clear photo of your prescription. Our pharmacist will verify it.",
                                fontSize = 13.sp,
                                color = Color(0xFF1E40AF)
                            )
                        }
                    }
                }

                // Image Selection
                item {
                    if (selectedImageUri != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Selected Prescription",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1F2937)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Image(
                                    painter = rememberAsyncImagePainter(selectedImageUri),
                                    contentDescription = "Prescription",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(onClick = { selectedImageUri = null }) {
                                        Icon(Icons.Default.Delete, "Remove", modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Remove")
                                    }
                                }
                            }
                        }
                    } else {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { imagePickerLauncher.launch("image/*") },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .background(
                                            Color(0xFF0B8FAC).copy(alpha = 0.1f),
                                            RoundedCornerShape(40.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudUpload,
                                        contentDescription = "Upload",
                                        tint = Color(0xFF0B8FAC),
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Tap to select prescription image",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1F2937)
                                )
                                Text(
                                    text = "From your gallery",
                                    fontSize = 13.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }
                        }
                    }
                }

                // Patient Details
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Patient Details",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937)
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = patientName,
                                onValueChange = { patientName = it },
                                label = { Text("Patient Name *") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = notes,
                                onValueChange = { notes = it },
                                label = { Text("Additional Notes (Optional)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                minLines = 3,
                                maxLines = 5
                            )
                        }
                    }
                }

                // Upload Button
                item {
                    Button(
                        onClick = {
                            if (selectedImageUri != null && patientName.isNotBlank()) {
                                viewModel.uploadPrescription(patientName, selectedImageUri!!, notes)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please select image and enter patient name",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = selectedImageUri != null && patientName.isNotBlank() && !uiState.isUploading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0B8FAC)
                        )
                    ) {
                        if (uiState.isUploading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Icon(Icons.Default.CloudUpload, "Upload", modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Upload Prescription", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Uploaded Prescriptions List
                if (uiState.prescriptions.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Your Prescriptions (${uiState.prescriptions.size})",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                    }

                    items(uiState.prescriptions) { prescription ->
                        PrescriptionCard(
                            prescription = prescription,
                            onSetReminder = {
                                selectedPrescription = prescription
                                showReminderDialog = true
                            },
                            onDelete = {
                                viewModel.deletePrescription(prescription.id)
                            }
                        )
                    }
                }
            }

            // Loading overlay
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }

    // Reminder Dialog
    if (showReminderDialog && selectedPrescription != null) {
        MedicineReminderDialog(
            prescription = selectedPrescription!!,
            onDismiss = {
                showReminderDialog = false
                selectedPrescription = null
            },
            onSetReminder = { medicineName, dosage, time ->
                Toast.makeText(
                    context,
                    "Reminder set for $medicineName at $time",
                    Toast.LENGTH_SHORT
                ).show()
                showReminderDialog = false
                selectedPrescription = null
            }
        )
    }
}

@Composable
fun PrescriptionCard(
    prescription: Prescription,
    onSetReminder: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Prescription Image
                Image(
                    painter = rememberAsyncImagePainter(prescription.imageUrl),
                    contentDescription = "Prescription",
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = prescription.patientName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1F2937)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Uploaded: ${dateFormat.format(prescription.uploadDate)}",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Status Badge
                    Box(
                        modifier = Modifier
                            .background(
                                when (prescription.status) {
                                    PrescriptionStatus.PENDING -> Color(0xFFFEF3C7)
                                    PrescriptionStatus.VERIFIED -> Color(0xFFD1FAE5)
                                    PrescriptionStatus.REJECTED -> Color(0xFFFEE2E2)
                                },
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = prescription.status.toDisplayString(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = when (prescription.status) {
                                PrescriptionStatus.PENDING -> Color(0xFFD97706)
                                PrescriptionStatus.VERIFIED -> Color(0xFF059669)
                                PrescriptionStatus.REJECTED -> Color(0xFFDC2626)
                            }
                        )
                    }
                }

                // Menu
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, "More")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Set Reminder") },
                            onClick = {
                                showMenu = false
                                onSetReminder()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Alarm, "Reminder")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete", color = Color(0xFFDC2626)) },
                            onClick = {
                                showMenu = false
                                onDelete()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, "Delete", tint = Color(0xFFDC2626))
                            }
                        )
                    }
                }
            }

            // Notes if available
            if (prescription.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Notes: ${prescription.notes}",
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineReminderDialog(
    prescription: Prescription,
    onDismiss: () -> Unit,
    onSetReminder: (String, String, String) -> Unit
) {
    var medicineName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var selectedHour by remember { mutableStateOf(8) }
    var selectedMinute by remember { mutableStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Medicine Reminder") },
        text = {
            Column {
                OutlinedTextField(
                    value = medicineName,
                    onValueChange = { medicineName = it },
                    label = { Text("Medicine Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it },
                    label = { Text("Dosage (e.g., 1 tablet)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Reminder Time:", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hour selector
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { selectedHour = (selectedHour + 1) % 24 }) {
                            Icon(Icons.Default.KeyboardArrowUp, "Increase")
                        }
                        Text(
                            text = String.format("%02d", selectedHour),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { selectedHour = if (selectedHour == 0) 23 else selectedHour - 1 }) {
                            Icon(Icons.Default.KeyboardArrowDown, "Decrease")
                        }
                    }
                    Text(":", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    // Minute selector
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { selectedMinute = (selectedMinute + 15) % 60 }) {
                            Icon(Icons.Default.KeyboardArrowUp, "Increase")
                        }
                        Text(
                            text = String.format("%02d", selectedMinute),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { selectedMinute = if (selectedMinute < 15) 45 else selectedMinute - 15 }) {
                            Icon(Icons.Default.KeyboardArrowDown, "Decrease")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (medicineName.isNotBlank() && dosage.isNotBlank()) {
                        val time = String.format("%02d:%02d", selectedHour, selectedMinute)
                        onSetReminder(medicineName, dosage, time)
                    }
                },
                enabled = medicineName.isNotBlank() && dosage.isNotBlank()
            ) {
                Text("Set Reminder")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}