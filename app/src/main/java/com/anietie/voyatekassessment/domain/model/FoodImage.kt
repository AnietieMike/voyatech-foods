package com.anietie.voyatekassessment.domain.model

import com.google.gson.annotations.SerializedName

data class FoodImage(
    val id: Int,
    @SerializedName("image_url") val imageUrl: String
)
