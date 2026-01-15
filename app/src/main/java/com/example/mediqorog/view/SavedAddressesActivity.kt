package com.example.mediqorog.view

import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.UUID

// Data Models
data class DeliveryAddress(
    val id: String,
    val label: String, // Home, Work, Other
    val fullAddress: String,
    val landmark: String,
    val phoneNumber: String,
    val isDefault: Boolean = false
)

data class NearbyPlace(
    val id: String,
    val name: String,
    val type: PlaceType,
    val address: String,
    val distance: String,
    val rating: Float,
    val isOpen24Hours: Boolean,
    val phoneNumber: String,
    val latitude: Double,
    val longitude: Double,
    val specialization: String? = null // For clinics
)

enum class PlaceType {
    HOSPITAL, CLINIC, PHARMACY, EMERGENCY
}

class SavedAddressesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SavedAddressesScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedAddressesScreen() {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    var showAddAddressDialog by remember { mutableStateOf(false) }
    var selectedPlaceType by remember { mutableStateOf(PlaceType.HOSPITAL) }

    // Sample delivery addresses
    val deliveryAddresses = remember {
        mutableStateListOf(
            DeliveryAddress(
                id = "1",
                label = "Home",
                fullAddress = "Thamel, Kathmandu 44600",
                landmark = "Near Thamel Chowk",
                phoneNumber = "+977 9800000000",
                isDefault = true
            ),
            DeliveryAddress(
                id = "2",
                label = "Work",
                fullAddress = "Durbar Marg, Kathmandu",
                landmark = "Opposite Hotel Yak & Yeti",
                phoneNumber = "+977 9800000000",
                isDefault = false
            )
        )
    }

    // Sample nearby places
    val nearbyPlaces = remember {
        listOf(
            NearbyPlace(
                id = "1",
                name = "Norvic International Hospital",
                type = PlaceType.HOSPITAL,
                address = "Thapathali, Kathmandu",
                distance = "2.3 km",
                rating = 4.5f,
                isOpen24Hours = true,
                phoneNumber = "+977 1-4258554",
                latitude = 27.6942,
                longitude = 85.3161
            ),
            NearbyPlace(
                id = "2",
                name = "Grande International Hospital",
                type = PlaceType.HOSPITAL,
                address = "Dhapasi, Kathmandu",
                distance = "3.1 km",
                rating = 4.3f,
                isOpen24Hours = true,
                phoneNumber = "+977 1-5159266",
                latitude = 27.7350,
                longitude = 85.3300
            ),
            NearbyPlace(
                id = "3",
                name = "Medicare National Hospital",
                type = PlaceType.HOSPITAL,
                address = "Chabahil, Kathmandu",
                distance = "4.5 km",
                rating = 4.2f,
                isOpen24Hours = true,
                phoneNumber = "+977 1-4910720",
                latitude = 27.7200,
                longitude = 85.3500
            ),
            NearbyPlace(
                id = "4",
                name = "City Care Clinic",
                type = PlaceType.CLINIC,
                address = "New Baneshwor, Kathmandu",
                distance = "1.8 km",
                rating = 4.0f,
                isOpen24Hours = false,
                phoneNumber = "+977 1-4782834",
                latitude = 27.6900,
                longitude = 85.3400,
                specialization = "General Medicine, Pediatrics"
            ),
            NearbyPlace(
                id = "5",
                name = "Valley Dental Clinic",
                type = PlaceType.CLINIC,
                address = "Lazimpat, Kathmandu",
                distance = "2.0 km",
                rating = 4.4f,
                isOpen24Hours = false,
                phoneNumber = "+977 1-4411234",
                latitude = 27.7150,
                longitude = 85.3250,
                specialization = "Dentistry"
            ),
            NearbyPlace(
                id = "6",
                name = "Health Plus Pharmacy",
                type = PlaceType.PHARMACY,
                address = "Thamel, Kathmandu",
                distance = "0.5 km",
                rating = 4.6f,
                isOpen24Hours = true,
                phoneNumber = "+977 1-4700123",
                latitude = 27.7150,
                longitude = 85.3120
            ),
            NearbyPlace(
                id = "7",
                name = "MediCare Pharmacy",
                type = PlaceType.PHARMACY,
                address = "New Road, Kathmandu",
                distance = "1.2 km",
                rating = 4.3f,
                isOpen24Hours = false,
                phoneNumber = "+977 1-4256789",
                latitude = 27.7050,
                longitude = 85.3100
            ),
            NearbyPlace(
                id = "8",
                name = "Nepal Ambulance Service",
                type = PlaceType.EMERGENCY,
                address = "24/7 Emergency Service",
                distance = "Nearest available",
                rating = 5.0f,
                isOpen24Hours = true,
                phoneNumber = "102",
                latitude = 0.0,
                longitude = 0.0
            )
        )
    }

