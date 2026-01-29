package com.example.mediqorog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediqorog.view.Address
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AddressViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _addresses = MutableStateFlow<List<Address>>(emptyList())
    val addresses: StateFlow<List<Address>> = _addresses

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _currentAddress = MutableStateFlow<Address?>(null)
    val currentAddress: StateFlow<Address?> = _currentAddress

    init {
        loadAddresses()
    }

    fun loadAddresses() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    firestore.collection("users")
                        .document(userId)
                        .collection("addresses")
                        .orderBy("createdAt", Query.Direction.DESCENDING)
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                _loading.value = false
                                return@addSnapshotListener
                            }

                            _addresses.value = snapshot?.documents?.mapNotNull { doc ->
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
                                    null
                                }
                            } ?: emptyList()
                            _loading.value = false
                        }
                } else {
                    _loading.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _loading.value = false
            }
        }
    }

    fun loadAddress(addressId: String) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val doc = firestore.collection("users")
                        .document(userId)
                        .collection("addresses")
                        .document(addressId)
                        .get()
                        .await()

                    if (doc.exists()) {
                        _currentAddress.value = Address(
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
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun saveAddress(address: Address): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: return false
            val addressId = if (address.id.isEmpty()) {
                firestore.collection("users")
                    .document(userId)
                    .collection("addresses")
                    .document()
                    .id
            } else {
                address.id
            }

            // If setting as default, unset other defaults
            if (address.isDefault) {
                val batch = firestore.batch()
                val docs = firestore.collection("users")
                    .document(userId)
                    .collection("addresses")
                    .get()
                    .await()

                docs.documents.forEach { doc ->
                    if (doc.id != addressId) {
                        batch.update(doc.reference, "isDefault", false)
                    }
                }
                batch.commit().await()
            }

            // Save the address
            val addressData = hashMapOf(
                "id" to addressId,
                "name" to address.name,
                "phone" to address.phone,
                "addressLine" to address.addressLine,
                "landmark" to address.landmark,
                "city" to address.city,
                "state" to address.state,
                "pincode" to address.pincode,
                "latitude" to address.latitude,
                "longitude" to address.longitude,
                "isDefault" to address.isDefault,
                "createdAt" to (if (address.createdAt == 0L) System.currentTimeMillis() else address.createdAt)
            )

            firestore.collection("users")
                .document(userId)
                .collection("addresses")
                .document(addressId)
                .set(addressData)
                .await()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteAddress(addressId: String): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: return false

            firestore.collection("users")
                .document(userId)
                .collection("addresses")
                .document(addressId)
                .delete()
                .await()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun setAsDefault(addressId: String): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: return false

            val batch = firestore.batch()
            val docs = firestore.collection("users")
                .document(userId)
                .collection("addresses")
                .get()
                .await()

            docs.documents.forEach { doc ->
                batch.update(doc.reference, "isDefault", doc.id == addressId)
            }
            batch.commit().await()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun clearCurrentAddress() {
        _currentAddress.value = null
    }
}