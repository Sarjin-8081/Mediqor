package com.example.mediqorog.repository

import android.util.Log
import com.example.mediqorog.BuildConfig
import com.example.mediqorog.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

class NutritionRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val TAG = "NutritionRepo"

    private val SPOONACULAR_API_KEY = try {
        BuildConfig.SPOONACULAR_API_KEY
    } catch (e: Exception) {
        Log.e(TAG, "API key error: ${e.message}")
        ""
    }
    private val SPOONACULAR_BASE_URL = "https://api.spoonacular.com"

    private fun getUserId(): String? = auth.currentUser?.uid

    private fun getToday(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    // Get meals for a specific date
    fun getMealsForDate(date: String): Flow<List<Meal>> = callbackFlow {
        val userId = getUserId()
        if (userId == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        try {
            val listener = firestore.collection("users")
                .document(userId)
                .collection("meals")
                .whereEqualTo("date", date)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Error: ${error.message}")
                        trySend(emptyList())
                        return@addSnapshotListener
                    }

                    val meals = snapshot?.documents?.mapNotNull {
                        try {
                            it.toObject(Meal::class.java)?.copy(id = it.id)
                        } catch (e: Exception) {
                            null
                        }
                    } ?: emptyList()

                    trySend(meals)
                }

            awaitClose { listener.remove() }
        } catch (e: Exception) {
            Log.e(TAG, "Flow error: ${e.message}")
            trySend(emptyList())
            close()
        }
    }

    // Get summary for a specific date
    suspend fun getSummaryForDate(date: String): DailySummary {
        return try {
            val userId = getUserId() ?: return DailySummary(date = date)

            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("meals")
                .whereEqualTo("date", date)
                .get()
                .await()

            val meals = snapshot.documents.mapNotNull {
                try {
                    it.toObject(Meal::class.java)
                } catch (e: Exception) {
                    null
                }
            }

            val goal = getDailyGoalSync()

            DailySummary(
                date = date,
                totalCalories = meals.sumOf { it.calories },
                totalProtein = meals.sumOf { it.protein },
                totalCarbs = meals.sumOf { it.carbs },
                totalFats = meals.sumOf { it.fats },
                mealCount = meals.size,
                goalMet = meals.sumOf { it.calories } >= (goal.calorieGoal * 0.8)
            )
        } catch (e: Exception) {
            Log.e(TAG, "Summary error: ${e.message}")
            DailySummary(date = date)
        }
    }

    // Get last 7 days summaries
    suspend fun getWeeklySummaries(): List<DailySummary> {
        return try {
            val summaries = mutableListOf<DailySummary>()
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val calendar = Calendar.getInstance()

            for (i in 0 until 7) {
                val date = sdf.format(calendar.time)
                summaries.add(getSummaryForDate(date))
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            }

            summaries.reversed()
        } catch (e: Exception) {
            Log.e(TAG, "Weekly error: ${e.message}")
            emptyList()
        }
    }

    // Calculate streak
    suspend fun calculateStreak(): UserStats {
        return try {
            val userId = getUserId() ?: return UserStats()

            val summaries = getWeeklySummaries()
            var currentStreak = 0
            var longestStreak = 0
            var tempStreak = 0

            // Calculate streaks
            for (summary in summaries.reversed()) {
                if (summary.goalMet) {
                    tempStreak++
                    if (tempStreak > longestStreak) longestStreak = tempStreak
                } else {
                    if (summary.date == getToday()) {
                        // Today not complete yet
                    } else {
                        tempStreak = 0
                    }
                }
            }

            currentStreak = tempStreak

            // Get total stats
            val totalSnapshot = firestore.collection("users")
                .document(userId)
                .collection("meals")
                .get()
                .await()

            UserStats(
                currentStreak = currentStreak,
                longestStreak = longestStreak,
                totalMealsLogged = totalSnapshot.size(),
                totalDaysTracked = summaries.count { it.mealCount > 0 },
                lastLoggedDate = getToday()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Streak error: ${e.message}")
            UserStats()
        }
    }

    // Get daily goal
    fun getDailyGoal(): Flow<DailyGoal> = callbackFlow {
        val userId = getUserId()
        if (userId == null) {
            trySend(DailyGoal())
            close()
            return@callbackFlow
        }

        try {
            val listener = firestore.collection("users")
                .document(userId)
                .collection("settings")
                .document("dailyGoal")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(DailyGoal())
                        return@addSnapshotListener
                    }

                    val goal = try {
                        snapshot?.toObject(DailyGoal::class.java) ?: DailyGoal()
                    } catch (e: Exception) {
                        DailyGoal()
                    }

                    trySend(goal)
                }

            awaitClose { listener.remove() }
        } catch (e: Exception) {
            trySend(DailyGoal())
            close()
        }
    }

    // Get daily goal synchronously
    private suspend fun getDailyGoalSync(): DailyGoal {
        return try {
            val userId = getUserId() ?: return DailyGoal()

            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("settings")
                .document("dailyGoal")
                .get()
                .await()

            snapshot.toObject(DailyGoal::class.java) ?: DailyGoal()
        } catch (e: Exception) {
            DailyGoal()
        }
    }

    // Set daily goal
    suspend fun setDailyGoal(goal: DailyGoal): Result<Unit> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("Not logged in"))

            firestore.collection("users")
                .document(userId)
                .collection("settings")
                .document("dailyGoal")
                .set(goal)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Search food
    suspend fun searchFood(query: String): Result<List<FoodItem>> {
        return withContext(Dispatchers.IO) {
            try {
                if (SPOONACULAR_API_KEY.isEmpty()) {
                    return@withContext Result.failure(Exception("API key missing"))
                }

                val encoded = URLEncoder.encode(query, "UTF-8")
                val url = "$SPOONACULAR_BASE_URL/food/ingredients/search?query=$encoded&number=10&apiKey=$SPOONACULAR_API_KEY"

                val response = URL(url).readText()
                val json = JSONObject(response)
                val results = json.getJSONArray("results")

                val items = mutableListOf<FoodItem>()

                for (i in 0 until minOf(results.length(), 10)) {
                    try {
                        val item = results.getJSONObject(i)
                        val id = item.getInt("id").toString()
                        val name = item.getString("name")
                        val image = item.optString("image", "")

                        val nutrition = getNutrition(id)

                        items.add(FoodItem(
                            id = id,
                            name = name.capitalize(),
                            calories = nutrition.calories,
                            protein = nutrition.protein,
                            carbs = nutrition.carbs,
                            fats = nutrition.fats,
                            imageUrl = if (image.isNotEmpty())
                                "https://spoonacular.com/cdn/ingredients_100x100/$image" else ""
                        ))
                    } catch (e: Exception) {
                        Log.e(TAG, "Parse error: ${e.message}")
                    }
                }

                Result.success(items)
            } catch (e: Exception) {
                Log.e(TAG, "Search error: ${e.message}")
                Result.failure(e)
            }
        }
    }

    // Get nutrition info
    private fun getNutrition(id: String): NutritionData {
        return try {
            val url = "$SPOONACULAR_BASE_URL/food/ingredients/$id/information?amount=100&unit=grams&apiKey=$SPOONACULAR_API_KEY"
            val response = URL(url).readText()
            val json = JSONObject(response)
            val nutrition = json.getJSONObject("nutrition")
            val nutrients = nutrition.getJSONArray("nutrients")

            var cal = 0
            var pro = 0
            var carb = 0
            var fat = 0

            for (i in 0 until nutrients.length()) {
                val nutrient = nutrients.getJSONObject(i)
                when (nutrient.getString("name")) {
                    "Calories" -> cal = nutrient.getDouble("amount").toInt()
                    "Protein" -> pro = nutrient.getDouble("amount").toInt()
                    "Carbohydrates" -> carb = nutrient.getDouble("amount").toInt()
                    "Fat" -> fat = nutrient.getDouble("amount").toInt()
                }
            }

            NutritionData(cal, pro, carb, fat)
        } catch (e: Exception) {
            NutritionData(0, 0, 0, 0)
        }
    }

    // Add meal
    suspend fun addMeal(foodItem: FoodItem, mealType: MealType, portion: String): Result<Unit> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("Not logged in"))

            val meal = hashMapOf(
                "userId" to userId,
                "name" to foodItem.name,
                "calories" to foodItem.calories,
                "protein" to foodItem.protein,
                "carbs" to foodItem.carbs,
                "fats" to foodItem.fats,
                "mealType" to mealType.name,
                "timestamp" to System.currentTimeMillis(),
                "imageUri" to foodItem.imageUrl,
                "portion" to portion,
                "date" to getToday()
            )

            firestore.collection("users")
                .document(userId)
                .collection("meals")
                .add(meal)
                .await()

            Log.d(TAG, "Meal added: ${foodItem.name}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Add meal error: ${e.message}")
            Result.failure(e)
        }
    }

    // Delete meal
    suspend fun deleteMeal(mealId: String): Result<Unit> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("Not logged in"))

            firestore.collection("users")
                .document(userId)
                .collection("meals")
                .document(mealId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private data class NutritionData(
        val calories: Int,
        val protein: Int,
        val carbs: Int,
        val fats: Int
    )
}