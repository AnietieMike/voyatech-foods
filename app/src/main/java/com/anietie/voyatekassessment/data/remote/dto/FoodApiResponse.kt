package com.anietie.voyatekassessment.data.remote.dto

data class FoodApiResponse<out T>(val status: String?, val message: String?, val data: T?)
