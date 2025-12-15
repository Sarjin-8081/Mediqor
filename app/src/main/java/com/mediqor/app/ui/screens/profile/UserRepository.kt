package com.mediqor.app.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mediqor.app.ui.screens.profile.model.User
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = Firebase.firestore
    private val usersCollection = firestore.collection("users")

    fun currentUid(): String? = auth.currentUser?.uid

    suspend fun fetchCurrentUser(): User? {
        val uid = currentUid() ?: return null
        val doc = usersCollection.document(uid).get().await()
        if (!doc.exists()) return User(uid = uid) // minimal
        val data = doc.data ?: return null
        return User(
            uid = uid,
            name = data["name"] as? String ?: "",
            email = data["email"] as? String ?: (auth.currentUser?.email ?: ""),
            photoUrl = data["photoUrl"] as? String,
            ePoints = (data["ePoints"] as? Long)?.toInt() ?: (data["ePoints"] as? Int ?: 0)
        )
    }
}
