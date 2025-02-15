package com.anietie.voyatekassessment.data.remote.dto

data class TagResponse(
    val status: String,
    val message: String,
    val data: List<TagDto>
)
