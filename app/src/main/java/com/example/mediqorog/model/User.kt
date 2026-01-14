package com.example.mediqorog.model

data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)