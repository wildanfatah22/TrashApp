package com.capstone.trashapp.domain.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import com.capstone.trashapp.data.response.PredictResponse
import okhttp3.MultipartBody
import retrofit2.Call


interface PredictRepository {
    fun predictImage(image: MultipartBody.Part): Call<PredictResponse>

    fun classifyImage(imageUri: Uri): LiveData<Result<PredictResponse>>
}