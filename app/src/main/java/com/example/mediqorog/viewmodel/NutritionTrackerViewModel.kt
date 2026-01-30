package com.example.mediqorog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediqorog.model.*
import com.example.mediqorog.repository.NutritionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NutritionTrackerViewModel(
    private val repository: NutritionRepository = NutritionRepository()
) : ViewModel() {

    // Selected date
    private val _selectedDate = MutableStateFlow(getCurrentDate())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    // Today's meals
    private val _todayMeals = MutableStateFlow<List<Meal>>(emptyList())
    val todayMeals: StateFlow<List<Meal>> = _todayMeals.asStateFlow()

    // Today's summary
    private val _todaySummary = MutableStateFlow(DailySummary(date = getCurrentDate()))
    val todaySummary: StateFlow<DailySummary> = _todaySummary.asStateFlow()

    // Weekly summaries
    private val _weeklySummaries = MutableStateFlow<List<DailySummary>>(emptyList())
    val weeklySummaries: StateFlow<List<DailySummary>> = _weeklySummaries.asStateFlow()

    // User stats (streak, etc)
    private val _userStats = MutableStateFlow(UserStats())
    val userStats: StateFlow<UserStats> = _userStats.asStateFlow()

    // Daily goal
    private val _dailyGoal = MutableStateFlow(DailyGoal())
    val dailyGoal: StateFlow<DailyGoal> = _dailyGoal.asStateFlow()

    // Search
    private val _searchResults = MutableStateFlow<List<FoodItem>>(emptyList())
    val searchResults: StateFlow<List<FoodItem>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    // UI states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        loadMealsForSelectedDate()
        loadDailyGoal()
        loadWeeklySummaries()
        loadUserStats()
    }

    private fun loadMealsForSelectedDate() {
        viewModelScope.launch {
            repository.getMealsForDate(_selectedDate.value).collect { meals ->
                _todayMeals.value = meals
                updateSummary()
            }
        }
    }

    private fun updateSummary() {
        val meals = _todayMeals.value
        val goal = _dailyGoal.value

        _todaySummary.value = DailySummary(
            date = _selectedDate.value,
            totalCalories = meals.sumOf { it.calories },
            totalProtein = meals.sumOf { it.protein },
            totalCarbs = meals.sumOf { it.carbs },
            totalFats = meals.sumOf { it.fats },
            mealCount = meals.size,
            goalMet = meals.sumOf { it.calories } >= (goal.calorieGoal * 0.8)
        )
    }

    private fun loadDailyGoal() {
        viewModelScope.launch {
            repository.getDailyGoal().collect { goal ->
                _dailyGoal.value = goal
                updateSummary()
            }
        }
    }

    private fun loadWeeklySummaries() {
        viewModelScope.launch {
            try {
                val summaries = repository.getWeeklySummaries()
                _weeklySummaries.value = summaries
            } catch (e: Exception) {
                _error.value = "Failed to load weekly data"
            }
        }
    }

    private fun loadUserStats() {
        viewModelScope.launch {
            try {
                val stats = repository.calculateStreak()
                _userStats.value = stats
            } catch (e: Exception) {
                _error.value = "Failed to load stats"
            }
        }
    }

    // Change selected date
    fun changeDate(daysOffset: Int) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = sdf.parse(_selectedDate.value) ?: Date()
        calendar.add(Calendar.DAY_OF_YEAR, daysOffset)

        _selectedDate.value = sdf.format(calendar.time)
        loadMealsForSelectedDate()
    }

    // Search food
    fun searchFood(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            try {
                _isSearching.value = true
                _error.value = null

                val result = repository.searchFood(query)

                if (result.isSuccess) {
                    _searchResults.value = result.getOrNull() ?: emptyList()
                } else {
                    _error.value = "Search failed: ${result.exceptionOrNull()?.message}"
                    _searchResults.value = emptyList()
                }
            } catch (e: Exception) {
                _error.value = "Search error: ${e.message}"
                _searchResults.value = emptyList()
            } finally {
                _isSearching.value = false
            }
        }
    }

    // Add meal
    fun addMeal(foodItem: FoodItem, mealType: MealType, portion: String = "100g") {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = repository.addMeal(foodItem, mealType, portion)

                if (result.isSuccess) {
                    _successMessage.value = "Meal added successfully!"
                    loadWeeklySummaries()
                    loadUserStats()
                } else {
                    _error.value = "Failed to add meal"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Delete meal
    fun deleteMeal(mealId: String) {
        viewModelScope.launch {
            try {
                val result = repository.deleteMeal(mealId)
                if (result.isSuccess) {
                    _successMessage.value = "Meal deleted"
                    loadWeeklySummaries()
                    loadUserStats()
                } else {
                    _error.value = "Failed to delete meal"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            }
        }
    }

    // Set daily goal
    fun setDailyGoal(calories: Int, protein: Int, carbs: Int, fats: Int) {
        viewModelScope.launch {
            try {
                val goal = DailyGoal(
                    calorieGoal = calories,
                    proteinGoal = protein,
                    carbsGoal = carbs,
                    fatsGoal = fats
                )
                val result = repository.setDailyGoal(goal)

                if (result.isSuccess) {
                    _successMessage.value = "Goal updated!"
                } else {
                    _error.value = "Failed to update goal"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            }
        }
    }

    fun clearSearchResults() {
        _searchResults.value = emptyList()
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSuccess() {
        _successMessage.value = null
    }

    // Progress calculations
    fun getCalorieProgress(): Float {
        val summary = _todaySummary.value
        val goal = _dailyGoal.value
        return if (goal.calorieGoal > 0) {
            (summary.totalCalories.toFloat() / goal.calorieGoal.toFloat()).coerceIn(0f, 1.5f)
        } else 0f
    }

    fun getProteinProgress(): Float {
        val summary = _todaySummary.value
        val goal = _dailyGoal.value
        return if (goal.proteinGoal > 0) {
            (summary.totalProtein.toFloat() / goal.proteinGoal.toFloat()).coerceIn(0f, 1.5f)
        } else 0f
    }

    fun getCarbsProgress(): Float {
        val summary = _todaySummary.value
        val goal = _dailyGoal.value
        return if (goal.carbsGoal > 0) {
            (summary.totalCarbs.toFloat() / goal.carbsGoal.toFloat()).coerceIn(0f, 1.5f)
        } else 0f
    }

    fun getFatsProgress(): Float {
        val summary = _todaySummary.value
        val goal = _dailyGoal.value
        return if (goal.fatsGoal > 0) {
            (summary.totalFats.toFloat() / goal.fatsGoal.toFloat()).coerceIn(0f, 1.5f)
        } else 0f
    }

    companion object {
        private fun getCurrentDate(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(Date())
        }
    }
}