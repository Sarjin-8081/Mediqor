package com.example.mediqorog.repository

import android.util.Log
import com.example.mediqorog.model.User
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

            val userDoc = usersCollection.document(firebaseUser.uid).get().await()
            val user = if (userDoc.exists()) {
                userDoc.toObject(User::class.java)?.copy(uid = firebaseUser.uid)
            } else {
                val newUser = User(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    displayName = firebaseUser.displayName ?: "",
                    role = "customer"
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

            val user = User(
                uid = firebaseUser.uid,
                email = email,
                displayName = displayName,
                role = "customer",
                photoUrl = "",
                createdAt = System.currentTimeMillis()
            )

            Log.d("UserRepoImpl", "Saving user to Firestore...")
            usersCollection.document(firebaseUser.uid).set(user).await()
            Log.d("UserRepoImpl", "User saved to Firestore successfully")

            Log.d("UserRepoImpl", "Sign up successful: ${user.email}")
            Result.success(user)
        } catch (e: Exception) {
            Log.e("UserRepoImpl", "Sign up failed: ${e.message}", e)
            // Even if Firestore save fails, Auth user is created, so we should clean up
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

            val userDoc = usersCollection.document(firebaseUser.uid).get().await()
            val user = if (userDoc.exists()) {
                userDoc.toObject(User::class.java)?.copy(uid = firebaseUser.uid)
            } else {
                val newUser = User(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    displayName = firebaseUser.displayName ?: "",
                    photoUrl = firebaseUser.photoUrl?.toString() ?: "",
                    role = "customer",
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
            val userDoc = usersCollection.document(firebaseUser.uid).get().await()
            if (userDoc.exists()) {
                userDoc.toObject(User::class.java)
            } else {
                // User exists in Auth but not in Firestore, create the document
                val user = User(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    displayName = firebaseUser.displayName ?: "",
                    photoUrl = firebaseUser.photoUrl?.toString() ?: "",
                    role = "customer",
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
}