package com.anietie.voyatekassessment.data.remote.dto

import com.anietie.voyatekassessment.domain.model.Category
import com.google.gson.annotations.SerializedName

data class CategoryDto(
    val description: String,
    val id: Int,
    val name: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
) {
    fun toDomain(): Category {
         return Category(
            description = description,
            id = id,
            name = name
        )
    }
}
