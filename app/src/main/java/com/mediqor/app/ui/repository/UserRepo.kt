package com.mediqor.app.ui.repository

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.mediqor.app.ui.model.UserModel

interface UserRepo {
    fun signUp(
        email: String,
        password: String,
        name: String,
        callback: (Boolean, String) -> Unit
    )

    fun signIn(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    )

    fun signOut()

    fun getCurrentUser(): UserModel?

    fun isLoggedIn(): Boolean

    fun getUserById(
        uid: String,
        callback: (UserModel?) -> Unit
    )

    fun updateUser(
        user: UserModel,
        callback: (Boolean, String) -> Unit
    )

    fun getGoogleSignInClient(context: Context): GoogleSignInClient

    fun signInWithGoogle(
        account: GoogleSignInAccount,
        callback: (Boolean, String) -> Unit
    )
}