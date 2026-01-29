package com.example.mediqorog.repository

import com.example.mediqorog.model.User

interface AdminUserRepository {
    suspend fun getAllUsers(): Result<List<User>>
    suspend fun getUserById(userId: String): Result<User?>
    suspend fun deleteUser(userId: String): Result<Unit>
    suspend fun updateUser(user: User): Result<Unit>
    suspend fun getUsersByRole(role: String): Result<List<User>>
}