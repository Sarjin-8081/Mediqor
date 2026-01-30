package com.example.mediqorog.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mediqorog.model.FoodItem
import com.example.mediqorog.model.Meal
import com.example.mediqorog.model.MealType
import com.example.mediqorog.viewmodel.NutritionTrackerViewModel
import java.text.SimpleDateFormat
import java.util.*

class NutritionTrackerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                NutritionTrackerScreen(onBackClick = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionTrackerScreen(
    onBackClick: () -> Unit,
    viewModel: NutritionTrackerViewModel = viewModel()
) {
    var showSearch by remember { mutableStateOf(false) }
    var showGoalDialog by remember { mutableStateOf(false) }
    var showWeeklyView by remember { mutableStateOf(false) }

    val todayMeals by viewModel.todayMeals.collectAsState()
    val todaySummary by viewModel.todaySummary.collectAsState()
    val dailyGoal by viewModel.dailyGoal.collectAsState()
    val weeklySummaries by viewModel.weeklySummaries.collectAsState()
    val userStats by viewModel.userStats.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val error by viewModel.error.collectAsState()
    val success by viewModel.successMessage.collectAsState()

    // Show snackbar for errors/success
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(success) {
        success?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSuccess()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Nutrition Tracker", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showWeeklyView = !showWeeklyView }) {
                        Icon(
                            if (showWeeklyView) Icons.Default.DateRange else Icons.Default.CalendarMonth,
                            "Toggle View"
                        )
                    }
                    IconButton(onClick = { showGoalDialog = true }) {
                        Icon(Icons.Default.Settings, "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0B8FAC),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            if (!showWeeklyView) {
                FloatingActionButton(
                    onClick = { showSearch = true },
                    containerColor = Color(0xFF0B8FAC)
                ) {
                    Icon(Icons.Default.Add, "Add Meal", tint = Color.White)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding)
        ) {
            if (showWeeklyView) {
                WeeklyView(
                    weeklySummaries = weeklySummaries,
                    userStats = userStats,
                    dailyGoal = dailyGoal
                )
            } else {
                DailyView(
                    selectedDate = selectedDate,
                    meals = todayMeals,
                    summary = todaySummary,
                    goal = dailyGoal,
                    onDateChange = { viewModel.changeDate(it) },
                    onDeleteMeal = { viewModel.deleteMeal(it) },
                    viewModel = viewModel
                )
            }
        }
    }

    // Search/Add Meal Dialog
    if (showSearch) {
        SearchFoodDialog(
            onDismiss = { showSearch = false },
            viewModel = viewModel
        )
    }

    // Goal Settings Dialog
    if (showGoalDialog) {
        GoalSettingsDialog(
            currentGoal = dailyGoal,
            onDismiss = { showGoalDialog = false },
            onSave = { cal, pro, carb, fat ->
                viewModel.setDailyGoal(cal, pro, carb, fat)
                showGoalDialog = false
            }
        )
    }
}

@Composable
fun DailyView(
    selectedDate: String,
    meals: List<Meal>,
    summary: com.example.mediqorog.model.DailySummary,
    goal: com.example.mediqorog.model.DailyGoal,
    onDateChange: (Int) -> Unit,
    onDeleteMeal: (String) -> Unit,
    viewModel: NutritionTrackerViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Date Selector
        item {
            DateSelector(
                date = selectedDate,
                onPreviousDay = { onDateChange(-1) },
                onNextDay = { onDateChange(1) }
            )
        }

        // Stats Card
        item {
            StatsCard(
                summary = summary,
                goal = goal,
                viewModel = viewModel
            )
        }

        // Meals by Type
        item {
            MealsByTypeSection(
                meals = meals,
                onDeleteMeal = onDeleteMeal
            )
        }
    }
}

@Composable
fun DateSelector(
    date: String,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit
) {
    val sdf = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
    val dateObj = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)
    val displayDate = dateObj?.let { sdf.format(it) } ?: date

    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val isToday = date == today

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousDay) {
                Icon(Icons.Default.KeyboardArrowLeft, "Previous")
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = displayDate,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                if (isToday) {
                    Text(
                        text = "TODAY",
                        fontSize = 12.sp,
                        color = Color(0xFF0B8FAC),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            IconButton(
                onClick = onNextDay,
                enabled = !isToday
            ) {
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    "Next",
                    tint = if (isToday) Color.Gray else Color.Black
                )
            }
        }
    }
}

