package com.anietie.voyatekassessment.data.remote.dto

data class FetchFoodResponse(
    val status: String,
    val message: String,
    val data: List<FoodDto>
)