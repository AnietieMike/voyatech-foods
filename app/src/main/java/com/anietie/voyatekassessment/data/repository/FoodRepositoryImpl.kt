package com.anietie.voyatekassessment.data.repository

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
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class FoodRepositoryImpl(
    private val api: FoodApiService
) : FoodRepository {

    override fun getAllFoods(): Flow<List<FoodItem>> = flow {
        val response = api.fetchAllFoods()
        val dtoList = response.body()?.data ?: emptyList()
        emit(dtoList.map { it.toDomain() })
    }.flowOn(IO)

    override suspend fun addFood(food: FoodItem) {
        api.addFood(
            name = food.name!!.toRequestBody("text/plain".toMediaTypeOrNull()),
            description = food.description!!.toRequestBody("text/plain".toMediaTypeOrNull()),
            categoryId = food.categoryId!!.toRequestBody("text/plain".toMediaTypeOrNull()),
            calories = food.calories!!.toRequestBody("text/plain".toMediaTypeOrNull()),
            images = food.images!!.map {
                val image = File(it)
                val requestFile = image.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("images", image.name, requestFile)
            },
            tags = food.tags?.takeIf { it.isNotEmpty() }
                ?.map { it.toRequestBody("text/plain".toMediaTypeOrNull()) } ?: emptyList()
        )
    }

    override suspend fun removeFood(foodId: String) {
        api.removeFood(foodId)
    }

    override suspend fun getFoodById(foodId: String): FoodItem? {
        TODO("Not yet implemented")
    }

    override suspend fun updateFood(food: FoodItem) {
        api.updateFood(
            foodId = "01",
            name = food.name!!.toRequestBody("text/plain".toMediaTypeOrNull()),
            description = food.description!!.toRequestBody("text/plain".toMediaTypeOrNull()),
            categoryId = (food.categoryId ?: "").toRequestBody("text/plain".toMediaTypeOrNull()),
            calories = food.calories!!.toRequestBody("text/plain".toMediaTypeOrNull()),
            tags = food.tags?.takeIf { it.isNotEmpty() }
                ?.map { it.toRequestBody("text/plain".toMediaTypeOrNull()) } ?: emptyList(),
            images = food.images?.takeIf { it.isNotEmpty() }
                ?.map {
                val image = File(it)
                val requestFile = image.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("images", image.name, requestFile)
            } ?: emptyList()
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
