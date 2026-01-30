package com.example.mediqorog.model

import java.util.*

data class PrescriptionData(
    val id: String = "",
    val userId: String = "",
    val medicineName: String = "",
    val dosage: String = "",
    val frequency: String = "",
    val times: List<String> = emptyList(),
    val startDate: Long = 0,
    val endDate: Long = 0,
    val doctorName: String = "",
    val beforeMeal: Boolean = false,
    val quantityRemaining: Int = 0,
    val totalQuantity: Int = 0,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    // Empty constructor for Firebase
    constructor() : this(
        id = "",
        userId = "",
        medicineName = "",
        dosage = "",
        frequency = "",
        times = emptyList(),
        startDate = 0,
        endDate = 0,
        doctorName = "",
        beforeMeal = false,
        quantityRemaining = 0,
        totalQuantity = 0,
        isActive = true,
        createdAt = System.currentTimeMillis()
    )

    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "userId" to userId,
            "medicineName" to medicineName,
            "dosage" to dosage,
            "frequency" to frequency,
            "times" to times,
            "startDate" to startDate,
            "endDate" to endDate,
            "doctorName" to doctorName,
            "beforeMeal" to beforeMeal,
            "quantityRemaining" to quantityRemaining,
            "totalQuantity" to totalQuantity,
            "isActive" to isActive,
            "createdAt" to createdAt
        )
    }
}