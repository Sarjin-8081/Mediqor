package com.example.mediqorog.repository

import android.util.Log
import com.example.mediqorog.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AdminUserRepositoryImpl : AdminUserRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    override suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val snapshot = usersCollection.get().await()
            val users = snapshot.documents.mapNotNull { doc ->
                doc.toObject(User::class.java)?.copy(uid = doc.id)
            }
            Result.success(users)
        } catch (e: Exception) {
            Log.e("AdminUserRepo", "Error getting all users", e)
            Result.failure(e)
        }
    }

    override suspend fun getUserById(userId: String): Result<User?> {
        return try {
            val doc = usersCollection.document(userId).get().await()
            val user = doc.toObject(User::class.java)?.copy(uid = doc.id)
            Result.success(user)
        } catch (e: Exception) {
            Log.e("AdminUserRepo", "Error getting user by id", e)
            Result.failure(e)
        }
    }

    override suspend fun updateUser(user: User): Result<Unit> {
        return try {
            usersCollection.document(user.uid).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AdminUserRepo", "Error updating user", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            usersCollection.document(userId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AdminUserRepo", "Error deleting user", e)
            Result.failure(e)
        }
    }

    override suspend fun getUsersByRole(role: String): Result<List<User>> {
        return try {
            val snapshot = usersCollection
                .whereEqualTo("role", role)
                .get()
                .await()
            val users = snapshot.documents.mapNotNull { doc ->
                doc.toObject(User::class.java)?.copy(uid = doc.id)
            }
            Result.success(users)
        } catch (e: Exception) {
            Log.e("AdminUserRepo", "Error getting users by role", e)
            Result.failure(e)
        }
    }
}