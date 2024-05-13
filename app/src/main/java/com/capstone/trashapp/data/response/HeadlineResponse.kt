package com.capstone.trashapp.data.response

import com.google.gson.annotations.SerializedName


data class HeadlineResponse(

    @field:SerializedName("totalResults")
    val totalResults: Int? = null,

    @field:SerializedName("articles")
    val articles: List<Article>,

    @field:SerializedName("status")
    val status: String
)


