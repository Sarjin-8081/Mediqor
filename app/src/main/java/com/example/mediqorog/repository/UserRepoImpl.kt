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
                // ✅ Existing user - load their data
                val existingUser = userDoc.toObject(User::class.java)?.copy(
                    uid = firebaseUser.uid,
                    role = role
                )

                // Update role and last login in Firestore
                val currentRole = userDoc.getString("role")
                val updates = mutableMapOf<String, Any>("lastLoginAt" to System.currentTimeMillis())
                if (currentRole != role) {
                    updates["role"] = role
                }
                usersCollection.document(firebaseUser.uid).update(updates).await()

                existingUser?.copy(
                    role = role,
                    lastLoginAt = System.currentTimeMillis()
                )
            } else {
                // ✅ User doesn't exist in Firestore - create with ALL fields
                val newUser = User(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    displayName = firebaseUser.displayName ?: "",
                    phoneNumber = firebaseUser.phoneNumber ?: "",
                    photoUrl = firebaseUser.photoUrl?.toString() ?: "",
                    role = role,
                    createdAt = System.currentTimeMillis(),

                    // New fields with defaults
                    bloodGroup = "",
                    dateOfBirth = "",
                    gender = "",
                    address = "",
                    emergencyContact = "",
                    isEmailVerified = firebaseUser.isEmailVerified,
                    isPhoneVerified = !firebaseUser.phoneNumber.isNullOrEmpty(),
                    accountStatus = "active",
                    lastLoginAt = System.currentTimeMillis(),
                    totalOrders = 0,
                    totalSpent = 0.0
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

            // ✅ Create User with ALL 18 fields
            val user = User(
                uid = firebaseUser.uid,
                email = email,
                displayName = displayName,
                phoneNumber = "", // Will be filled later
                photoUrl = "",
                role = role,
                createdAt = System.currentTimeMillis(),

                // Medical info - defaults
                bloodGroup = "",
                dateOfBirth = "",
                gender = "",
                address = "",
                emergencyContact = "",

                // Account status
                isEmailVerified = false,
                isPhoneVerified = false,
                accountStatus = "active", // ✅ New users are active
                lastLoginAt = System.currentTimeMillis(),

                // Statistics
                totalOrders = 0,
                totalSpent = 0.0,


            )

            Log.d("UserRepoImpl", "Saving user to Firestore with role: $role and all fields")
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

    // ✅ NEW: Enhanced signup with phone and blood group
    override suspend fun signUpEnhanced(
        email: String,
        password: String,
        displayName: String,
        phoneNumber: String,
        bloodGroup: String
    ): Result<User> {
        return try {
            Log.d("UserRepoImpl", "Attempting enhanced sign up for: $email")
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: return Result.failure(Exception("Failed to create user"))

            Log.d("UserRepoImpl", "Firebase user created, UID: ${firebaseUser.uid}")

            // ✅ Determine role based on AdminConfig
            val role = if (AdminConfig.isAdmin(email)) "admin" else "customer"

            // ✅ Create User with phone and blood group from form
            val user = User(
                uid = firebaseUser.uid,
                email = email,
                displayName = displayName,
                phoneNumber = phoneNumber, // ✅ From form
                photoUrl = "",
                role = role,
                createdAt = System.currentTimeMillis(),

                // Medical info
                bloodGroup = bloodGroup, // ✅ From form
                dateOfBirth = "",
                gender = "",
                address = "",
                emergencyContact = "",

                // Account status
                isEmailVerified = false,
                isPhoneVerified = phoneNumber.isNotEmpty(),
                accountStatus = "active",
                lastLoginAt = System.currentTimeMillis(),

                // Statistics
                totalOrders = 0,
                totalSpent = 0.0,


            )

            Log.d("UserRepoImpl", "Saving enhanced user to Firestore")
            usersCollection.document(firebaseUser.uid).set(user).await()
            Log.d("UserRepoImpl", "Enhanced user saved successfully")

            Result.success(user)
        } catch (e: Exception) {
            Log.e("UserRepoImpl", "Enhanced sign up failed: ${e.message}", e)
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
                // Existing user
                val existingUser = userDoc.toObject(User::class.java)?.copy(
                    uid = firebaseUser.uid,
                    role = role
                )

                // Update role and last login
                val currentRole = userDoc.getString("role")
                val updates = mutableMapOf<String, Any>("lastLoginAt" to System.currentTimeMillis())
                if (currentRole != role) {
                    updates["role"] = role
                }
                usersCollection.document(firebaseUser.uid).update(updates).await()

                existingUser?.copy(
                    role = role,
                    lastLoginAt = System.currentTimeMillis()
                )
            } else {
                // ✅ New Google user - create with ALL fields
                val newUser = User(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    displayName = firebaseUser.displayName ?: "",
                    phoneNumber = firebaseUser.phoneNumber ?: "",
                    photoUrl = firebaseUser.photoUrl?.toString() ?: "",
                    role = role,
                    createdAt = System.currentTimeMillis(),

                    // Medical info - defaults for Google users
                    bloodGroup = "",
                    dateOfBirth = "",
                    gender = "",
                    address = "",
                    emergencyContact = "",

                    // Account status
                    isEmailVerified = firebaseUser.isEmailVerified,
                    isPhoneVerified = !firebaseUser.phoneNumber.isNullOrEmpty(),
                    accountStatus = "active",
                    lastLoginAt = System.currentTimeMillis(),

                    // Statistics
                    totalOrders = 0,
                    totalSpent = 0.0,


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

                // Update role if changed
                val currentRole = userDoc.getString("role")
                if (currentRole != role) {
                    usersCollection.document(firebaseUser.uid).update("role", role).await()
                }
                user
            } else {
                // ✅ Create user if doesn't exist with ALL fields
                val user = User(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    displayName = firebaseUser.displayName ?: "",
                    phoneNumber = firebaseUser.phoneNumber ?: "",
                    photoUrl = firebaseUser.photoUrl?.toString() ?: "",
                    role = role,
                    createdAt = System.currentTimeMillis(),

                    // New fields with defaults
                    bloodGroup = "",
                    dateOfBirth = "",
                    gender = "",
                    address = "",
                    emergencyContact = "",
                    isEmailVerified = firebaseUser.isEmailVerified,
                    isPhoneVerified = !firebaseUser.phoneNumber.isNullOrEmpty(),
                    accountStatus = "active",
                    lastLoginAt = System.currentTimeMillis(),
                    totalOrders = 0,
                    totalSpent = 0.0,
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

    // ✅ Updates all existing users with correct role and adds missing fields
    override suspend fun updateAllUsersWithRole(): Result<String> {
        return try {
            Log.d("UserRepoImpl", "Starting to update all users with roles and missing fields...")
            val allUsers = usersCollection.get().await()
            var updatedCount = 0

            for (document in allUsers.documents) {
                val email = document.getString("email") ?: ""
                val currentRole = document.getString("role")
                val correctRole = if (AdminConfig.isAdmin(email)) "admin" else "customer"

                val updates = mutableMapOf<String, Any>()

                // Update role if different
                if (currentRole != correctRole) {
                    updates["role"] = correctRole
                }

                // Add missing fields with defaults
                if (!document.contains("bloodGroup")) updates["bloodGroup"] = ""
                if (!document.contains("dateOfBirth")) updates["dateOfBirth"] = ""
                if (!document.contains("gender")) updates["gender"] = ""
                if (!document.contains("address")) updates["address"] = ""
                if (!document.contains("emergencyContact")) updates["emergencyContact"] = ""
                if (!document.contains("isEmailVerified")) updates["isEmailVerified"] = false
                if (!document.contains("isPhoneVerified")) updates["isPhoneVerified"] = false
                if (!document.contains("accountStatus")) updates["accountStatus"] = "active"
                if (!document.contains("lastLoginAt")) updates["lastLoginAt"] = System.currentTimeMillis()
                if (!document.contains("totalOrders")) updates["totalOrders"] = 0
                if (!document.contains("totalSpent")) updates["totalSpent"] = 0.0
                if (!document.contains("deviceToken")) updates["deviceToken"] = ""

                // Only update if there are changes
                if (updates.isNotEmpty()) {
                    usersCollection.document(document.id).update(updates).await()
                    updatedCount++
                    Log.d("UserRepoImpl", "Updated ${document.id}: $email -> $correctRole (${updates.size} fields)")
                }
            }

            val message = "Successfully updated $updatedCount users with new fields"
            Log.d("UserRepoImpl", message)
            Result.success(message)
        } catch (e: Exception) {
            Log.e("UserRepoImpl", "Failed to update users: ${e.message}", e)
            Result.failure(e)
        }
    }
}