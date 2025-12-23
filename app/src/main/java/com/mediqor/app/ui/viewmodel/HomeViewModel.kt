package com.mediqor.app.viewmodel

import androidx.lifecycle.ViewModel
import com.mediqor.app.R
import com.mediqor.app.model.Category
import com.mediqor.app.model.Product

class HomeViewModel : ViewModel() {

    val categories = listOf(
        Category("Pharmacy", R.drawable.mediqor),
        Category("Family Care", R.drawable.mediqor),
        Category("Personal Care", R.drawable.mediqor),
        Category("Supplements", R.drawable.mediqor),
        Category("Surgical", R.drawable.mediqor),
        Category("Devices", R.drawable.mediqor)
    )

    val products = listOf(
        Product("Zandu Balm Ultra Power-8ml", R.drawable.mediqor),
        Product("Vicks Vaporub", R.drawable.mediqor),
        Product("The Derma Co Niacinamide Serum", R.drawable.mediqor),
        Product("Minimalist Alpha Arbutin Serum", R.drawable.mediqor)
    )
}
