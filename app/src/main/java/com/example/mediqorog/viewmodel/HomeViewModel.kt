package com.example.mediqorog.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.mediqorog.R
import com.example.mediqorog.model.CategoryModel
import com.example.mediqorog.model.ProductModel


class HomeViewModel : ViewModel() {

    val categories = listOf(
        CategoryModel(
            title = "Pharmacy",
            icon = Icons.Default.LocalPharmacy,
            color = Color(0xFF4CAF50),
            drawableRes = R.drawable.pharmacy
        ),
        CategoryModel(
            title = "Family Care",
            icon = Icons.Default.FamilyRestroom,
            color = Color(0xFF2196F3),
            drawableRes = R.drawable.family_care
        ),
        CategoryModel(
            title = "Personal Care",
            icon = Icons.Default.Person,
            color = Color(0xFFFF9800),
            drawableRes = R.drawable.personal_care
        ),
        CategoryModel(
            title = "Supplements",
            icon = Icons.Default.Lightbulb,
            color = Color(0xFF9C27B0),
            drawableRes = R.drawable.supplements
        ),
        CategoryModel(
            title = "Surgical",
            icon = Icons.Default.Build,
            color = Color(0xFFF44336),
            drawableRes = R.drawable.surgical
        ),
        CategoryModel(
            title = "Devices",
            icon = Icons.Default.DevicesOther,
            color = Color(0xFF00BCD4),
            drawableRes = R.drawable.devices
        )
    )

    // Flash Sale Products - FIXED ORDER: id, name, price, description, imageUrl, category
    val flashSaleProducts = listOf(
        ProductModel("1", "Paracetamol 500mg", 50.0, "Pain relief", "", "Pharmacy"),
        ProductModel("2", "Vitamin C 1000mg", 120.0, "Immunity booster", "", "Supplements"),
        ProductModel("3", "Hand Sanitizer", 150.0, "70% Alcohol", "", "Personal Care"),
        ProductModel("4", "Face Mask N95", 200.0, "Pack of 5", "", "Surgical")
    )

    // Top Selling Products - FIXED ORDER
    val topSellingProducts = listOf(
        ProductModel("5", "Aspirin 25mg", 80.0, "Heart health", "", "Pharmacy"),
        ProductModel("6", "Omega-3 Fish Oil", 450.0, "Brain & heart", "", "Supplements"),
        ProductModel("7", "Baby Diapers", 450.0, "Pack of 20", "", "Family Care"),
        ProductModel("8", "BP Monitor", 2500.0, "Digital", "", "Devices")
    )

    // Sunscreen Products - FIXED ORDER
    val sunscreenProducts = listOf(
        ProductModel("9", "Neutrogena SPF 50+", 850.0, "UVA/UVB protection", "", "Personal Care"),
        ProductModel("10", "Nivea Sun Protect", 650.0, "SPF 30", "", "Personal Care"),
        ProductModel("11", "Cetaphil Sun SPF 50", 950.0, "For sensitive skin", "", "Personal Care"),
        ProductModel("12", "Lotus Herbals SPF 40", 550.0, "Matte finish", "", "Personal Care")
    )

    // Body Lotion Products - FIXED ORDER
    val bodyLotionProducts = listOf(
        ProductModel("13", "Vaseline Body Lotion", 350.0, "Deep moisture", "", "Personal Care"),
        ProductModel("14", "Nivea Nourishing Lotion", 450.0, "24h hydration", "", "Personal Care"),
        ProductModel("15", "Cetaphil Moisturizer", 750.0, "For dry skin", "", "Personal Care"),
        ProductModel("16", "Himalaya Body Lotion", 280.0, "Herbal formula", "", "Personal Care")
    )

    val products = flashSaleProducts + topSellingProducts + sunscreenProducts + bodyLotionProducts
}