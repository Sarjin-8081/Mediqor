package com.example.mediqorog.repository

import com.example.mediqorog.model.User
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

interface UserRepository {
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signUp(email: String, password: String, displayName: String): Result<User>
    suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<User>
    suspend fun signOut(): Result<Unit>
    suspend fun getCurrentUser(): User?
    suspend fun resetPassword(email: String): Result<Unit>
}
