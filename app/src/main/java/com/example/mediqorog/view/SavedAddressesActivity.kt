package com.example.mediqorog.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class Address(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val addressLine: String = "",
    val landmark: String = "",
    val city: String = "",
    val state: String = "",
    val pincode: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readByte() != 0.toByte(),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(phone)
        parcel.writeString(addressLine)
        parcel.writeString(landmark)
        parcel.writeString(city)
        parcel.writeString(state)
        parcel.writeString(pincode)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeByte(if (isDefault) 1 else 0)
        parcel.writeLong(createdAt)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Address> {
        override fun createFromParcel(parcel: Parcel): Address = Address(parcel)
        override fun newArray(size: Int): Array<Address?> = arrayOfNulls(size)
    }
}

class SavedAddressesActivity : ComponentActivity() {

    private val TAG = "SavedAddressesActivity"
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var isSelectMode = false

    private val addAddressLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d(TAG, "Result received: ${result.resultCode}")
        if (result.resultCode == RESULT_OK) {
            Toast.makeText(this, "Address saved!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if we're in selection mode
        isSelectMode = intent.getBooleanExtra("SELECT_MODE", false)

        Log.d(TAG, "SavedAddressesActivity created, SELECT_MODE: $isSelectMode")

        setContent {
            MaterialTheme {
                SavedAddressesScreen()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SavedAddressesScreen() {
        var addresses by remember { mutableStateOf<List<Address>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }
        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        // Load addresses
        LaunchedEffect(Unit) {
            Log.d(TAG, "Loading addresses...")
            val userId = auth.currentUser?.uid
            Log.d(TAG, "User ID: $userId")

            if (userId != null) {
                firestore.collection("users")
                    .document(userId)
                    .collection("addresses")
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.e(TAG, "Error loading addresses", error)
                            isLoading = false
                            return@addSnapshotListener
                        }

                        val loadedAddresses = snapshot?.documents?.mapNotNull { doc ->
                            try {
                                Address(
                                    id = doc.id,
                                    name = doc.getString("name") ?: "",
                                    phone = doc.getString("phone") ?: "",
                                    addressLine = doc.getString("addressLine") ?: "",
                                    landmark = doc.getString("landmark") ?: "",
                                    city = doc.getString("city") ?: "",
                                    state = doc.getString("state") ?: "",
                                    pincode = doc.getString("pincode") ?: "",
                                    latitude = doc.getDouble("latitude") ?: 0.0,
                                    longitude = doc.getDouble("longitude") ?: 0.0,
                                    isDefault = doc.getBoolean("isDefault") ?: false,
                                    createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                                )
                            } catch (e: Exception) {
                                Log.e(TAG, "Error parsing address", e)
                                null
                            }
                        } ?: emptyList()

                        Log.d(TAG, "Loaded ${loadedAddresses.size} addresses")
                        addresses = loadedAddresses
                        isLoading = false
                    }
            } else {
                Log.e(TAG, "No user logged in")
                isLoading = false
            }
        }

        fun openAddAddress() {
            try {
                Log.d(TAG, "Opening AddEditAddressActivity...")
                val intent = Intent(this@SavedAddressesActivity, AddEditAddressActivity::class.java)
                addAddressLauncher.launch(intent)
                Log.d(TAG, "Intent launched successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error opening activity", e)
                Toast.makeText(
                    this@SavedAddressesActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        fun openEditAddress(addressId: String) {
            try {
                Log.d(TAG, "Opening edit for address: $addressId")
                val intent = Intent(this@SavedAddressesActivity, AddEditAddressActivity::class.java).apply {
                    putExtra("ADDRESS_ID", addressId)
                }
                addAddressLauncher.launch(intent)
            } catch (e: Exception) {
                Log.e(TAG, "Error opening edit", e)
                Toast.makeText(
                    this@SavedAddressesActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        fun selectAddress(address: Address) {
            Log.d(TAG, "Address selected: ${address.name}")
            val resultIntent = Intent().apply {
                putExtra("SELECTED_ADDRESS", address)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        // Auto-navigate to AddEditAddressActivity if no addresses and in select mode
        LaunchedEffect(isLoading, addresses.isEmpty()) {
            if (!isLoading && addresses.isEmpty() && isSelectMode) {
                Log.d(TAG, "No addresses found, opening AddEditAddressActivity")
                openAddAddress()
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            if (isSelectMode) "Select Delivery Address" else "Saved Addresses",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { finish() }) {
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
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        Log.d(TAG, "FAB clicked")
                        openAddAddress()
                    },
                    containerColor = Color(0xFF0B8FAC)
                ) {
                    Icon(Icons.Filled.Add, "Add", tint = Color.White)
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF5F5F5))
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color(0xFF0B8FAC)
                        )
                    }
                    addresses.isEmpty() -> {
                        EmptyView(onAddClick = {
                            Log.d(TAG, "Empty view button clicked")
                            openAddAddress()
                        })
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(addresses, key = { it.id }) { address ->
                                AddressCard(
                                    address = address,
                                    isSelectMode = isSelectMode,
                                    onSelect = { selectAddress(address) },
                                    onEdit = { openEditAddress(address.id) },
                                    onDelete = {
                                        scope.launch {
                                            try {
                                                val userId = auth.currentUser?.uid
                                                if (userId != null) {
                                                    firestore.collection("users")
                                                        .document(userId)
                                                        .collection("addresses")
                                                        .document(address.id)
                                                        .delete()
                                                        .await()
                                                    snackbarHostState.showSnackbar("Address deleted")
                                                }
                                            } catch (e: Exception) {
                                                Log.e(TAG, "Delete error", e)
                                                snackbarHostState.showSnackbar("Failed to delete")
                                            }
                                        }
                                    },
                                    onSetDefault = {
                                        scope.launch {
                                            try {
                                                val userId = auth.currentUser?.uid
                                                if (userId != null) {
                                                    val batch = firestore.batch()
                                                    val docs = firestore.collection("users")
                                                        .document(userId)
                                                        .collection("addresses")
                                                        .get()
                                                        .await()

                                                    docs.documents.forEach { doc ->
                                                        batch.update(
                                                            doc.reference,
                                                            "isDefault",
                                                            doc.id == address.id
                                                        )
                                                    }
                                                    batch.commit().await()
                                                    snackbarHostState.showSnackbar("Default address updated")
                                                }
                                            } catch (e: Exception) {
                                                Log.e(TAG, "Set default error", e)
                                                snackbarHostState.showSnackbar("Failed to update")
                                            }
                                        }
                                    }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(80.dp)) }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun EmptyView(onAddClick: () -> Unit) {
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
                modifier = Modifier.size(100.dp),
                tint = Color(0xFF0B8FAC).copy(alpha = 0.3f)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "No saved addresses",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3748)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Add delivery addresses for quick checkout",
                fontSize = 15.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    Log.d(TAG, "Add button in empty view clicked")
                    onAddClick()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B8FAC)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Your First Address", fontWeight = FontWeight.SemiBold)
            }
        }
    }

    @Composable
    fun AddressCard(
        address: Address,
        isSelectMode: Boolean,
        onSelect: () -> Unit,
        onEdit: () -> Unit,
        onDelete: () -> Unit,
        onSetDefault: () -> Unit
    ) {
        var showMenu by remember { mutableStateOf(false) }
        var showDeleteDialog by remember { mutableStateOf(false) }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (isSelectMode) {
                        Modifier.clickable { onSelect() }
                    } else {
                        Modifier
                    }
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header Row - Name and Menu
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            address.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                        if (address.isDefault) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF10B981)
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

                    if (!isSelectMode) {
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Filled.MoreVert, "Options", tint = Color(0xFF9CA3AF))
                            }

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                if (!address.isDefault) {
                                    DropdownMenuItem(
                                        text = { Text("Set as Default") },
                                        onClick = {
                                            onSetDefault()
                                            showMenu = false
                                        },
                                        leadingIcon = { Icon(Icons.Filled.CheckCircle, null) }
                                    )
                                }
                                DropdownMenuItem(
                                    text = { Text("Edit") },
                                    onClick = {
                                        onEdit()
                                        showMenu = false
                                    },
                                    leadingIcon = { Icon(Icons.Filled.Edit, null) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Delete", color = Color(0xFFEF4444)) },
                                    onClick = {
                                        showDeleteDialog = true
                                        showMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Filled.Delete, null, tint = Color(0xFFEF4444))
                                    }
                                )
                            }
                        }
                    } else {
                        Icon(
                            Icons.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color(0xFF0B8FAC)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Phone Number
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Filled.Phone,
                        contentDescription = null,
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        address.phone,
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Address Line
                Text(
                    address.addressLine,
                    fontSize = 14.sp,
                    color = Color(0xFF374151),
                    lineHeight = 20.sp
                )

                // Landmark
                if (address.landmark.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Near ${address.landmark}",
                        fontSize = 12.sp,
                        color = Color(0xFF9CA3AF),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Divider
                Divider(color = Color(0xFFE5E7EB), thickness = 1.dp)

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Row - City, State, Pincode
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            buildString {
                                if (address.city.isNotEmpty()) append(address.city)
                                if (address.city.isNotEmpty() && address.state.isNotEmpty()) append(", ")
                                if (address.state.isNotEmpty()) append(address.state)
                            },
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                        )
                    }

                    if (address.pincode.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFF3F4F6)
                        ) {
                            Text(
                                address.pincode,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF374151)
                            )
                        }
                    }
                }

                // Select button in select mode
                if (isSelectMode) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onSelect,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0B8FAC)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Deliver Here", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                icon = {
                    Icon(
                        Icons.Filled.Delete,
                        null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(48.dp)
                    )
                },
                title = { Text("Delete Address?", fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "Are you sure you want to delete '${address.name}'?",
                        textAlign = TextAlign.Center
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onDelete()
                            showDeleteDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}