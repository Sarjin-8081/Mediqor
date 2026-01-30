package com.example.mediqorog.repository

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpoonacularApi {

    @GET("food/ingredients/search")
    suspend fun searchFood(
        @Query("query") query: String,
        @Query("number") number: Int = 10,
        @Query("apiKey") apiKey: String
    ): FoodSearchResponse

    @GET("food/ingredients/{id}/information")
    suspend fun getFoodInfo(
        @Path("id") id: Int,
        @Query("amount") amount: Double = 100.0,
        @Query("unit") unit: String = "grams",
        @Query("apiKey") apiKey: String
    ): FoodNutritionResponse
}

data class FoodSearchResponse(
    val results: List<FoodSearchResult>,
    val offset: Int,
    val number: Int,
    val totalResults: Int
)

data class FoodSearchResult(
    val id: Int,
    val name: String,
    val image: String
)

data class FoodNutritionResponse(
    val id: Int,
    val name: String,
    val amount: Double,
    val unit: String,
    val nutrition: Nutrition
)

data class Nutrition(
    val nutrients: List<Nutrient>
)

data class Nutrient(
    val name: String,
    val amount: Double,
    val unit: String
)