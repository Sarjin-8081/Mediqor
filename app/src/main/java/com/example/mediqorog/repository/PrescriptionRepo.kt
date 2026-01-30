package com.example.mediqorog.repository

import android.util.Log
import com.example.mediqorog.model.PrescriptionData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.*

class PrescriptionRepo {
    private val firestore = FirebaseFirestore.getInstance()
    private val prescriptionsCollection = firestore.collection("prescriptions")

    companion object {
        private const val TAG = "PrescriptionRepo"
    }

    suspend fun addPrescription(prescription: PrescriptionData): Result<String> {
        return try {
            val prescriptionId = UUID.randomUUID().toString()
            val prescriptionWithId = prescription.copy(id = prescriptionId)

            Log.d(TAG, "Adding prescription: $prescriptionId")

            prescriptionsCollection
                .document(prescriptionId)
                .set(prescriptionWithId.toMap())
                .await()

            Log.d(TAG, "Prescription added successfully")
            Result.success(prescriptionId)
        } catch (e: Exception) {
            Log.e(TAG, "Error adding prescription", e)
            Result.failure(e)
        }
    }

    suspend fun getUserPrescriptions(userId: String): Result<List<PrescriptionData>> {
        return try {
            Log.d(TAG, "Fetching prescriptions for user: $userId")

            val snapshot = prescriptionsCollection
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val prescriptions = snapshot.documents.mapNotNull { doc ->
                try {
                    val data = doc.data ?: return@mapNotNull null
                    PrescriptionData(
                        id = data["id"] as? String ?: "",
                        userId = data["userId"] as? String ?: "",
                        medicineName = data["medicineName"] as? String ?: "",
                        dosage = data["dosage"] as? String ?: "",
                        frequency = data["frequency"] as? String ?: "",
                        times = (data["times"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                        startDate = (data["startDate"] as? Long) ?: 0,
                        endDate = (data["endDate"] as? Long) ?: 0,
                        doctorName = data["doctorName"] as? String ?: "",
                        beforeMeal = data["beforeMeal"] as? Boolean ?: false,
                        quantityRemaining = (data["quantityRemaining"] as? Long)?.toInt() ?: 0,
                        totalQuantity = (data["totalQuantity"] as? Long)?.toInt() ?: 0,
                        isActive = data["isActive"] as? Boolean ?: true,
                        createdAt = (data["createdAt"] as? Long) ?: 0
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing prescription: ${doc.id}", e)
                    null
                }
            }

            Log.d(TAG, "Retrieved ${prescriptions.size} prescriptions")
            Result.success(prescriptions)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching prescriptions", e)
            Result.failure(e)
        }
    }

    suspend fun deletePrescription(prescriptionId: String, userId: String): Result<Unit> {
        return try {
            Log.d(TAG, "Deleting prescription: $prescriptionId")

            val doc = prescriptionsCollection.document(prescriptionId).get().await()

            if (!doc.exists()) {
                return Result.failure(Exception("Prescription not found"))
            }

            val docUserId = doc.getString("userId")
            if (docUserId != userId) {
                return Result.failure(SecurityException("Unauthorized"))
            }

            prescriptionsCollection.document(prescriptionId).delete().await()

            Log.d(TAG, "Prescription deleted")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting prescription", e)
            Result.failure(e)
        }
    }

    suspend fun updateQuantity(prescriptionId: String, userId: String, newQuantity: Int): Result<Unit> {
        return try {
            val doc = prescriptionsCollection.document(prescriptionId).get().await()

            if (!doc.exists()) {
                return Result.failure(Exception("Prescription not found"))
            }

            val docUserId = doc.getString("userId")
            if (docUserId != userId) {
                return Result.failure(SecurityException("Unauthorized"))
            }

            prescriptionsCollection
                .document(prescriptionId)
                .update("quantityRemaining", newQuantity)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating quantity", e)
            Result.failure(e)
        }
    }
}