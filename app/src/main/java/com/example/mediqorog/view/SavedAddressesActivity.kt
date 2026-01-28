// ========== SavedAddressesActivity.kt ==========
package com.example.mediqorog.view

import android.os.Bundle
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
import java.util.*

class SavedAddressesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SavedAddressesScreen(onBackClick = { finish() })
        }
    }
}

data class Address(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val addressLine: String = "",
    val landmark: String = "",
    val pincode: String = "",
    val isDefault: Boolean = false
)

class SavedAddressesViewModel : ViewModel() {
    private val _addresses = MutableStateFlow<List<Address>>(emptyList())
    val addresses: StateFlow<List<Address>> = _addresses

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    init {
        loadAddresses()
    }

    private fun loadAddresses() {
        _loading.value = true
        val userId = auth.currentUser?.uid

        if (userId != null) {
            firestore.collection("users")
                .document(userId)
                .collection("addresses")
                .get()
                .addOnSuccessListener { documents ->
                    _addresses.value = documents.mapNotNull { it.toObject(Address::class.java) }
                    _loading.value = false
                }
                .addOnFailureListener {
                    _loading.value = false
                }
        } else {
            _loading.value = false
        }
    }

    suspend fun saveAddress(address: Address): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("Not logged in")
            val addressId = if (address.id.isEmpty()) UUID.randomUUID().toString() else address.id

            firestore.collection("users")
                .document(userId)
                .collection("addresses")
                .document(addressId)
                .set(address.copy(id = addressId))
                .await()

            loadAddresses()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteAddress(addressId: String): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("Not logged in")

            firestore.collection("users")
                .document(userId)
                .collection("addresses")
                .document(addressId)
                .delete()
                .await()

            loadAddresses()
            true
        } catch (e: Exception) {
            false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedAddressesScreen(onBackClick: () -> Unit) {
    val viewModel: SavedAddressesViewModel = viewModel()
    val addresses by viewModel.addresses.collectAsState()
    val loading by viewModel.loading.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingAddress by remember { mutableStateOf<Address?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Addresses", fontWeight = FontWeight.Bold) },
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
                Icon(Icons.Filled.Add, contentDescription = "Add Address", tint = Color.White)
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
            } else if (addresses.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No saved addresses", fontSize = 18.sp, color = Color.Gray)
                    Text("Add an address to get started", fontSize = 14.sp, color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(addresses) { address ->
                        AddressCard(
                            address = address,
                            onEdit = { editingAddress = address },
                            onDelete = {
                                scope.launch {
                                    val success = viewModel.deleteAddress(address.id)
                                    if (success) {
                                        snackbarHostState.showSnackbar("Address deleted")
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
        AddEditAddressDialog(
            address = null,
            onDismiss = { showAddDialog = false },
            onSave = { address ->
                scope.launch {
                    val success = viewModel.saveAddress(address)
                    if (success) {
                        snackbarHostState.showSnackbar("Address saved!")
                        showAddDialog = false
                    }
                }
            }
        )
    }

    editingAddress?.let { address ->
        AddEditAddressDialog(
            address = address,
            onDismiss = { editingAddress = null },
            onSave = { updatedAddress ->
                scope.launch {
                    val success = viewModel.saveAddress(updatedAddress)
                    if (success) {
                        snackbarHostState.showSnackbar("Address updated!")
                        editingAddress = null
                    }
                }
            }
        )
    }
}

@Composable
fun AddressCard(address: Address, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Color(0xFF0B8FAC))
                    Text(address.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                if (address.isDefault) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF4CAF50)
                    ) {
                        Text(
                            "DEFAULT",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 10.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(address.phone, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(address.addressLine, fontSize = 14.sp)
            if (address.landmark.isNotEmpty()) {
                Text("Landmark: ${address.landmark}", fontSize = 13.sp, color = Color.Gray)
            }
            Text("PIN: ${address.pincode}", fontSize = 13.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit")
                }
                TextButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
fun AddEditAddressDialog(address: Address?, onDismiss: () -> Unit, onSave: (Address) -> Unit) {
    var name by remember { mutableStateOf(address?.name ?: "") }
    var phone by remember { mutableStateOf(address?.phone ?: "") }
    var addressLine by remember { mutableStateOf(address?.addressLine ?: "") }
    var landmark by remember { mutableStateOf(address?.landmark ?: "") }
    var pincode by remember { mutableStateOf(address?.pincode ?: "") }
    var isDefault by remember { mutableStateOf(address?.isDefault ?: false) }

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
                Text(
                    if (address == null) "Add Address" else "Edit Address",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = addressLine,
                    onValueChange = { addressLine = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                OutlinedTextField(
                    value = landmark,
                    onValueChange = { landmark = it },
                    label = { Text("Landmark (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = pincode,
                    onValueChange = { pincode = it },
                    label = { Text("Pincode") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isDefault, onCheckedChange = { isDefault = it })
                    Text("Set as default address")
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
                            val newAddress = Address(
                                id = address?.id ?: "",
                                name = name,
                                phone = phone,
                                addressLine = addressLine,
                                landmark = landmark,
                                pincode = pincode,
                                isDefault = isDefault
                            )
                            onSave(newAddress)
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