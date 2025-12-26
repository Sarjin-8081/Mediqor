package com.mediqor.app.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class CommonViewModel(
    private val repo: CommonRepo
) : ViewModel() {

    // Upload image to Firebase Storage
    fun uploadImage(
        context: Context,
        imageUri: Uri?,
        onResult: (String?) -> Unit
    ) {
        if (imageUri == null) {
            onResult(null)
            return
        }

        viewModelScope.launch {
            try {
                val imageUrl = repo.uploadImageToStorage(context, imageUri)
                onResult(imageUrl)
            } catch (e: Exception) {
                Log.e("CommonViewModel", "Upload failed: ${e.message}")
                onResult(null)
            }
        }
    }
}

// Repository Interface
interface CommonRepo {
    suspend fun uploadImageToStorage(context: Context, imageUri: Uri): String
}

// Repository Implementation
class CommonRepoImpl : CommonRepo {

    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference

    override suspend fun uploadImageToStorage(context: Context, imageUri: Uri): String {
        val fileName = "product_images/${UUID.randomUUID()}.jpg"
        val imageRef = storageRef.child(fileName)

        // Upload the file
        imageRef.putFile(imageUri).await()

        // Get download URL
        val downloadUrl = imageRef.downloadUrl.await()
        return downloadUrl.toString()
    }
}

// Alternative: If you want to compress the image before upload
class CommonRepoImplWithCompression : CommonRepo {

    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference

    override suspend fun uploadImageToStorage(context: Context, imageUri: Uri): String {
        val fileName = "product_images/${UUID.randomUUID()}.jpg"
        val imageRef = storageRef.child(fileName)

        // Get compressed bitmap
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        // Compress bitmap
        val baos = java.io.ByteArrayOutputStream()
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, baos)
        val data = baos.toByteArray()

        // Upload compressed image
        imageRef.putBytes(data).await()

        // Get download URL
        val downloadUrl = imageRef.downloadUrl.await()
        return downloadUrl.toString()
    }
}