    val filteredPlaces = nearbyPlaces.filter {
        when (selectedPlaceType) {
            PlaceType.HOSPITAL -> it.type == PlaceType.HOSPITAL
            PlaceType.CLINIC -> it.type == PlaceType.CLINIC
            PlaceType.PHARMACY -> it.type == PlaceType.PHARMACY
            PlaceType.EMERGENCY -> it.type == PlaceType.EMERGENCY
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Addresses") },
                navigationIcon = {
                    IconButton(onClick = { (context as ComponentActivity).finish() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
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
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { showAddAddressDialog = true },
                    containerColor = Color(0xFF0B8FAC)
                ) {
                    Icon(Icons.Default.Add, "Add Address", tint = Color.White)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {

            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Color(0xFF0B8FAC)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Delivery") },
                    icon = { Icon(Icons.Outlined.Home, null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Nearby") },
                    icon = { Icon(Icons.Outlined.LocationOn, null) }
                )
            }

            when (selectedTab) {
                0 -> {
                    // Delivery Addresses Tab
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(deliveryAddresses) { address ->
                            DeliveryAddressCard(
                                address = address,
                                onEdit = { /* TODO */ },
                                onDelete = { deliveryAddresses.remove(address) },
                                onSetDefault = {
                                    deliveryAddresses.replaceAll {
                                        it.copy(isDefault = it.id == address.id)
                                    }
                                }
                            )
                        }
                    }
                }
                1 -> {
                    // Nearby Places Tab
                    Column {
                        // Emergency Quick Access
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val intent = Intent(Intent.ACTION_DIAL).apply {
                                            data = Uri.parse("tel:102")
                                        }
                                        context.startActivity(intent)
                                    }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFD32F2F),
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(
                                        Icons.Default.LocalHospital,
                                        contentDescription = "Emergency",
                                        tint = Color.White,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Emergency Service",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color(0xFFD32F2F)
                                    )
                                    Text(
                                        "Call 102 for ambulance",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                }
                                Icon(
                                    Icons.Default.Phone,
                                    contentDescription = "Call",
                                    tint = Color(0xFFD32F2F)
                                )
                            }
                        }

                        // Filter Chips
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = selectedPlaceType == PlaceType.HOSPITAL,
                                onClick = { selectedPlaceType = PlaceType.HOSPITAL },
                                label = { Text("Hospitals") },
                                leadingIcon = if (selectedPlaceType == PlaceType.HOSPITAL) {
                                    { Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp)) }
                                } else null
                            )
                            FilterChip(
                                selected = selectedPlaceType == PlaceType.CLINIC,
                                onClick = { selectedPlaceType = PlaceType.CLINIC },
                                label = { Text("Clinics") },
                                leadingIcon = if (selectedPlaceType == PlaceType.CLINIC) {
                                    { Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp)) }
                                } else null
                            )
                            FilterChip(
                                selected = selectedPlaceType == PlaceType.PHARMACY,
                                onClick = { selectedPlaceType = PlaceType.PHARMACY },
                                label = { Text("Pharmacies") },
                                leadingIcon = if (selectedPlaceType == PlaceType.PHARMACY) {
                                    { Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp)) }
                                } else null
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Nearby Places List
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredPlaces) { place ->
                                NearbyPlaceCard(place = place)
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Address Dialog
    if (showAddAddressDialog) {
        AddAddressDialog(
            onDismiss = { showAddAddressDialog = false },
            onAdd = { address ->
                deliveryAddresses.add(address)
                showAddAddressDialog = false
            }
        )
    }
}

