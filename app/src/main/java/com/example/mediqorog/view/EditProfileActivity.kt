package com.example.mediqorog.view

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class EditProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            EditProfileScreen(
                onBackClick = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()
    val currentUser = auth.currentUser

    var isLoading by remember { mutableStateOf(false) }
    var isUploadingImage by remember { mutableStateOf(false) }

    // Basic Information
    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var profileImageUrl by remember { mutableStateOf("") }

    // Medical Information
    var bloodGroup by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }

    // Additional Information
    var address by remember { mutableStateOf("") }
    var emergencyContact by remember { mutableStateOf("") }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showImageSourceDialog by remember { mutableStateOf(false) }

    // Dropdown states
    var expandedGender by remember { mutableStateOf(false) }
    var expandedBloodGroup by remember { mutableStateOf(false) }

    val genderOptions = listOf("Male", "Female", "Other", "Prefer not to say")
    val bloodGroupOptions = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    // Load current user data
    LaunchedEffect(Unit) {
        currentUser?.let { user ->
            email = user.email ?: ""
            profileImageUrl = user.photoUrl?.toString() ?: ""

            try {
                val doc = firestore.collection("users")
                    .document(user.uid)
                    .get()
                    .await()

                displayName = doc.getString("displayName") ?: ""
                phoneNumber = doc.getString("phoneNumber") ?: ""
                profileImageUrl = doc.getString("photoUrl") ?: ""
                bloodGroup = doc.getString("bloodGroup") ?: ""
                dateOfBirth = doc.getString("dateOfBirth") ?: ""
                gender = doc.getString("gender") ?: ""
                address = doc.getString("address") ?: ""
                emergencyContact = doc.getString("emergencyContact") ?: ""
            } catch (e: Exception) {
                Toast.makeText(context, "Error loading profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            isUploadingImage = true

            scope.launch {
                try {
                    currentUser?.let { user ->
                        val imageRef = storage.reference
                            .child("profile_images/${user.uid}/${UUID.randomUUID()}.jpg")

                        imageRef.putFile(uri).await()
                        val downloadUrl = imageRef.downloadUrl.await().toString()

                        profileImageUrl = downloadUrl
                        isUploadingImage = false
                        Toast.makeText(context, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    isUploadingImage = false
                    Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B8FAC))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            }
    ) {
        // Top Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Edit Profile",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Main Content Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .fillMaxHeight(0.88f),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFB)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 28.dp)
                    .padding(top = 32.dp, bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Picture Section
                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0B8FAC).copy(alpha = 0.15f))
                            .border(3.dp, Color(0xFF0B8FAC).copy(alpha = 0.3f), CircleShape)
                            .clickable { showImageSourceDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isUploadingImage) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(40.dp),
                                color = Color(0xFF0B8FAC),
                                strokeWidth = 3.dp
                            )
                        } else if (profileImageUrl.isNotEmpty()) {
                            AsyncImage(
                                model = profileImageUrl,
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Profile",
                                modifier = Modifier.size(60.dp),
                                tint = Color(0xFF0B8FAC)
                            )
                        }
                    }

                    if (!isUploadingImage) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(Color(0xFF0B8FAC))
                                .border(3.dp, Color(0xFFF8FAFB), CircleShape)
                                .clickable { showImageSourceDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "Change Picture",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                if (profileImageUrl.isNotEmpty() && !isUploadingImage) {
                    Spacer(modifier = Modifier.height(12.dp))
                    TextButton(onClick = { showDeleteDialog = true }) {
                        Text(
                            "Remove Photo",
                            color = Color(0xFFD32F2F),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Basic Information Section
                SectionHeader(title = "Basic Information")

                Spacer(modifier = Modifier.height(16.dp))

                CustomerFormField(
                    label = "Full Name",
                    value = displayName,
                    onValueChange = { displayName = it },
                    placeholder = "Enter your full name",
                    enabled = !isLoading && !isUploadingImage
                )

                Spacer(modifier = Modifier.height(16.dp))

                CustomerFormField(
                    label = "Email Address",
                    value = email,
                    onValueChange = { },
                    placeholder = "Email",
                    enabled = false
                )

                Spacer(modifier = Modifier.height(16.dp))

                CustomerFormField(
                    label = "Phone Number",
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    placeholder = "10-digit phone number",
                    enabled = !isLoading && !isUploadingImage,
                    keyboardType = KeyboardType.Phone
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Medical Information Section
                SectionHeader(title = "Medical Information")

                Spacer(modifier = Modifier.height(16.dp))

                // Blood Group Dropdown
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Blood Group",
                        style = TextStyle(
                            color = Color(0xFF2D3748),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    ExposedDropdownMenuBox(
                        expanded = expandedBloodGroup,
                        onExpandedChange = {
                            if (!isLoading && !isUploadingImage) {
                                expandedBloodGroup = it
                            }
                        }
                    ) {
                        OutlinedTextField(
                            value = bloodGroup,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = {
                                Text(
                                    "Select blood group",
                                    color = Color.Gray.copy(alpha = 0.6f),
                                    fontSize = 14.sp
                                )
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBloodGroup)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isLoading && !isUploadingImage,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                disabledContainerColor = Color.White,
                                focusedIndicatorColor = Color(0xFF0B8FAC),
                                unfocusedIndicatorColor = Color(0xFFBDBDBD),
                                cursorColor = Color(0xFF0B8FAC),
                                focusedTextColor = Color(0xFF2D3748),
                                unfocusedTextColor = Color(0xFF2D3748)
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = expandedBloodGroup,
                            onDismissRequest = { expandedBloodGroup = false }
                        ) {
                            bloodGroupOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        bloodGroup = option
                                        expandedBloodGroup = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                CustomerFormField(
                    label = "Date of Birth",
                    value = dateOfBirth,
                    onValueChange = { dateOfBirth = it },
                    placeholder = "YYYY-MM-DD",
                    enabled = !isLoading && !isUploadingImage
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Gender Dropdown
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Gender",
                        style = TextStyle(
                            color = Color(0xFF2D3748),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    ExposedDropdownMenuBox(
                        expanded = expandedGender,
                        onExpandedChange = {
                            if (!isLoading && !isUploadingImage) {
                                expandedGender = it
                            }
                        }
                    ) {
                        OutlinedTextField(
                            value = gender,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = {
                                Text(
                                    "Select gender",
                                    color = Color.Gray.copy(alpha = 0.6f),
                                    fontSize = 14.sp
                                )
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGender)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isLoading && !isUploadingImage,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                disabledContainerColor = Color.White,
                                focusedIndicatorColor = Color(0xFF0B8FAC),
                                unfocusedIndicatorColor = Color(0xFFBDBDBD),
                                cursorColor = Color(0xFF0B8FAC),
                                focusedTextColor = Color(0xFF2D3748),
                                unfocusedTextColor = Color(0xFF2D3748)
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = expandedGender,
                            onDismissRequest = { expandedGender = false }
                        ) {
                            genderOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        gender = option
                                        expandedGender = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Additional Information Section
                SectionHeader(title = "Additional Information")

                Spacer(modifier = Modifier.height(16.dp))

                CustomerFormField(
                    label = "Address",
                    value = address,
                    onValueChange = { address = it },
                    placeholder = "Enter your complete address",
                    enabled = !isLoading && !isUploadingImage,
                    singleLine = false,
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                CustomerFormField(
                    label = "Emergency Contact",
                    value = emergencyContact,
                    onValueChange = { emergencyContact = it },
                    placeholder = "Emergency contact number",
                    enabled = !isLoading && !isUploadingImage,
                    keyboardType = KeyboardType.Phone
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Save Button
                Button(
                    onClick = {
                        if (displayName.isBlank()) {
                            Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isLoading = true
                        scope.launch {
                            try {
                                currentUser?.let { user ->
                                    firestore.collection("users")
                                        .document(user.uid)
                                        .update(
                                            mapOf(
                                                "displayName" to displayName,
                                                "phoneNumber" to phoneNumber,
                                                "photoUrl" to profileImageUrl,
                                                "bloodGroup" to bloodGroup,
                                                "dateOfBirth" to dateOfBirth,
                                                "gender" to gender,
                                                "address" to address,
                                                "emergencyContact" to emergencyContact
                                            )
                                        )
                                        .await()

                                    Toast.makeText(
                                        context,
                                        "Profile updated successfully!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onBackClick()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Error: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = !isLoading && !isUploadingImage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2D3748),
                        disabledContainerColor = Color(0xFF2D3748).copy(alpha = 0.6f)
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Save Changes",
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Delete Photo Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Remove Profile Picture", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove your profile picture?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        profileImageUrl = ""
                        selectedImageUri = null
                        Toast.makeText(context, "Photo removed", Toast.LENGTH_SHORT).show()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Remove", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Image Source Selection Dialog
    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = {
                Text(
                    "Choose Photo Source",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748)
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Gallery Option
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showImageSourceDialog = false
                                imagePickerLauncher.launch("image/*")
                            },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.PhotoLibrary,
                                contentDescription = "Gallery",
                                tint = Color(0xFF0B8FAC),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                "Choose from Gallery",
                                fontSize = 16.sp,
                                color = Color(0xFF2D3748),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showImageSourceDialog = false }) {
                    Text("Cancel", color = Color.Gray, fontWeight = FontWeight.Medium)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color(0xFFF8FAFB)
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(20.dp)
                .background(
                    color = Color(0xFF0B8FAC),
                    shape = RoundedCornerShape(2.dp)
                )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = TextStyle(
                color = Color(0xFF2D3748),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomerFormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = TextStyle(
                color = Color(0xFF2D3748),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    placeholder,
                    color = Color.Gray.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
            },
            enabled = enabled,
            singleLine = singleLine,
            minLines = minLines,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedIndicatorColor = Color(0xFF0B8FAC),
                unfocusedIndicatorColor = Color(0xFFBDBDBD),
                cursorColor = Color(0xFF0B8FAC),
                focusedTextColor = Color(0xFF2D3748),
                unfocusedTextColor = Color(0xFF2D3748),
                disabledTextColor = Color.Gray
            )
        )
    }
}