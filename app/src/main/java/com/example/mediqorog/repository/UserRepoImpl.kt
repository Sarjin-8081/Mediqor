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
            val user = userDoc.toObject(User::class.java) ?: User(
                uid = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                displayName = firebaseUser.displayName ?: ""
            )

            Log.d("UserRepoImpl", "Sign in successful: ${user.email}")
            Result.success(user)
        } catch (e: Exception) {
            Log.e("UserRepoImpl", "Sign in failed: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun signUp(email: String, password: String, displayName: String): Result<User> {
        return try {
            Log.d("UserRepoImpl", "Attempting sign up for: $email")
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: return Result.failure(Exception("Failed to create user"))

            val user = User(
                uid = firebaseUser.uid,
                email = email,
                displayName = displayName
            )

            // Save user to Firestore
            usersCollection.document(firebaseUser.uid).set(user).await()

            Log.d("UserRepoImpl", "Sign up successful: ${user.email}")
            Result.success(user)
        } catch (e: Exception) {
            Log.e("UserRepoImpl", "Sign up failed: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<User> {
        return try {
            Log.d("UserRepoImpl", "Starting Google sign-in for: ${account.email}")
            Log.d("UserRepoImpl", "ID Token present: ${account.idToken != null}")

            if (account.idToken == null) {
                Log.e("UserRepoImpl", "ID Token is null! Firebase authentication may not be configured.")
                // Create user without Firebase Auth as fallback
                val user = User(
                    uid = account.id ?: "",
                    email = account.email ?: "",
                    displayName = account.displayName ?: "",
                    photoUrl = account.photoUrl?.toString() ?: ""
                )
                return Result.success(user)
            }

            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            Log.d("UserRepoImpl", "Credential created, signing in with Firebase...")

            val authResult = auth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user ?: return Result.failure(Exception("User not found"))

            val user = User(
                uid = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                displayName = firebaseUser.displayName ?: "",
                photoUrl = firebaseUser.photoUrl?.toString() ?: ""
            )

            // Save or update user in Firestore
            usersCollection.document(firebaseUser.uid).set(user).await()

            Log.d("UserRepoImpl", "Google sign-in successful: ${user.email}")
            Result.success(user)
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
            userDoc.toObject(User::class.java)
        } catch (e: Exception) {
            Log.e("UserRepoImpl", "Failed to get current user: ${e.message}")
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
}