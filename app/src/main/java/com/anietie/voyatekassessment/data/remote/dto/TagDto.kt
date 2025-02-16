package com.anietie.voyatekassessment.data.remote.dto

import com.anietie.voyatekassessment.domain.model.Tag
import com.google.gson.annotations.SerializedName

data class TagDto(
    val id: Int,
    val name: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
) {
    fun toDomain(): Tag =
        Tag(
            id = id.toString(),
            name = name,
        )
}
