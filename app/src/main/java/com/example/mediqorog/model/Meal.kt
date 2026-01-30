package com.example.mediqorog.model

// Simple data class for Firestore (no Room annotations)
data class Meal(
    val id: String = "",  // Changed from Long to String for Firestore document IDs
    val name: String = "",
    val calories: Int = 0,
    val protein: Int = 0,
    val carbs: Int = 0,
    val fats: Int = 0,
    val mealType: String = MealType.SNACK.name,
    val timestamp: Long = System.currentTimeMillis(),
    val imageUri: String = "",
    val portion: String = "",
    val date: String = "",
    val userId: String = ""
) {
    fun getMealTypeEnum(): MealType {
        return try {
            MealType.valueOf(mealType)
        } catch (e: Exception) {
            MealType.SNACK
        }
    }
}

enum class MealType {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK;

    fun getDisplayName(): String = when(this) {
        MealType.BREAKFAST -> "Breakfast"
        MealType.LUNCH -> "Lunch"
        MealType.DINNER -> "Dinner"
        MealType.SNACK -> "Snack"
    }

    fun getEmoji(): String = when(this) {
        MealType.BREAKFAST -> "🌅"
        MealType.LUNCH -> "🍱"
        MealType.DINNER -> "🌙"
        MealType.SNACK -> "🍿"
    }
}

// Firestore-compatible daily goal
data class DailyGoal(
    val id: Int = 1,
    val calorieGoal: Int = 2000,
    val proteinGoal: Int = 150,
    val carbsGoal: Int = 200,
    val fatsGoal: Int = 65
)

// UI Data classes (not stored in Firestore)
data class DailySummary(
    val date: String,
    val totalCalories: Int = 0,
    val totalProtein: Int = 0,
    val totalCarbs: Int = 0,
    val totalFats: Int = 0,
    val mealCount: Int = 0,
    val goalMet: Boolean = false  // Added this property
)

data class WeeklySummary(
    val dailySummaries: List<DailySummary> = emptyList(),
    val averageCalories: Int = 0,
    val totalMeals: Int = 0
)

// Food Item for search results
data class FoodItem(
    val id: String = "",
    val name: String = "",
    val calories: Int = 0,
    val protein: Int = 0,
    val carbs: Int = 0,
    val fats: Int = 0,
    val imageUrl: String = ""
)

// Helper function to create Meal with MealType enum
fun createMeal(
    name: String,
    calories: Int,
    protein: Int,
    carbs: Int,
    fats: Int,
    mealType: MealType,
    imageUri: String = "",
    portion: String = "",
    date: String = "",
    userId: String = ""
): Meal {
    return Meal(
        id = System.currentTimeMillis().toString(),  // Generate String ID
        name = name,
        calories = calories,
        protein = protein,
        carbs = carbs,
        fats = fats,
        mealType = mealType.name,
        timestamp = System.currentTimeMillis(),
        imageUri = imageUri,
        portion = portion,
        date = date,
        userId = userId
    )
}