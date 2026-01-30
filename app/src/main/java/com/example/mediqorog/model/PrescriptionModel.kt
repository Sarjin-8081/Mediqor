package com.example.mediqorog.model

import com.google.firebase.firestore.DocumentId
import java.util.*

/**
 * Prescription Status
 */
enum class PrescriptionStatus {
    PENDING,
    VERIFIED,
    REJECTED;

    fun toDisplayString(): String {
        return when (this) {
            PENDING -> "Pending"
            VERIFIED -> "Verified"
            REJECTED -> "Rejected"
        }
    }
}

/**
 * Medicine Reminder
 */
data class MedicineReminder(
    val medicineName: String = "",
    val dosage: String = "",
    val frequency: String = "", // e.g., "Daily", "Twice a day", "Weekly"
    val times: List<String> = emptyList(), // e.g., ["08:00", "20:00"]
    val startDate: Date = Date(),
    val endDate: Date = Date(),
    val enabled: Boolean = true
) {
    constructor() : this("", "", "", emptyList(), Date(), Date(), true)
}

/**
 * Prescription Model with Firebase support
 */
data class Prescription(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val patientName: String = "",
    val imageUrl: String = "",
    val notes: String = "",
    val uploadDate: Date = Date(),
    val status: PrescriptionStatus = PrescriptionStatus.PENDING,
    val verifiedBy: String? = null,
    val verifiedDate: Date? = null,
    val rejectionReason: String? = null,
    val reminders: List<MedicineReminder> = emptyList()
) {
    // No-arg constructor for Firebase
    constructor() : this("", "", "", "", "", Date(), PrescriptionStatus.PENDING, null, null, null, emptyList())
}