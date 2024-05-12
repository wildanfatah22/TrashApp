package com.capstone.trashapp.data.repository

import com.capstone.trashapp.data.remote.MlApiService
import com.capstone.trashapp.data.response.PredictResponse
import com.capstone.trashapp.domain.repository.PredictRepository
import okhttp3.MultipartBody
import retrofit2.Call
import javax.inject.Inject

class PredictRepositoryImpl @Inject constructor(
    private val mlApiService: MlApiService
) : PredictRepository {
    override fun predictImage(image: MultipartBody.Part): Call<PredictResponse> {
        return mlApiService.predictImage(image)
    }
}