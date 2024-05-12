package com.capstone.trashapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.trashapp.domain.repository.PredictRepository
import com.capstone.trashapp.utils.imageFileToByteArray
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val predictRepository: PredictRepository
) : ViewModel() {

    fun saveScan(label: String, confidenceScore: Int, file: File) {
        viewModelScope.launch {
            val blob = imageFileToByteArray(file)
//            predictRepository.saveScanToHistory(
//                Scan(
//                    label = label,
//                    confidenceScore = confidenceScore,
//                    scanImgBlob = blob,
//                    timestamp = Date().getFormattedTimestamp()
//                )
//            )
        }
    }
}