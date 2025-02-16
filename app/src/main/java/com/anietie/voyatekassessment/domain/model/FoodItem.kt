package com.anietie.voyatekassessment.domain.model

data class FoodItem(
    val id: Int,
    val categoryId: Int,
    val category: Category,
    val name: String,
    val description: String,
    val calories: String?,
    val images: List<FoodImage>? = emptyList(),
    val tags: List<String>? = emptyList(),
)
