package com.mediqor.app.ui.model

data class UserModel(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val role: String = "user"
)