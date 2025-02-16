package com.anietie.voyatekassessment.data.remote.dto

import com.anietie.voyatekassessment.domain.model.FoodImage
import com.anietie.voyatekassessment.domain.model.FoodItem
import com.google.gson.annotations.SerializedName

data class FoodDto(
    val id: Int,
    val name: String,
    val description: String,
    @SerializedName("category_id") val categoryId: Int,
    val calories: Int,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    val foodTags: List<String>,
    val foodImages: List<FoodImage>,
    val category: CategoryDto,
) {
    fun toDomain(): FoodItem =
        FoodItem(
            id = id,
            name = name,
            description = description,
            categoryId = categoryId,
            calories = calories.toString(),
            images = foodImages,
            tags = foodTags,
            category = category.toDomain(),
        )
}
