package com.mediqor.app.ui.repository

import android.content.Context
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

interface CommonRepo {
    suspend fun uploadImageToStorage(context: Context, imageUri: Uri): String
}

class CommonRepoImpl : CommonRepo {

    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference

    override suspend fun uploadImageToStorage(context: Context, imageUri: Uri): String {
        val fileName = "product_images/${UUID.randomUUID()}.jpg"
        val imageRef = storageRef.child(fileName)

        imageRef.putFile(imageUri).await()
        val downloadUrl = imageRef.downloadUrl.await()
        return downloadUrl.toString()
    }
}
//firebase build left
/*
development left*/
