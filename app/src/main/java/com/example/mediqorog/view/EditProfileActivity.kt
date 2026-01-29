package com.example.mediqorog.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mediqorog.model.User
import com.example.mediqorog.repository.UserRepoImpl
import com.example.mediqorog.ui.theme.MediqorOGTheme
import com.example.mediqorog.viewmodel.UserViewModel

class EditProfileActivity : ComponentActivity() {
    private val viewModel: UserViewModel by lazy {
        UserViewModel(UserRepoImpl())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MediqorOGTheme {
                EditProfileScreen(
                    viewModel = viewModel,
                    onBack = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: UserViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val user by viewModel.user.collectAsState()

    // Form state variables
    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var emergencyContact by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Initialize form with current user data
    LaunchedEffect(user) {
        user?.let {
            displayName = it.displayName
            email = it.email
            phoneNumber = it.phoneNumber
            bloodGroup = it.bloodGroup
            dateOfBirth = it.dateOfBirth
            gender = it.gender
            address = it.address
            emergencyContact = it.emergencyContact
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Profile Picture Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (user?.photoUrl?.isNotEmpty() == true) {
                    AsyncImage(
                        model = user?.photoUrl,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Default Profile",
                        modifier = Modifier.size(120.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Basic Information
            Text(
                text = "Basic Information",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            ProfileTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = "Full Name",
                icon = Icons.Default.Person
            )

            ProfileTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                icon = Icons.Default.Email,
                enabled = false
            )

            ProfileTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = "Phone Number",
                icon = Icons.Default.Phone,
                keyboardType = KeyboardType.Phone
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Medical Information
            Text(
                text = "Medical Information",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            ProfileTextField(
                value = bloodGroup,
                onValueChange = { bloodGroup = it },
                label = "Blood Group",
                icon = Icons.Default.Favorite,
                placeholder = "e.g., A+, O-, AB+"
            )

            ProfileTextField(
                value = dateOfBirth,
                onValueChange = { dateOfBirth = it },
                label = "Date of Birth",
                icon = Icons.Default.DateRange,
                placeholder = "YYYY-MM-DD"
            )

            // Gender Dropdown
            var expandedGender by remember { mutableStateOf(false) }
            val genderOptions = listOf("Male", "Female", "Other", "Prefer not to say")

            ExposedDropdownMenuBox(
                expanded = expandedGender,
                onExpandedChange = { expandedGender = it }
            ) {
                OutlinedTextField(
                    value = gender,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Gender") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGender)
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Person, "Gender")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .padding(vertical = 8.dp)
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

            Spacer(modifier = Modifier.height(16.dp))

            // Additional Information
            Text(
                text = "Additional Information",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            ProfileTextField(
                value = address,
                onValueChange = { address = it },
                label = "Address",
                icon = Icons.Default.LocationOn,
                singleLine = false,
                maxLines = 3
            )

            ProfileTextField(
                value = emergencyContact,
                onValueChange = { emergencyContact = it },
                label = "Emergency Contact",
                icon = Icons.Default.Phone,
                keyboardType = KeyboardType.Phone
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    user?.let { currentUser ->
                        isLoading = true
                        val updatedUser = currentUser.copy(
                            displayName = displayName,
                            phoneNumber = phoneNumber,
                            bloodGroup = bloodGroup,
                            dateOfBirth = dateOfBirth,
                            gender = gender,
                            address = address,
                            emergencyContact = emergencyContact
                        )

                        viewModel.updateUser(updatedUser)
                        Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                        isLoading = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Save Changes")
                }
            }
        }
    }
}

@Composable
fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
    placeholder: String = ""
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(icon, label) },
        enabled = enabled,
        singleLine = singleLine,
        maxLines = maxLines,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}