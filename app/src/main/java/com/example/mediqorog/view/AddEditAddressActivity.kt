package com.example.mediqorog.view

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AddEditAddressActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val addressId = intent.getStringExtra("ADDRESS_ID")

        setContent {
            MaterialTheme {
                AddEditScreen(
                    addressId = addressId,
                    onSuccess = {
                        setResult(Activity.RESULT_OK)
                        finish()
                    },
                    onCancel = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    addressId: String?,
    onSuccess: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var addressLine by remember { mutableStateOf("") }
    var landmark by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var pincode by remember { mutableStateOf("") }
    var isDefault by remember { mutableStateOf(false) }
    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }

    var isLoadingLocation by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Load existing address
    LaunchedEffect(addressId) {
        if (addressId != null) {
            isLoading = true
            withContext(Dispatchers.IO) {
                try {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@withContext
                    val doc = FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(userId)
                        .collection("addresses")
                        .document(addressId)
                        .get()
                        .await()

                    if (doc.exists()) {
                        withContext(Dispatchers.Main) {
                            name = doc.getString("name") ?: ""
                            phone = doc.getString("phone") ?: ""
                            addressLine = doc.getString("addressLine") ?: ""
                            landmark = doc.getString("landmark") ?: ""
                            city = doc.getString("city") ?: ""
                            state = doc.getString("state") ?: ""
                            pincode = doc.getString("pincode") ?: ""
                            isDefault = doc.getBoolean("isDefault") ?: false
                            latitude = doc.getDouble("latitude") ?: 0.0
                            longitude = doc.getDouble("longitude") ?: 0.0
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Error loading: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } finally {
                    isLoading = false
                }
            }
        }
    }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val geocoder = remember { @Suppress("DEPRECATION") Geocoder(context) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { it }) {
            isLoadingLocation = true
            scope.launch {
                try {
                    val token = CancellationTokenSource()
                    @Suppress("MissingPermission")
                    val location: Location? = withContext(Dispatchers.IO) {
                        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, token.token).await()
                    }

                    location?.let { loc ->
                        latitude = loc.latitude
                        longitude = loc.longitude

                        withContext(Dispatchers.IO) {
                            @Suppress("DEPRECATION")
                            val addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
                            addresses?.firstOrNull()?.let { addr ->
                                withContext(Dispatchers.Main) {
                                    addressLine = listOfNotNull(addr.subThoroughfare, addr.thoroughfare).joinToString(", ")
                                    landmark = addr.featureName ?: ""
                                    city = addr.locality ?: addr.subAdminArea ?: ""
                                    state = addr.adminArea ?: ""
                                    pincode = addr.postalCode ?: ""
                                    snackbarHostState.showSnackbar("Location detected!")
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar("Error: ${e.message}")
                } finally {
                    isLoadingLocation = false
                }
            }
        }
    }

    fun requestLocation() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            isLoadingLocation = true
            scope.launch {
                try {
                    val token = CancellationTokenSource()
                    @Suppress("MissingPermission")
                    val location: Location? = withContext(Dispatchers.IO) {
                        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, token.token).await()
                    }

                    location?.let { loc ->
                        latitude = loc.latitude
                        longitude = loc.longitude

                        withContext(Dispatchers.IO) {
                            @Suppress("DEPRECATION")
                            val addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
                            addresses?.firstOrNull()?.let { addr ->
                                withContext(Dispatchers.Main) {
                                    addressLine = listOfNotNull(addr.subThoroughfare, addr.thoroughfare).joinToString(", ")
                                    landmark = addr.featureName ?: ""
                                    city = addr.locality ?: addr.subAdminArea ?: ""
                                    state = addr.adminArea ?: ""
                                    pincode = addr.postalCode ?: ""
                                    snackbarHostState.showSnackbar("Location detected!")
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar("Error: ${e.message}")
                } finally {
                    isLoadingLocation = false
                }
            }
        } else {
            locationPermissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    fun saveAddress() {
        when {
            name.isBlank() -> scope.launch { snackbarHostState.showSnackbar("Enter label") }
            phone.isBlank() -> scope.launch { snackbarHostState.showSnackbar("Enter phone") }
            addressLine.isBlank() -> scope.launch { snackbarHostState.showSnackbar("Enter address") }
            pincode.isBlank() -> scope.launch { snackbarHostState.showSnackbar("Enter pincode") }
            else -> {
                isSaving = true
                scope.launch {
                    try {
                        val userId = FirebaseAuth.getInstance().currentUser?.uid
                        if (userId == null) {
                            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
                            isSaving = false
                            return@launch
                        }

                        val firestore = FirebaseFirestore.getInstance()
                        val newId = addressId ?: firestore.collection("users").document(userId)
                            .collection("addresses").document().id

                        withContext(Dispatchers.IO) {
                            // Unset other defaults if this is default
                            if (isDefault) {
                                val docs = firestore.collection("users").document(userId)
                                    .collection("addresses").get().await()
                                val batch = firestore.batch()
                                docs.documents.forEach { doc ->
                                    if (doc.id != newId) {
                                        batch.update(doc.reference, "isDefault", false)
                                    }
                                }
                                batch.commit().await()
                            }

                            // Save address
                            val data = hashMapOf(
                                "id" to newId,
                                "name" to name,
                                "phone" to phone,
                                "addressLine" to addressLine,
                                "landmark" to landmark,
                                "city" to city,
                                "state" to state,
                                "pincode" to pincode,
                                "latitude" to latitude,
                                "longitude" to longitude,
                                "isDefault" to isDefault,
                                "createdAt" to System.currentTimeMillis()
                            )

                            firestore.collection("users").document(userId)
                                .collection("addresses").document(newId)
                                .set(data).await()
                        }

                        Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
                        onSuccess()

                    } catch (e: Exception) {
                        Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                    } finally {
                        isSaving = false
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (addressId == null) "Add Address" else "Edit Address", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0B8FAC),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel", fontWeight = FontWeight.SemiBold)
                    }
                    Button(
                        onClick = { saveAddress() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B8FAC)),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isSaving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Save", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF0B8FAC))
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF5F5F5))
                    .verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { requestLocation() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    enabled = !isLoadingLocation,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoadingLocation) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                        Text("Detecting...")
                    } else {
                        Icon(Icons.Filled.MyLocation, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Use Current Location")
                    }
                }

                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Label (e.g., Home)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                OutlinedTextField(value = addressLine, onValueChange = { addressLine = it }, label = { Text("Complete Address") }, modifier = Modifier.fillMaxWidth(), minLines = 3, shape = RoundedCornerShape(12.dp))
                OutlinedTextField(value = landmark, onValueChange = { landmark = it }, label = { Text("Landmark (Optional)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("City") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = state, onValueChange = { state = it }, label = { Text("State") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                }

                OutlinedTextField(value = pincode, onValueChange = { pincode = it }, label = { Text("Pincode") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F9FF)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { isDefault = !isDefault }.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isDefault,
                            onCheckedChange = { isDefault = it },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0B8FAC))
                        )
                        Spacer(Modifier.width(12.dp))
                        Text("Make this my default address", fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(Modifier.height(20.dp))
            }
        }
    }
}