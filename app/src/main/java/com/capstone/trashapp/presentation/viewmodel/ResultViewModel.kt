package com.capstone.trashapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.trashapp.data.local.entity.Scan
import com.capstone.trashapp.domain.repository.AppRepository
import com.capstone.trashapp.utils.getFormattedTimestamp
import com.capstone.trashapp.utils.imageFileToByteArray
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    fun saveScan(label: String, confidenceScore: Float, file: File) {
        viewModelScope.launch {
            val blob = imageFileToByteArray(file)
            repository.saveScanToHistory(
                Scan(
                    label = label,
                    confidenceScore = confidenceScore,
                    scanImgBlob = blob,
                    timestamp = Date().getFormattedTimestamp()
                )
            )
        }
    }
}