@Composable
fun StatsCard(
    summary: com.example.mediqorog.model.DailySummary,
    goal: com.example.mediqorog.model.DailyGoal,
    viewModel: NutritionTrackerViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Today's Progress",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0B8FAC)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NutrientProgressRing(
                    label = "Calories",
                    current = summary.totalCalories,
                    goal = goal.calorieGoal,
                    unit = "",
                    color = Color(0xFFFF6B6B),
                    progress = viewModel.getCalorieProgress()
                )
                NutrientProgressRing(
                    label = "Protein",
                    current = summary.totalProtein,
                    goal = goal.proteinGoal,
                    unit = "g",
                    color = Color(0xFF4ECDC4),
                    progress = viewModel.getProteinProgress()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NutrientProgressRing(
                    label = "Carbs",
                    current = summary.totalCarbs,
                    goal = goal.carbsGoal,
                    unit = "g",
                    color = Color(0xFFFFA726),
                    progress = viewModel.getCarbsProgress()
                )
                NutrientProgressRing(
                    label = "Fats",
                    current = summary.totalFats,
                    goal = goal.fatsGoal,
                    unit = "g",
                    color = Color(0xFF9C27B0),
                    progress = viewModel.getFatsProgress()
                )
            }
        }
    }
}

@Composable
fun NutrientProgressRing(
    label: String,
    current: Int,
    goal: Int,
    unit: String,
    color: Color,
    progress: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(90.dp)
    ) {
        Box(
            modifier = Modifier.size(70.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxSize(),
                color = color,
                strokeWidth = 7.dp,
                trackColor = color.copy(alpha = 0.2f)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$current",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                if (unit.isNotEmpty()) {
                    Text(
                        text = unit,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "/ $goal$unit",
            fontSize = 11.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun MealsByTypeSection(
    meals: List<Meal>,
    onDeleteMeal: (String) -> Unit
) {
    val mealsByType = meals.groupBy { it.getMealTypeEnum() }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        MealType.values().forEach { mealType ->
            val mealsForType = mealsByType[mealType] ?: emptyList()

            MealTypeCard(
                mealType = mealType,
                meals = mealsForType,
                onDeleteMeal = onDeleteMeal
            )
        }
    }
}

@Composable
fun MealTypeCard(
    mealType: MealType,
    meals: List<Meal>,
    onDeleteMeal: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(mealType.getEmoji(), fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = mealType.getDisplayName(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (meals.isNotEmpty()) {
                    val totalCal = meals.sumOf { it.calories }
                    Text(
                        text = "$totalCal cal",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0B8FAC)
                    )
                }
            }

            if (meals.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                meals.forEach { meal ->
                    MealItemRow(meal = meal, onDelete = { onDeleteMeal(meal.id) })
                    if (meal != meals.last()) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No meals logged",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun MealItemRow(
    meal: Meal,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF5F5F5))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = meal.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${meal.calories} cal • P:${meal.protein}g C:${meal.carbs}g F:${meal.fats}g",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        IconButton(onClick = { showDeleteConfirm = true }) {
            Icon(
                Icons.Default.Delete,
                "Delete",
                tint = Color.Red.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Meal?") },
            text = { Text("Are you sure you want to delete ${meal.name}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteConfirm = false
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// Continuation in next message due to length...
// PART 2 - Add these to the same file as Part 1

@Composable
fun WeeklyView(
    weeklySummaries: List<com.example.mediqorog.model.DailySummary>,
    userStats: com.example.mediqorog.model.UserStats,
    dailyGoal: com.example.mediqorog.model.DailyGoal
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Streak Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF0B8FAC)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🔥",
                            fontSize = 32.sp
                        )
                        Text(
                            text = "${userStats.currentStreak}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Day Streak",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🏆",
                            fontSize = 32.sp
                        )
                        Text(
                            text = "${userStats.longestStreak}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Best Streak",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }

        // Weekly Chart
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Last 7 Days",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    WeeklyBarChart(weeklySummaries, dailyGoal)
                }
            }
        }

        // Daily Breakdown
        item {
            Text(
                text = "Daily Breakdown",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(weeklySummaries) { summary ->
            DailySummaryCard(summary, dailyGoal)
        }
    }
}

@Composable
fun WeeklyBarChart(
    summaries: List<com.example.mediqorog.model.DailySummary>,
    goal: com.example.mediqorog.model.DailyGoal
) {
    val maxCalories = goal.calorieGoal * 1.2f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        summaries.forEach { summary ->
            val height = if (maxCalories > 0) {
                ((summary.totalCalories / maxCalories) * 150).coerceIn(0f, 150f)
            } else 0f

            val sdf = SimpleDateFormat("EEE", Locale.getDefault())
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(summary.date)
            val dayLabel = date?.let { sdf.format(it) } ?: ""

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(40.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(30.dp)
                        .height(height.dp)
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .background(
                            if (summary.goalMet) Color(0xFF4CAF50)
                            else if (summary.totalCalories > 0) Color(0xFFFFA726)
                            else Color.LightGray
                        )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dayLabel,
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun DailySummaryCard(
    summary: com.example.mediqorog.model.DailySummary,
    goal: com.example.mediqorog.model.DailyGoal
) {
    val sdf = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(summary.date)
    val displayDate = date?.let { sdf.format(it) } ?: summary.date

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayDate,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${summary.totalCalories} / ${goal.calorieGoal} cal",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Text(
                    text = "P:${summary.totalProtein}g C:${summary.totalCarbs}g F:${summary.totalFats}g",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (summary.goalMet) Color(0xFF4CAF50)
                        else if (summary.totalCalories > 0) Color(0xFFFFA726)
                        else Color.LightGray
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (summary.goalMet) "✓" else "${summary.mealCount}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFoodDialog(
    onDismiss: () -> Unit,
    viewModel: NutritionTrackerViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    var selectedFood by remember { mutableStateOf<FoodItem?>(null) }
    var showMealTypeDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight(0.9f)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier.fillMaxHeight()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Search Food",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        if (it.length >= 2) {
                            viewModel.searchFood(it)
                        } else if (it.isEmpty()) {
                            viewModel.clearSearchResults()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Type food name...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, "Search", tint = Color(0xFF0B8FAC))
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Results
                if (isSearching) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF0B8FAC))
                    }
                } else if (searchResults.isNotEmpty()) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(searchResults) { food ->
                            FoodSearchResultCard(
                                food = food,
                                onClick = {
                                    selectedFood = food
                                    showMealTypeDialog = true
                                }
                            )
                        }
                    }
                } else if (searchQuery.isNotEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No results found", color = Color.Gray)
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔍", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Search for food",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }

    // Meal Type Selection
    if (showMealTypeDialog && selectedFood != null) {
        AlertDialog(
            onDismissRequest = { showMealTypeDialog = false },
            title = { Text("Select Meal Type") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    MealType.values().forEach { mealType ->
                        Card(
                            onClick = {
                                viewModel.addMeal(selectedFood!!, mealType)
                                showMealTypeDialog = false
                                onDismiss()
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF5F5F5)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(mealType.getEmoji(), fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = mealType.getDisplayName(),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showMealTypeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun FoodSearchResultCard(
    food: FoodItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (food.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = food.imageUrl,
                    contentDescription = food.name,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🍽️", fontSize = 28.sp)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = food.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "per 100g",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Text(
                    text = "P:${food.protein}g C:${food.carbs}g F:${food.fats}g",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${food.calories}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B8FAC)
                )
                Text(
                    text = "cal",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun GoalSettingsDialog(
    currentGoal: com.example.mediqorog.model.DailyGoal,
    onDismiss: () -> Unit,
    onSave: (Int, Int, Int, Int) -> Unit
) {
    var calories by remember { mutableStateOf(currentGoal.calorieGoal.toString()) }
    var protein by remember { mutableStateOf(currentGoal.proteinGoal.toString()) }
    var carbs by remember { mutableStateOf(currentGoal.carbsGoal.toString()) }
    var fats by remember { mutableStateOf(currentGoal.fatsGoal.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Daily Goals") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = calories,
                    onValueChange = { calories = it },
                    label = { Text("Calories") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = protein,
                    onValueChange = { protein = it },
                    label = { Text("Protein (g)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = carbs,
                    onValueChange = { carbs = it },
                    label = { Text("Carbs (g)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = fats,
                    onValueChange = { fats = it },
                    label = { Text("Fats (g)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        calories.toIntOrNull() ?: 2000,
                        protein.toIntOrNull() ?: 150,
                        carbs.toIntOrNull() ?: 200,
                        fats.toIntOrNull() ?: 65
                    )
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}