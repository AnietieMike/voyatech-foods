package com.anietie.voyatekassessment.data.remote.dto

import com.anietie.voyatekassessment.domain.model.Category

data class CategoryResponse(
    val status: String,
    val message: String,
    val data: List<CategoryDto>
)