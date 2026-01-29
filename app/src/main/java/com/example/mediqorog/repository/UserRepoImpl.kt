package com.example.mediqorog.repository

import android.util.Log
import com.example.mediqorog.model.User
import com.example.mediqorog.utils.AdminConfig
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepoImpl : UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    private suspend fun getDeviceToken(): String {
        return ""
    }

    override suspend fun signUp(email: String, password: String, displayName: String): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user

            if (firebaseUser != null) {
                val role = if (AdminConfig.isAdmin(email)) "admin" else "customer"
                val currentTime = System.currentTimeMillis()

                val user = User(
                    uid = firebaseUser.uid,
                    email = email,
                    displayName = displayName,
                    phoneNumber = "",
                    photoUrl = "",
                    role = role,
                    createdAt = currentTime,
                    bloodGroup = "",
                    dateOfBirth = "",
                    gender = "",
                    address = "",
                    emergencyContact = "",
                    isEmailVerified = false,
                    isPhoneVerified = false,
                    accountStatus = "active",
                    lastLoginAt = currentTime,
                    totalOrders = 0,
                    totalSpent = 0.0,
                    deviceToken = ""
                )

                usersCollection.document(user.uid).set(user).await()
                Log.d("UserRepo", "User created successfully with all 18 fields")
                Result.success(user)
            } else {
                Result.failure(Exception("Failed to create user"))
            }
        } catch (e: Exception) {
            try {
                auth.currentUser?.delete()?.await()
            } catch (deleteException: Exception) {
                Log.e("UserRepo", "Failed to cleanup user: ${deleteException.message}")
            }
            Log.e("UserRepo", "SignUp failed: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user

            if (firebaseUser != null) {
                val doc = usersCollection.document(firebaseUser.uid).get().await()

                if (doc.exists()) {
                    val user = doc.toObject(User::class.java)

                    if (user != null) {
                        val updates = hashMapOf<String, Any>(
                            "lastLoginAt" to System.currentTimeMillis()
                        )

                        usersCollection.document(firebaseUser.uid).update(updates).await()

                        val updatedUser = user.copy(
                            lastLoginAt = System.currentTimeMillis()
                        )

                        Log.d("UserRepo", "Sign in successful, lastLoginAt updated")
                        Result.success(updatedUser)
                    } else {
                        Result.failure(Exception("User data not found"))
                    }
                } else {
                    Result.failure(Exception("User document does not exist"))
                }
            } else {
                Result.failure(Exception("Firebase user is null"))
            }
        } catch (e: Exception) {
            Log.e("UserRepo", "SignIn failed: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val firebaseUser = result.user

            if (firebaseUser != null) {
                val userDoc = usersCollection.document(firebaseUser.uid).get().await()
                val currentTime = System.currentTimeMillis()

                if (userDoc.exists()) {
                    val user = userDoc.toObject(User::class.java)

                    if (user != null) {
                        val updates = hashMapOf<String, Any>(
                            "lastLoginAt" to currentTime
                        )

                        usersCollection.document(firebaseUser.uid).update(updates).await()

                        val updatedUser = user.copy(
                            lastLoginAt = currentTime
                        )

                        Log.d("UserRepo", "Google sign in - existing user, lastLoginAt updated")
                        Result.success(updatedUser)
                    } else {
                        Result.failure(Exception("Failed to parse user data"))
                    }
                } else {
                    val role = if (AdminConfig.isAdmin(firebaseUser.email ?: "")) "admin" else "customer"

                    val newUser = User(
                        uid = firebaseUser.uid,
                        email = firebaseUser.email ?: "",
                        displayName = firebaseUser.displayName ?: "",
                        phoneNumber = firebaseUser.phoneNumber ?: "",
                        photoUrl = firebaseUser.photoUrl?.toString() ?: "",
                        role = role,
                        createdAt = currentTime,
                        bloodGroup = "",
                        dateOfBirth = "",
                        gender = "",
                        address = "",
                        emergencyContact = "",
                        isEmailVerified = firebaseUser.isEmailVerified,
                        isPhoneVerified = false,
                        accountStatus = "active",
                        lastLoginAt = currentTime,
                        totalOrders = 0,
                        totalSpent = 0.0,
                        deviceToken = ""
                    )

                    usersCollection.document(newUser.uid).set(newUser).await()

                    Log.d("UserRepo", "Google sign in - new user created with all 18 fields")
                    Result.success(newUser)
                }
            } else {
                Result.failure(Exception("Firebase user is null"))
            }
        } catch (e: Exception) {
            Log.e("UserRepo", "Google sign in failed: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): User? {
        return try {
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                val doc = usersCollection.document(firebaseUser.uid).get().await()
                doc.toObject(User::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("UserRepo", "Get current user failed: ${e.message}")
            null
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkIfAdmin(userId: String): Result<Boolean> {
        return try {
            val doc = usersCollection.document(userId).get().await()
            val role = doc.getString("role") ?: "customer"
            Result.success(role == "admin")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateAllUsersWithRole(): Result<String> {
        return try {
            val querySnapshot = usersCollection.get().await()
            var updatedCount = 0

            for (document in querySnapshot.documents) {
                val email = document.getString("email") ?: ""
                val currentRole = document.getString("role") ?: "customer"
                val correctRole = if (AdminConfig.isAdmin(email)) "admin" else "customer"

                val updates = mutableMapOf<String, Any>()

                if (currentRole != correctRole) {
                    updates["role"] = correctRole
                }

                if (!document.contains("bloodGroup")) updates["bloodGroup"] = ""
                if (!document.contains("dateOfBirth")) updates["dateOfBirth"] = ""
                if (!document.contains("gender")) updates["gender"] = ""
                if (!document.contains("address")) updates["address"] = ""
                if (!document.contains("emergencyContact")) updates["emergencyContact"] = ""
                if (!document.contains("isEmailVerified")) updates["isEmailVerified"] = false
                if (!document.contains("isPhoneVerified")) updates["isPhoneVerified"] = false
                if (!document.contains("accountStatus")) updates["accountStatus"] = "active"
                if (!document.contains("lastLoginAt")) updates["lastLoginAt"] = document.getLong("createdAt") ?: System.currentTimeMillis()
                if (!document.contains("totalOrders")) updates["totalOrders"] = 0
                if (!document.contains("totalSpent")) updates["totalSpent"] = 0.0
                if (!document.contains("deviceToken")) updates["deviceToken"] = ""

                if (updates.isNotEmpty()) {
                    document.reference.update(updates).await()
                    updatedCount++
                }
            }

            Result.success("Updated $updatedCount users with roles and missing fields")
        } catch (e: Exception) {
            Log.e("UserRepo", "Update users failed: ${e.message}")
            Result.failure(e)
        }
    }
}