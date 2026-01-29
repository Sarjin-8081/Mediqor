// ========== PrescriptionUploadActivity.kt ==========
package com.example.mediqorog.view

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class PrescriptionUploadActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PrescriptionUploadScreen(onBackClick = { finish() })
        }
    }
}

data class PrescriptionUpload(
    val id: String = "",
    val userId: String = "",
    val imageUrl: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "pending"
)

class PrescriptionUploadViewModel : ViewModel() {
    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState

    private val storage = FirebaseStorage.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    sealed class UploadState {
        object Idle : UploadState()
        object Uploading : UploadState()
        data class Success(val url: String) : UploadState()
        data class Error(val message: String) : UploadState()
    }

    suspend fun uploadPrescription(uri: Uri) {
        _uploadState.value = UploadState.Uploading

        try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
            val fileName = "prescription_${System.currentTimeMillis()}.jpg"
            val storageRef = storage.reference.child("prescriptions/$userId/$fileName")

            // Upload to Firebase Storage
            storageRef.putFile(uri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()

            // Save metadata to Firestore
            val prescription = PrescriptionUpload(
                id = UUID.randomUUID().toString(),
                userId = userId,
                imageUrl = downloadUrl,
                timestamp = System.currentTimeMillis(),
                status = "pending"
            )

            firestore.collection("prescriptions")
                .document(prescription.id)
                .set(prescription)
                .await()

            _uploadState.value = UploadState.Success(downloadUrl)
        } catch (e: Exception) {
            _uploadState.value = UploadState.Error(e.message ?: "Upload failed")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionUploadScreen(onBackClick: () -> Unit) {
    val viewModel: PrescriptionUploadViewModel = viewModel()
    val uploadState by viewModel.uploadState.collectAsState()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    LaunchedEffect(uploadState) {
        when (uploadState) {
            is PrescriptionUploadViewModel.UploadState.Success -> {
                snackbarHostState.showSnackbar("Prescription uploaded successfully!")
                selectedImageUri = null
            }
            is PrescriptionUploadViewModel.UploadState.Error -> {
                snackbarHostState.showSnackbar((uploadState as PrescriptionUploadViewModel.UploadState.Error).message)
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upload Prescription", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Filled.Info,
                        contentDescription = null,
                        tint = Color(0xFF0B8FAC)
                    )
                    Column {
                        Text(
                            "Upload Guidelines",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "• Ensure prescription is clearly visible\n" +
                                    "• Photo should be well-lit\n" +
                                    "• All medicine names must be readable\n" +
                                    "• Valid doctor signature required",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Image Preview or Upload Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(2.dp, Color(0xFF0B8FAC), RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5))
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(selectedImageUri),
                        contentDescription = "Selected Prescription",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Filled.CloudUpload,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color(0xFF0B8FAC)
                        )
                        Text(
                            "Tap to select prescription image",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Upload Button
            if (selectedImageUri != null) {
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.uploadPrescription(selectedImageUri!!)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0B8FAC)
                    ),
                    enabled = uploadState !is PrescriptionUploadViewModel.UploadState.Uploading
                ) {
                    if (uploadState is PrescriptionUploadViewModel.UploadState.Uploading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Uploading...")
                    } else {
                        Icon(Icons.Filled.Upload, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Upload Prescription", fontSize = 16.sp)
                    }
                }

                OutlinedButton(
                    onClick = { selectedImageUri = null },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}