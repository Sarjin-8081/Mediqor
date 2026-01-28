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

    override suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            Log.d("UserRepoImpl", "Attempting sign in for: $email")
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: return Result.failure(Exception("User not found"))

            // ✅ Determine role based on AdminConfig
            val role = if (AdminConfig.isAdmin(firebaseUser.email ?: "")) "admin" else "customer"

            val userDoc = usersCollection.document(firebaseUser.uid).get().await()
            val user = if (userDoc.exists()) {
                // ✅ Update role if it's different
                val existingUser = userDoc.toObject(User::class.java)?.copy(
                    uid = firebaseUser.uid,
                    role = role
                )
                // Update Firestore if role changed or doesn't exist
                val currentRole = userDoc.getString("role")
                if (currentRole != role) {
                    usersCollection.document(firebaseUser.uid).update("role", role).await()
                }
                existingUser?.copy(role = role)
            } else {
                val newUser = User(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    displayName = firebaseUser.displayName ?: "",
                    role = role
                )
                usersCollection.document(firebaseUser.uid).set(newUser).await()
                newUser
            }

            Log.d("UserRepoImpl", "Sign in successful: ${user?.email}, role: ${user?.role}")
            user?.let { Result.success(it) } ?: Result.failure(Exception("Failed to load user"))
        } catch (e: Exception) {
            Log.e("UserRepoImpl", "Sign in failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun signUp(
        email: String,
        password: String,
        displayName: String
    ): Result<User> {
        return try {
            Log.d("UserRepoImpl", "Attempting sign up for: $email")
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: return Result.failure(Exception("Failed to create user"))

            Log.d("UserRepoImpl", "Firebase user created, UID: ${firebaseUser.uid}")

            // ✅ Determine role based on AdminConfig
            val role = if (AdminConfig.isAdmin(email)) "admin" else "customer"

            val user = User(
                uid = firebaseUser.uid,
                email = email,
                displayName = displayName,
                role = role,
                photoUrl = "",
                createdAt = System.currentTimeMillis()
            )

            Log.d("UserRepoImpl", "Saving user to Firestore with role: $role")
            usersCollection.document(firebaseUser.uid).set(user).await()
            Log.d("UserRepoImpl", "User saved to Firestore successfully")

            Log.d("UserRepoImpl", "Sign up successful: ${user.email}")
            Result.success(user)
        } catch (e: Exception) {
            Log.e("UserRepoImpl", "Sign up failed: ${e.message}", e)
            try {
                auth.currentUser?.delete()?.await()
                Log.d("UserRepoImpl", "Cleaned up auth user after Firestore failure")
            } catch (cleanupError: Exception) {
                Log.e("UserRepoImpl", "Failed to cleanup auth user: ${cleanupError.message}")
            }
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<User> {
        return try {
            Log.d("UserRepoImpl", "Starting Google sign-in for: ${account.email}")

            if (account.idToken == null) {
                Log.e("UserRepoImpl", "ID Token is null!")
                return Result.failure(Exception("Google authentication failed - no token"))
            }

            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user ?: return Result.failure(Exception("User not found"))

            // ✅ Determine role based on AdminConfig
            val role = if (AdminConfig.isAdmin(firebaseUser.email ?: "")) "admin" else "customer"

            val userDoc = usersCollection.document(firebaseUser.uid).get().await()
            val user = if (userDoc.exists()) {
                val existingUser = userDoc.toObject(User::class.java)?.copy(
                    uid = firebaseUser.uid,
                    role = role
                )
                // Update Firestore if role changed or doesn't exist
                val currentRole = userDoc.getString("role")
                if (currentRole != role) {
                    usersCollection.document(firebaseUser.uid).update("role", role).await()
                }
                existingUser?.copy(role = role)
            } else {
                val newUser = User(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    displayName = firebaseUser.displayName ?: "",
                    photoUrl = firebaseUser.photoUrl?.toString() ?: "",
                    role = role,
                    createdAt = System.currentTimeMillis()
                )
                usersCollection.document(firebaseUser.uid).set(newUser).await()
                newUser
            }

            Log.d("UserRepoImpl", "Google sign-in successful: ${user?.email}, role: ${user?.role}")
            user?.let { Result.success(it) } ?: Result.failure(Exception("Failed to load user"))
        } catch (e: Exception) {
            Log.e("UserRepoImpl", "Google sign-in failed: ${e.message}", e)
            Result.failure(Exception("Google sign-in failed: ${e.message}"))
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Log.d("UserRepoImpl", "Sign out successful")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepoImpl", "Sign out failed: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null

        return try {
            // ✅ Determine role based on AdminConfig
            val role = if (AdminConfig.isAdmin(firebaseUser.email ?: "")) "admin" else "customer"

            val userDoc = usersCollection.document(firebaseUser.uid).get().await()
            if (userDoc.exists()) {
                val user = userDoc.toObject(User::class.java)?.copy(role = role)
                // Update Firestore if role changed or doesn't exist
                val currentRole = userDoc.getString("role")
                if (currentRole != role) {
                    usersCollection.document(firebaseUser.uid).update("role", role).await()
                }
                user
            } else {
                val user = User(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    displayName = firebaseUser.displayName ?: "",
                    photoUrl = firebaseUser.photoUrl?.toString() ?: "",
                    role = role,
                    createdAt = System.currentTimeMillis()
                )
                usersCollection.document(firebaseUser.uid).set(user).await()
                user
            }
        } catch (e: Exception) {
            Log.e("UserRepoImpl", "Failed to get current user: ${e.message}", e)
            null
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Log.d("UserRepoImpl", "Password reset email sent to: $email")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepoImpl", "Password reset failed: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun checkIfAdmin(userId: String): Result<Boolean> {
        return try {
            val userDoc = usersCollection.document(userId).get().await()
            val user = userDoc.toObject(User::class.java)
            val isAdmin = user?.role == "admin"

            Log.d("UserRepoImpl", "Admin check for $userId: $isAdmin")
            Result.success(isAdmin)
        } catch (e: Exception) {
            Log.e("UserRepoImpl", "Admin check failed: ${e.message}")
            Result.failure(e)
        }
    }

    // ✅ NEW METHOD - Updates all existing users with correct role
    override suspend fun updateAllUsersWithRole(): Result<String> {
        return try {
            Log.d("UserRepoImpl", "Starting to update all users with roles...")
            val allUsers = usersCollection.get().await()
            var updatedCount = 0

            for (document in allUsers.documents) {
                val email = document.getString("email") ?: ""
                val currentRole = document.getString("role")
                val correctRole = if (AdminConfig.isAdmin(email)) "admin" else "customer"

                // Only update if role is missing or different
                if (currentRole != correctRole) {
                    usersCollection.document(document.id).update("role", correctRole).await()
                    updatedCount++
                    Log.d("UserRepoImpl", "Updated ${document.id}: $email -> $correctRole")
                }
            }

            val message = "Successfully updated $updatedCount users"
            Log.d("UserRepoImpl", message)
            Result.success(message)
        } catch (e: Exception) {
            Log.e("UserRepoImpl", "Failed to update users: ${e.message}", e)
            Result.failure(e)
        }
    }
}