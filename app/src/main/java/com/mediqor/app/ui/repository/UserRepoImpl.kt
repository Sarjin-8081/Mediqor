package com.mediqor.app.ui.repository

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.mediqor.app.R
import com.mediqor.app.ui.model.UserModel

class UserRepoImpl : UserRepo {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("users")

    override fun signUp(
        email: String,
        password: String,
        name: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: ""

                val user = UserModel(
                    uid = uid,
                    email = email,
                    name = name
                )

                usersRef.child(uid).setValue(user)
                    .addOnSuccessListener {
                        callback(true, "Account created successfully!")
                    }
                    .addOnFailureListener { e ->
                        callback(false, "Failed to save user: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Sign up failed")
            }
    }

    override fun signIn(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                callback(true, "Login successful!")
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Login failed")
            }
    }

    override fun signOut() {
        auth.signOut()
    }

    override fun getCurrentUser(): UserModel? {
        val firebaseUser = auth.currentUser ?: return null

        return UserModel(
            uid = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            name = firebaseUser.displayName ?: ""
        )
    }

    override fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    override fun getUserById(uid: String, callback: (UserModel?) -> Unit) {
        usersRef.child(uid).get()
            .addOnSuccessListener { snapshot ->
                val user = snapshot.getValue(UserModel::class.java)
                callback(user)
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    override fun updateUser(user: UserModel, callback: (Boolean, String) -> Unit) {
        usersRef.child(user.uid).setValue(user)
            .addOnSuccessListener {
                callback(true, "Profile updated!")
            }
            .addOnFailureListener { e ->
                callback(false, "Update failed: ${e.message}")
            }
    }

    override fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, gso)
    }

    override fun signInWithGoogle(
        account: GoogleSignInAccount,
        callback: (Boolean, String) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        auth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                val user = result.user
                val uid = user?.uid ?: ""

                usersRef.child(uid).get()
                    .addOnSuccessListener { snapshot ->
                        if (!snapshot.exists()) {
                            val newUser = UserModel(
                                uid = uid,
                                email = user?.email ?: "",
                                name = user?.displayName ?: ""
                            )

                            usersRef.child(uid).setValue(newUser)
                                .addOnSuccessListener {
                                    callback(true, "Signed in with Google!")
                                }
                                .addOnFailureListener { e ->
                                    callback(false, "Failed to save user: ${e.message}")
                                }
                        } else {
                            callback(true, "Welcome back!")
                        }
                    }
                    .addOnFailureListener { e ->
                        callback(false, "Error checking user: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Google sign-in failed")
            }
    }

    override fun resetPassword(email: String, callback: (Boolean, String) -> Unit) {
        android.util.Log.d("UserRepoImpl", "Attempting to send reset email to: $email")

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    android.util.Log.d("UserRepoImpl", "✅ Reset email sent successfully to: $email")
                    callback(true, "Password reset email sent! Check your inbox and spam folder.")
                } else {
                    val exception = task.exception
                    android.util.Log.e("UserRepoImpl", "❌ Failed to send reset email to: $email")
                    android.util.Log.e("UserRepoImpl", "Exception: ${exception?.message}")
                    android.util.Log.e(
                        "UserRepoImpl",
                        "Exception type: ${exception?.javaClass?.simpleName}"
                    )

                    val errorMessage = when {
                        exception?.message?.contains("badly formatted") == true ->
                            "Invalid email format"

                        exception?.message?.contains("no user record") == true ||
                                exception?.message?.contains("USER_NOT_FOUND") == true ->
                            "No account found with this email address"

                        exception?.message?.contains("network") == true ->
                            "Network error. Check your connection."

                        exception?.message?.contains("TOO_MANY_ATTEMPTS") == true ->
                            "Too many attempts. Please try again later."

                        else ->
                            "Failed to send reset email: ${exception?.message ?: "Unknown error"}"
                    }
                    callback(false, errorMessage)
                }
            }
    }
}