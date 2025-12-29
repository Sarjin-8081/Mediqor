package com.mediqor.app.viewmodel

import androidx.lifecycle.ViewModel
import com.mediqor.app.R
import com.mediqor.app.model.ProductModel

data class Category(val title: String, val imageRes: Int)

class HomeViewModel : ViewModel() {

    val categories = listOf(
        Category("Pharmacy", R.drawable.ic_launcher_foreground),
        Category("Family Care", R.drawable.ic_launcher_foreground),
        Category("Personal Care", R.drawable.ic_launcher_foreground),
        Category("Supplements", R.drawable.ic_launcher_foreground),
        Category("Surgical", R.drawable.ic_launcher_foreground),
        Category("Devices", R.drawable.ic_launcher_foreground)
    )

    val products = listOf(
        ProductModel(name = "Zandu Balm Ultra Power-8ml", imageUrl = "https://via.placeholder.com/150"),
        ProductModel(name = "Vicks Vaporub", imageUrl = "https://via.placeholder.com/150"),
        ProductModel(name = "The Derma Co Niacinamide Serum", imageUrl = "https://via.placeholder.com/150"),
        ProductModel(name = "Minimalist Alpha Arbutin Serum", imageUrl = "https://via.placeholder.com/150")
    )
}
