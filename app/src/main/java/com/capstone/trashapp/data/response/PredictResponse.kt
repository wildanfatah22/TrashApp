package com.capstone.trashapp.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class PredictResponse(

    @field:SerializedName("prediction")
    val prediction: String? = null,

    @field:SerializedName("accuracy")
    val accuracy: Double? = null
) : Parcelable