@Composable
fun DeliveryAddressCard(
    address: DeliveryAddress,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSetDefault: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (address.isDefault) Color(0xFFE3F2FD) else Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF0B8FAC),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            when (address.label) {
                                "Home" -> Icons.Default.Home
                                "Work" -> Icons.Default.BusinessCenter
                                else -> Icons.Default.LocationOn
                            },
                            contentDescription = address.label,
                            tint = Color.White,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = address.label,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (address.isDefault) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF4CAF50)
                                ) {
                                    Text(
                                        "Default",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, "Menu")
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        if (!address.isDefault) {
                            DropdownMenuItem(
                                text = { Text("Set as Default") },
                                onClick = {
                                    showMenu = false
                                    onSetDefault()
                                },
                                leadingIcon = { Icon(Icons.Default.Star, "Default") }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                showMenu = false
                                onEdit()
                            },
                            leadingIcon = { Icon(Icons.Default.Edit, "Edit") }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                showMenu = false
                                onDelete()
                            },
                            leadingIcon = { Icon(Icons.Default.Delete, "Delete", tint = Color.Red) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    Icons.Outlined.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = address.fullAddress,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = address.landmark,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.Phone,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = address.phoneNumber,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun NearbyPlaceCard(place: NearbyPlace) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = when (place.type) {
                            PlaceType.HOSPITAL -> Color(0xFFE3F2FD)
                            PlaceType.CLINIC -> Color(0xFFF3E5F5)
                            PlaceType.PHARMACY -> Color(0xFFE8F5E9)
                            PlaceType.EMERGENCY -> Color(0xFFFFEBEE)
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            when (place.type) {
                                PlaceType.HOSPITAL -> Icons.Default.LocalHospital
                                PlaceType.CLINIC -> Icons.Default.MedicalServices
                                PlaceType.PHARMACY -> Icons.Default.Medication
                                PlaceType.EMERGENCY -> Icons.Default.LocalHospital
                            },
                            contentDescription = null,
                            tint = when (place.type) {
                                PlaceType.HOSPITAL -> Color(0xFF2196F3)
                                PlaceType.CLINIC -> Color(0xFF9C27B0)
                                PlaceType.PHARMACY -> Color(0xFF4CAF50)
                                PlaceType.EMERGENCY -> Color(0xFFD32F2F)
                            },
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = place.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFFFFC107)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = place.rating.toString(),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "• ${place.distance}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }

                if (place.isOpen24Hours) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF4CAF50)
                    ) {
                        Text(
                            "24/7",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = place.address,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            if (place.specialization != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Specialization: ${place.specialization}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF0B8FAC)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${place.phoneNumber}")
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF0B8FAC)
                    )
                ) {
                    Icon(Icons.Default.Phone, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Call")
                }

                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("geo:${place.latitude},${place.longitude}?q=${place.latitude},${place.longitude}(${place.name})")
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0B8FAC)
                    )
                ) {
                    Icon(Icons.Default.NearMe, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Directions")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAddressDialog(
    onDismiss: () -> Unit,
    onAdd: (DeliveryAddress) -> Unit
) {
    var label by remember { mutableStateOf("Home") }
    var fullAddress by remember { mutableStateOf("") }
    var landmark by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var isDefault by remember { mutableStateOf(false) }

    val labels = listOf("Home", "Work", "Other")
    var expandedLabel by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "Add Delivery Address",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                ExposedDropdownMenuBox(
                    expanded = expandedLabel,
                    onExpandedChange = { expandedLabel = it }
                ) {
                    OutlinedTextField(
                        value = label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Label") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLabel) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedLabel,
                        onDismissRequest = { expandedLabel = false }
                    ) {
                        labels.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    label = item
                                    expandedLabel = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = fullAddress,
                    onValueChange = { fullAddress = it },
                    label = { Text("Full Address") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = landmark,
                    onValueChange = { landmark = it },
                    label = { Text("Landmark") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Set as default address")
                    Switch(
                        checked = isDefault,
                        onCheckedChange = { isDefault = it }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (fullAddress.isNotBlank() && phoneNumber.isNotBlank()) {
                                val address = DeliveryAddress(
                                    id = UUID.randomUUID().toString(),
                                    label = label,
                                    fullAddress = fullAddress,
                                    landmark = landmark,
                                    phoneNumber = phoneNumber,
                                    isDefault = isDefault
                                )
                                onAdd(address)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0B8FAC)
                        )
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}