package com.mediqor.app.ui.viewmodel

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.mediqor.app.ui.model.UserModel
import com.mediqor.app.ui.repository.UserRepo

class UserViewModel(private val repo: UserRepo) {

    fun signUp(
        email: String,
        password: String,
        name: String,
        callback: (Boolean, String) -> Unit
    ) {
        repo.signUp(email, password, name, callback)
    }

    fun signIn(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        repo.signIn(email, password, callback)
    }

    fun signOut() {
        repo.signOut()
    }

    fun getCurrentUser(): UserModel? {
        return repo.getCurrentUser()
    }

    fun isLoggedIn(): Boolean {
        return repo.isLoggedIn()
    }

    fun getUserById(uid: String, callback: (UserModel?) -> Unit) {
        repo.getUserById(uid, callback)
    }

    fun updateUser(user: UserModel, callback: (Boolean, String) -> Unit) {
        repo.updateUser(user, callback)
    }

    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        return repo.getGoogleSignInClient(context)
    }

    fun signInWithGoogle(
        account: GoogleSignInAccount,
        callback: (Boolean, String) -> Unit
    ) {
        repo.signInWithGoogle(account, callback)
    }

    fun resetPassword(email: String, callback: (Boolean, String) -> Unit) {
        repo.resetPassword(email, callback)
    }
}