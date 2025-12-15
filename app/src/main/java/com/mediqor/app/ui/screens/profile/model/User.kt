package com.mediqor.app.ui.screens.profile.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val ePoints: Int = 0
)
