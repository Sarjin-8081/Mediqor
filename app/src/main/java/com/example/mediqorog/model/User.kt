package com.example.mediqorog.model

data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val phoneNumber: String = "",
    val photoUrl: String = "",
    val role: String = "customer", // "admin" or "customer"
    val createdAt: Long = System.currentTimeMillis(),

    // New fields for enhanced user management
    val accountStatus: String = "active", // "active", "suspended", "pending"
    val isEmailVerified: Boolean = false,
    val isPhoneVerified: Boolean = false,
    val address: String = "",
    val bloodGroup: String = "",
    val gender: String = "",
    val dateOfBirth: String = "",
    val emergencyContact: String = "",
    val totalOrders: Int = 0,
    val totalSpent: Double = 0.0,
    val lastLoginAt: Long = System.currentTimeMillis()
) {
    fun isAdmin(): Boolean = role == "admin"
}