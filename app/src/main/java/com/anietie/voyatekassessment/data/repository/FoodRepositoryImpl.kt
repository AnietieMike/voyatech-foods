package com.anietie.voyatekassessment.data.repository

import android.util.Log
import com.anietie.voyatekassessment.data.remote.api.FoodApiService
import com.anietie.voyatekassessment.domain.model.Category
import com.anietie.voyatekassessment.domain.model.FoodItem
import com.anietie.voyatekassessment.domain.model.Tag
import com.anietie.voyatekassessment.domain.repository.FoodRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class FoodRepositoryImpl(
    private val api: FoodApiService,
) : FoodRepository {
    override fun getAllFoods(): Flow<List<FoodItem>> =
        flow {
            val response = api.fetchAllFoods()
            val dtoList = response.body()?.data ?: emptyList()
            emit(dtoList.map { it.toDomain() })
        }.flowOn(IO)

    override suspend fun addFood(
        name: String,
        description: String,
        categoryId: Int,
        calories: Int,
        tags: List<Int>,
        imagePaths: List<String>,
    ): FoodItem {
        // Function to Convert Tags to Indexed Parts
        fun createTagParts(tags: List<Int>): Map<String, RequestBody> =
            tags.associate { index ->
                "tags[$index]" to index.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            }

        // Function to Convert Images to Multipart
        fun prepareImages(imagePaths: List<String>): List<MultipartBody.Part> =
            imagePaths.mapIndexed { index, path ->
                val file = File(path)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("images[$index]", file.name, requestFile)
            }

        // Add Food API Call
        try {
            // Validate Inputs
            if (name.isBlank() || description.isBlank()) throw IllegalArgumentException("Name and Description cannot be empty.")
            if (categoryId <= 0) throw IllegalArgumentException("Invalid Category ID.")
            if (calories < 0) throw IllegalArgumentException("Calories must be non-negative.")
            if (imagePaths.isEmpty()) throw IllegalArgumentException("At least one image is required.")

            // Convert Inputs to RequestBody
            val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val categoryIdBody = categoryId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val caloriesBody = calories.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            // Convert Tags & Images
            val tagParts = createTagParts(tags)
            val imageParts = prepareImages(imagePaths)

            // API Call
            val response = api.addFood(nameBody, descriptionBody, categoryIdBody, caloriesBody, tagParts, imageParts)

            if (response.isSuccessful) {
                Log.d("API_RESPONSE", "Food uploaded successfully")
                return response.body()?.data?.toDomain() ?: throw Exception("API Error: No Data")
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                Log.e("API_ERROR", "Error: ${response.code()} $errorMessage")
                throw Exception("API Error: ${response.code()} - $errorMessage")
            }
        } catch (e: Exception) {
            Log.e("API_EXCEPTION", "Exception: ${e.message}")
            throw e // Rethrow exception for handling at a higher level
        }
    }

    override suspend fun removeFood(foodId: String) {
        api.removeFood(foodId)
    }

    override suspend fun updateFood(food: FoodItem) {
        api.updateFood(
            foodId = "01",
            name = food.name.toRequestBody("text/plain".toMediaTypeOrNull()),
            description = food.description.toRequestBody("text/plain".toMediaTypeOrNull()),
            categoryId = food.categoryId.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
            calories = food.calories!!.toRequestBody("text/plain".toMediaTypeOrNull()),
            tags =
                food.tags
                    ?.takeIf { it.isNotEmpty() }
                    ?.map { it.toRequestBody("text/plain".toMediaTypeOrNull()) } ?: emptyList(),
            images =
                food.images
                    ?.takeIf { it.isNotEmpty() }
                    ?.map {
                        val image = File(it.imageUrl)
                        val requestFile = image.asRequestBody("image/*".toMediaTypeOrNull())
                        MultipartBody.Part.createFormData("images", image.name, requestFile)
                    } ?: emptyList(),
        )
    }

    override suspend fun getCategories(): List<Category> {
        val response = api.getCategories().body()?.data ?: emptyList()
        return response.map { it.toDomain() }
    }

    override suspend fun getTags(): List<Tag> {
        val response = api.getTags().body()?.data ?: emptyList()
        return response.map { it.toDomain() }
    }
}
