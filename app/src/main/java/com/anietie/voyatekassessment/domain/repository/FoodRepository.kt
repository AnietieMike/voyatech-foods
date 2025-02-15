package com.anietie.voyatekassessment.domain.repository

import com.anietie.voyatekassessment.domain.model.Category
import com.anietie.voyatekassessment.domain.model.FoodItem
import com.anietie.voyatekassessment.domain.model.Tag
import kotlinx.coroutines.flow.Flow

interface FoodRepository {
    fun getAllFoods(): Flow<List<FoodItem>>
    suspend fun addFood(food: FoodItem)
    suspend fun removeFood(foodId: String)
    suspend fun getFoodById(foodId: String): FoodItem?
    suspend fun updateFood(food: FoodItem)
    suspend fun getCategories(): List<Category>
    suspend fun getTags(): List<Tag>
}