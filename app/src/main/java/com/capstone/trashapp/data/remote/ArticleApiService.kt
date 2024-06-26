package com.capstone.trashapp.data.remote

import com.capstone.trashapp.data.response.HeadlineResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ArticleApiService {

    @GET("/v2/everything")
    suspend fun getHeadlines(
        @Query("q") q: String = "waste",
//        @Query("category") category: String = "health",
//        @Query("language") language: String = "id",
        @Query("apiKey") apiKey: String
    ): HeadlineResponse
}