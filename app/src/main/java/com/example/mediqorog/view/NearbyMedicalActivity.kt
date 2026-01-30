package com.example.mediqorog.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
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

class NearbyMedicalActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                NearbyMedicalScreen(onNavigateBack = { finish() })
            }
        }
    }
}

data class MedicalPlace(
    val name: String,
    val type: String, // Hospital, Clinic, Pharmacy
    val address: String,
    val phone: String,
    val latitude: Double,
    val longitude: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyMedicalScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    var selectedCity by remember { mutableStateOf<String?>(null) }
    var selectedType by remember { mutableStateOf<String?>(null) }

    val cities = listOf("Kathmandu", "Bhaktapur", "Lalitpur")
    val types = listOf("Hospital", "Clinic", "Pharmacy")

    // Medical places data for each city
    val medicalPlaces = mapOf(
        "Kathmandu" to mapOf(
            "Hospital" to listOf(
                MedicalPlace("Bir Hospital", "Hospital", "Tundikhel, Kathmandu", "01-4221119", 27.7025, 85.3143),
                MedicalPlace("Teaching Hospital (TU)", "Hospital", "Maharajgunj, Kathmandu", "01-4412303", 27.7359, 85.3347),
                MedicalPlace("Grande International Hospital", "Hospital", "Dhapasi, Kathmandu", "01-5159266", 27.7359, 85.3226),
                MedicalPlace("Norvic International Hospital", "Hospital", "Thapathali, Kathmandu", "01-4258554", 27.6935, 85.3206)
            ),
            "Clinic" to listOf(
                MedicalPlace("New Road Clinic", "Clinic", "New Road, Kathmandu", "01-4228881", 27.7025, 85.3122),
                MedicalPlace("Durbar Marg Clinic", "Clinic", "Durbar Marg, Kathmandu", "01-4227766", 27.7104, 85.3176),
                MedicalPlace("Thamel Clinic", "Clinic", "Thamel, Kathmandu", "01-4701122", 27.7144, 85.3106)
            ),
            "Pharmacy" to listOf(
                MedicalPlace("Apollo Pharmacy", "Pharmacy", "New Baneshwor, Kathmandu", "01-4782266", 27.6936, 85.3389),
                MedicalPlace("MediCity Pharmacy", "Pharmacy", "Putalisadak, Kathmandu", "01-4442200", 27.7007, 85.3197),
                MedicalPlace("Sasto Pharmacy", "Pharmacy", "Koteshwor, Kathmandu", "01-4600800", 27.6769, 85.3478)
            )
        ),
        "Bhaktapur" to mapOf(
            "Hospital" to listOf(
                MedicalPlace("Bhaktapur Cancer Hospital", "Hospital", "Bhaktapur", "01-6611998", 27.6710, 85.4298),
                MedicalPlace("Madhyapur Hospital", "Hospital", "Madhyapur Thimi, Bhaktapur", "01-6632533", 27.6791, 85.3849),
                MedicalPlace("Siddhi Memorial Hospital", "Hospital", "Bhaktapur", "01-6610799", 27.6710, 85.4282)
            ),
            "Clinic" to listOf(
                MedicalPlace("Durbar Square Clinic", "Clinic", "Durbar Square, Bhaktapur", "01-6614477", 27.6721, 85.4298),
                MedicalPlace("Suryabinayak Clinic", "Clinic", "Suryabinayak, Bhaktapur", "01-6634455", 27.6612, 85.4471)
            ),
            "Pharmacy" to listOf(
                MedicalPlace("Bhaktapur Medical Pharmacy", "Pharmacy", "Bhaktapur", "01-6613344", 27.6715, 85.4286),
                MedicalPlace("City Pharmacy", "Pharmacy", "Thimi, Bhaktapur", "01-6632244", 27.6791, 85.3855)
            )
        ),
        "Lalitpur" to mapOf(
            "Hospital" to listOf(
                MedicalPlace("Patan Hospital", "Hospital", "Lagankhel, Lalitpur", "01-5522266", 27.6722, 85.3236),
                MedicalPlace("Alka Hospital", "Hospital", "Jawalakhel, Lalitpur", "01-5522295", 27.6753, 85.3181),
                MedicalPlace("Om Hospital and Research Center", "Hospital", "Chabahil, Lalitpur", "01-4478968", 27.7254, 85.3508),
                MedicalPlace("Nepal Mediciti Hospital", "Hospital", "Sainbu, Lalitpur", "01-4217766", 27.6465, 85.3675)
            ),
            "Clinic" to listOf(
                MedicalPlace("Jhamsikhel Clinic", "Clinic", "Jhamsikhel, Lalitpur", "01-5521100", 27.6825, 85.3091),
                MedicalPlace("Pulchowk Clinic", "Clinic", "Pulchowk, Lalitpur", "01-5520990", 27.6794, 85.3174),
                MedicalPlace("Kupondole Clinic", "Clinic", "Kupondole, Lalitpur", "01-5527799", 27.6894, 85.3123)
            ),
            "Pharmacy" to listOf(
                MedicalPlace("Patan Pharmacy", "Pharmacy", "Patan Dhoka, Lalitpur", "01-5527766", 27.6735, 85.3247),
                MedicalPlace("Jawalakhel Pharmacy", "Pharmacy", "Jawalakhel, Lalitpur", "01-5527744", 27.6759, 85.3178),
                MedicalPlace("Sanepa Pharmacy", "Pharmacy", "Sanepa, Lalitpur", "01-5523399", 27.6833, 85.3066)
            )
        )
    )

    val currentPlaces = if (selectedCity != null && selectedType != null) {
        medicalPlaces[selectedCity]?.get(selectedType) ?: emptyList()
    } else {
        emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nearby Medical Services", fontWeight = FontWeight.SemiBold) },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F7FA)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // City Selection
            if (selectedCity == null) {
                item {
                    Text(
                        text = "Select Your City",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                }

                items(cities) { city ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedCity = city },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocationCity,
                                contentDescription = null,
                                tint = Color(0xFF0B8FAC),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = city,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1F2937)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Color(0xFF6B7280)
                            )
                        }
                    }
                }
            }
            // Type Selection
            else if (selectedType == null) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { selectedCity = null }) {
                            Icon(Icons.Default.ArrowBack, "Back")
                        }
                        Text(
                            text = "Select Service Type in $selectedCity",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                    }
                }

                items(types) { type ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedType = type },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                when (type) {
                                    "Hospital" -> Icons.Default.LocalHospital
                                    "Clinic" -> Icons.Default.MedicalServices
                                    else -> Icons.Default.LocalPharmacy
                                },
                                contentDescription = null,
                                tint = when (type) {
                                    "Hospital" -> Color(0xFFEF4444)
                                    "Clinic" -> Color(0xFF3B82F6)
                                    else -> Color(0xFF10B981)
                                },
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = type,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1F2937)
                                )
                                Text(
                                    text = "${medicalPlaces[selectedCity]?.get(type)?.size ?: 0} nearby",
                                    fontSize = 13.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Color(0xFF6B7280)
                            )
                        }
                    }
                }
            }
            // Places List
            else {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { selectedType = null }) {
                            Icon(Icons.Default.ArrowBack, "Back")
                        }
                        Column {
                            Text(
                                text = "$selectedType in $selectedCity",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937)
                            )
                            Text(
                                text = "${currentPlaces.size} results",
                                fontSize = 13.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }
                }

                items(currentPlaces) { place ->
                    MedicalPlaceCard(place = place)
                }
            }
        }
    }
}

@Composable
fun MedicalPlaceCard(place: MedicalPlace) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Name and Type
            Text(
                text = place.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = when (place.type) {
                    "Hospital" -> Color(0xFFFEE2E2)
                    "Clinic" -> Color(0xFFDBEAFE)
                    else -> Color(0xFFD1FAE5)
                }
            ) {
                Text(
                    text = place.type,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = when (place.type) {
                        "Hospital" -> Color(0xFFDC2626)
                        "Clinic" -> Color(0xFF2563EB)
                        else -> Color(0xFF059669)
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Address
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFF6B7280),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = place.address,
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Phone
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Phone,
                    contentDescription = null,
                    tint = Color(0xFF6B7280),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = place.phone,
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${place.phone}")
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF0B8FAC)
                    )
                ) {
                    Icon(Icons.Default.Phone, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Call")
                }

                Button(
                    onClick = {
                        val gmmIntentUri = Uri.parse("geo:${place.latitude},${place.longitude}?q=${place.latitude},${place.longitude}(${place.name})")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        context.startActivity(mapIntent)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0B8FAC)
                    )
                ) {
                    Icon(Icons.Default.Directions, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Directions")
                }
            }
        }
    }
}