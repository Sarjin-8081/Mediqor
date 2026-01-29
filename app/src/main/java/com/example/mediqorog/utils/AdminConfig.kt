package com.example.mediqorog.utils

object AdminConfig {
    // Set of admin emails
    private val ADMIN_EMAILS = setOf(
        "mediqor.44@gmail.com",
        "samarthshah@mediqor.com",
        "sarjin.shrestha.81@gmail.com",
        "vendu123@gmail.com"
    )

    fun isAdmin(email: String): Boolean {
        return ADMIN_EMAILS.contains(email.lowercase().trim())
    }
}