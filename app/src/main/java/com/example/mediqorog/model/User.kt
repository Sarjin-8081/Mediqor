package com.example.mediqorog.model

data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val phoneNumber: String = "",
    val photoUrl: String = "",
    val role: String = "customer", // "admin" or "customer"
    val createdAt: Long = System.currentTimeMillis()
) {
    fun isAdmin(): Boolean = role == "admin"
}