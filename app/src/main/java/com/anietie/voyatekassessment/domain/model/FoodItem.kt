package com.anietie.voyatekassessment.domain.model

data class FoodItem(
    val categoryId: String,
    val name: String,
    val description: String,
    val calories: String,
    val images: List<String> = emptyList(),
    val tags: List<String> = emptyList()
)