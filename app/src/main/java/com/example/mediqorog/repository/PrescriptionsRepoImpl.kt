package com.example.mediqorog.repository

import android.net.Uri
import com.example.mediqorog.model.MedicineReminder
import com.example.mediqorog.model.Prescription
import com.example.mediqorog.model.PrescriptionStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.*

interface PrescriptionRepository {
    suspend fun uploadPrescription(
        userId: String,
        patientName: String,
        imageUri: Uri,
        notes: String
    ): Result<String>

    suspend fun getUserPrescriptions(userId: String): Result<List<Prescription>>
    suspend fun deletePrescription(prescriptionId: String): Result<Unit>
    suspend fun updatePrescriptionStatus(
        prescriptionId: String,
        status: PrescriptionStatus,
        reason: String? = null
    ): Result<Unit>
    suspend fun updatePrescriptionReminders(
        prescriptionId: String,
        reminders: List<MedicineReminder>
    ): Result<Unit>
}

class PrescriptionRepositoryImpl : PrescriptionRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val prescriptionsCollection = firestore.collection("prescriptions")
    private val storageRef = storage.reference.child("prescriptions")

    override suspend fun uploadPrescription(
        userId: String,
        patientName: String,
        imageUri: Uri,
        notes: String
    ): Result<String> {
        return try {
            val prescriptionId = prescriptionsCollection.document().id
            val imageRef = storageRef.child("$userId/$prescriptionId.jpg")
            imageRef.putFile(imageUri).await()
            val imageUrl = imageRef.downloadUrl.await().toString()

            val prescription = Prescription(
                id = prescriptionId,
                userId = userId,
                patientName = patientName,
                imageUrl = imageUrl,
                notes = notes,
                uploadDate = Date(),
                status = PrescriptionStatus.PENDING,
                reminders = emptyList()
            )

            prescriptionsCollection.document(prescriptionId).set(prescription).await()
            Result.success(prescriptionId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserPrescriptions(userId: String): Result<List<Prescription>> {
        return try {
            val snapshot = prescriptionsCollection
                .whereEqualTo("userId", userId)
                .orderBy("uploadDate", Query.Direction.DESCENDING)
                .get()
                .await()

            val prescriptions = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Prescription::class.java)
            }

            Result.success(prescriptions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deletePrescription(prescriptionId: String): Result<Unit> {
        return try {
            val doc = prescriptionsCollection.document(prescriptionId).get().await()
            val prescription = doc.toObject(Prescription::class.java)

            prescription?.imageUrl?.let { url ->
                try {
                    val imageRef = storage.getReferenceFromUrl(url)
                    imageRef.delete().await()
                } catch (e: Exception) {
                    // Continue
                }
            }

            prescriptionsCollection.document(prescriptionId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePrescriptionStatus(
        prescriptionId: String,
        status: PrescriptionStatus,
        reason: String?
    ): Result<Unit> {
        return try {
            val updates = mutableMapOf<String, Any>(
                "status" to status,
                "verifiedDate" to Date()
            )

            if (status == PrescriptionStatus.REJECTED && reason != null) {
                updates["rejectionReason"] = reason
            }

            prescriptionsCollection.document(prescriptionId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePrescriptionReminders(
        prescriptionId: String,
        reminders: List<MedicineReminder>
    ): Result<Unit> {
        return try {
            prescriptionsCollection.document(prescriptionId)
                .update("reminders", reminders)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}