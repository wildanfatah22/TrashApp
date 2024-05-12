package com.capstone.trashapp.domain.repository

import com.capstone.trashapp.data.response.PredictResponse
import okhttp3.MultipartBody
import retrofit2.Call


interface PredictRepository {
    fun predictImage(image: MultipartBody.Part): Call<PredictResponse>
}