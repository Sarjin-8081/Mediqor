package com.example.mediqorog.model

data class ProductModel(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val imageUrl: String = "",
    val stock: Int = 0,
    val brandName: String = "",
    val requiresPrescription: Boolean = false,
    val dosageForm: String = "",  // "Tablet", "Syrup", "Capsule"
    val strength: String = "",     // "500mg", "10ml"
    val discount: Double = 0.0,    // percentage
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val isFeatured: Boolean = false,
    val tags: List<String> = emptyList(),
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getFormattedPrice(): String {
        return "NPR %.2f".format(price)
    }

    fun getDiscountedPrice(): Double {
        return if (discount > 0) {
            price - (price * discount / 100)
        } else {
            price
        }
    }

    fun getFormattedDiscountedPrice(): String {
        return "NPR %.2f".format(getDiscountedPrice())
    }
}