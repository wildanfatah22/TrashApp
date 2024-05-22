package com.capstone.trashapp.data.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.capstone.trashapp.data.remote.MlApiService
import com.capstone.trashapp.data.response.PredictResponse
import com.capstone.trashapp.domain.repository.PredictRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class PredictRepositoryImpl @Inject constructor(
    private val mlApiService: MlApiService
) : PredictRepository {
    override fun predictImage(image: MultipartBody.Part): Call<PredictResponse> {
        return mlApiService.predictImage(image)
    }

    override fun classifyImage(imageUri: Uri): LiveData<Result<PredictResponse>> {
        val result = MutableLiveData<Result<PredictResponse>>()

        val file = File(imageUri.path!!)
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

        mlApiService.predictImage(body).enqueue(object : Callback<PredictResponse> {
            override fun onResponse(call: Call<PredictResponse>, response: Response<PredictResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        result.postValue(Result.success(it))
                    } ?: run {
                        result.postValue(Result.failure(Exception("Response body is null")))
                    }
                } else {
                    result.postValue(Result.failure(Exception("Failed to get classification result")))
                }
            }

            override fun onFailure(call: Call<PredictResponse>, t: Throwable) {
                result.postValue(Result.failure(t))
            }
        })

        return result
    }
}