package com.anietie.voyatekassessment.data.remote.dto

import com.anietie.voyatekassessment.domain.model.FoodItem

data class FoodDto(
    val categoryId: String,
    val name: String,
    val description: String,
    val calories: String,
    val images: List<String> = emptyList(),
    val tags: List<String> = emptyList()
) {
    fun toDomain(): FoodItem {
        return FoodItem(
            categoryId = categoryId,
            name = name,
            description = description,
            calories = calories,
            images = images,
            tags = tags
        )
    }
}
