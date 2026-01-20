package com.example.mediqorog.repository

import com.example.mediqorog.model.ProductModel

class ProductRepoImpl : ProductRepository {

    override fun getProductsByCategory(category: String): List<ProductModel> {
        return when(category.lowercase()) {
            "pharmacy" -> getPharmacyProducts()
            "family care" -> getFamilyCareProducts()
            "personal care" -> getPersonalCareProducts()
            "supplements" -> getSupplementsProducts()
            "surgical" -> getSurgicalProducts()
            "devices" -> getDevicesProducts()
            else -> emptyList()
        }
    }

    private fun getPharmacyProducts(): List<ProductModel> {
        return listOf(
            ProductModel(
                id = "1",
                name = "Paracetamol 500mg",
                price = 50.0,
                description = "Pain Relief",
                category = "pharmacy"
            ),
            ProductModel(
                id = "2",
                name = "Vitamin C 1000mg",
                price = 120.0,
                description = "Immunity Booster",
                category = "pharmacy"
            ),
            ProductModel(
                id = "3",
                name = "Hand Sanitizer 500ml",
                price = 80.0,
                description = "Hygiene",
                category = "pharmacy"
            ),
            ProductModel(
                id = "4",
                name = "Cough Syrup",
                price = 150.0,
                description = "Cold and Flu",
                category = "pharmacy"
            ),
            ProductModel(
                id = "5",
                name = "Bandage Roll",
                price = 45.0,
                description = "First Aid",
                category = "pharmacy"
            ),
            ProductModel(
                id = "6",
                name = "Antiseptic Cream",
                price = 95.0,
                description = "Wound Care",
                category = "pharmacy"
            )
        )
    }

    private fun getFamilyCareProducts(): List<ProductModel> {
        return listOf(
            ProductModel(
                id = "7",
                name = "Baby Diapers",
                price = 450.0,
                description = "Baby Care",
                category = "family care"
            ),
            ProductModel(
                id = "8",
                name = "Baby Wipes",
                price = 180.0,
                description = "Baby Hygiene",
                category = "family care"
            ),
            ProductModel(
                id = "9",
                name = "Family Health Kit",
                price = 890.0,
                description = "Complete Kit",
                category = "family care"
            ),
            ProductModel(
                id = "10",
                name = "Baby Shampoo",
                price = 220.0,
                description = "Gentle Care",
                category = "family care"
            ),
            ProductModel(
                id = "11",
                name = "Baby Powder",
                price = 150.0,
                description = "Soft Skin",
                category = "family care"
            ),
            ProductModel(
                id = "12",
                name = "Feeding Bottle",
                price = 320.0,
                description = "Baby Feeding",
                category = "family care"
            )
        )
    }

    private fun getPersonalCareProducts(): List<ProductModel> {
        return listOf(
            ProductModel(
                id = "13",
                name = "Face Wash",
                price = 250.0,
                description = "Skincare",
                category = "personal care"
            ),
            ProductModel(
                id = "14",
                name = "Body Lotion",
                price = 180.0,
                description = "Moisturizer",
                category = "personal care"
            ),
            ProductModel(
                id = "15",
                name = "Shampoo",
                price = 280.0,
                description = "Hair Care",
                category = "personal care"
            ),
            ProductModel(
                id = "16",
                name = "Conditioner",
                price = 320.0,
                description = "Hair Care",
                category = "personal care"
            ),
            ProductModel(
                id = "17",
                name = "Sunscreen SPF 50",
                price = 450.0,
                description = "Sun Protection",
                category = "personal care"
            ),
            ProductModel(
                id = "18",
                name = "Toothpaste",
                price = 120.0,
                description = "Dental Care",
                category = "personal care"
            )
        )
    }

    private fun getSupplementsProducts(): List<ProductModel> {
        return listOf(
            ProductModel(
                id = "19",
                name = "Multivitamin Tablets",
                price = 550.0,
                description = "Daily Vitamins",
                category = "supplements"
            ),
            ProductModel(
                id = "20",
                name = "Omega-3 Capsules",
                price = 780.0,
                description = "Heart Health",
                category = "supplements"
            ),
            ProductModel(
                id = "21",
                name = "Protein Powder",
                price = 1200.0,
                description = "Muscle Building",
                category = "supplements"
            ),
            ProductModel(
                id = "22",
                name = "Calcium Tablets",
                price = 420.0,
                description = "Bone Health",
                category = "supplements"
            ),
            ProductModel(
                id = "23",
                name = "Iron Supplements",
                price = 380.0,
                description = "Energy Boost",
                category = "supplements"
            ),
            ProductModel(
                id = "24",
                name = "Vitamin D3",
                price = 340.0,
                description = "Immunity",
                category = "supplements"
            )
        )
    }

    private fun getSurgicalProducts(): List<ProductModel> {
        return listOf(
            ProductModel(
                id = "25",
                name = "Surgical Mask (50pcs)",
                price = 350.0,
                description = "PPE",
                category = "surgical"
            ),
            ProductModel(
                id = "26",
                name = "Surgical Gloves",
                price = 120.0,
                description = "Protection",
                category = "surgical"
            ),
            ProductModel(
                id = "27",
                name = "Surgical Scissors",
                price = 280.0,
                description = "Medical Tools",
                category = "surgical"
            ),
            ProductModel(
                id = "28",
                name = "Gauze Pads",
                price = 95.0,
                description = "Wound Dressing",
                category = "surgical"
            ),
            ProductModel(
                id = "29",
                name = "Medical Tape",
                price = 65.0,
                description = "Adhesive",
                category = "surgical"
            ),
            ProductModel(
                id = "30",
                name = "Syringes (10pcs)",
                price = 150.0,
                description = "Medical Use",
                category = "surgical"
            )
        )
    }

    private fun getDevicesProducts(): List<ProductModel> {
        return listOf(
            ProductModel(
                id = "31",
                name = "Blood Pressure Monitor",
                price = 2500.0,
                description = "Digital Monitor",
                category = "devices"
            ),
            ProductModel(
                id = "32",
                name = "Digital Thermometer",
                price = 450.0,
                description = "Fast Reading",
                category = "devices"
            ),
            ProductModel(
                id = "33",
                name = "Glucose Meter",
                price = 1800.0,
                description = "Blood Sugar",
                category = "devices"
            ),
            ProductModel(
                id = "34",
                name = "Pulse Oximeter",
                price = 1200.0,
                description = "Oxygen Level",
                category = "devices"
            ),
            ProductModel(
                id = "35",
                name = "Nebulizer",
                price = 3500.0,
                description = "Respiratory Care",
                category = "devices"
            ),
            ProductModel(
                id = "36",
                name = "Weighing Scale",
                price = 890.0,
                description = "Digital Scale",
                category = "devices"
            )
        )
    }
}