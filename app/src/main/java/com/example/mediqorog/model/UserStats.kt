package com.example.mediqorog.model

// User statistics data class for Firestore
data class UserStats(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalMealsLogged: Int = 0,
    val totalDaysTracked: Int = 0,
    val lastLoggedDate: String = "",
    val userId: String = ""
)