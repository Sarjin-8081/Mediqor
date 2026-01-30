package com.example.mediqorog.model

data class User(
    // ============================================
    // BASIC INFORMATION (8 fields) ← Updated count
    // ============================================
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val phoneNumber: String = "",
    val photoUrl: String = "",
    val photoPublicId: String = "",  // ✅ ADD THIS - Cloudinary image public ID
    val role: String = "customer", // "admin" or "customer"
    val createdAt: Long = System.currentTimeMillis(),

    // ============================================
    // MEDICAL INFORMATION (5 fields)
    // ============================================
    val bloodGroup: String = "",        // A+, A-, B+, B-, AB+, AB-, O+, O-
    val dateOfBirth: String = "",       // Format: "YYYY-MM-DD"
    val gender: String = "",            // Male, Female, Other
    val address: String = "",           // Full address
    val emergencyContact: String = "",  // Emergency contact number

    // ============================================
    // ACCOUNT STATUS (4 fields)
    // ============================================
    val isEmailVerified: Boolean = false,  // Can verify later in settings
    val isPhoneVerified: Boolean = false,  // Can verify later in settings
    val accountStatus: String = "active",  // "active", "suspended", "pending"
    val lastLoginAt: Long = System.currentTimeMillis(), // Updates on every login

    // ============================================
    // STATISTICS (2 fields)
    // ============================================
    val totalOrders: Int = 0,
    val totalSpent: Double = 0.0,

    // ============================================
    // ADDITIONAL (1 field)
    // ============================================
    val deviceToken: String = "" // Multiple accounts can share same token - totally fine!
) {
    fun isAdmin(): Boolean = role == "admin"
}