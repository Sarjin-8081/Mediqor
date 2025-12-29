package com.mediqor.app.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediqor.app.ui.repository.CommonRepo
import kotlinx.coroutines.launch

class CommonViewModel(
    private val repo: CommonRepo
) : ViewModel() {

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
