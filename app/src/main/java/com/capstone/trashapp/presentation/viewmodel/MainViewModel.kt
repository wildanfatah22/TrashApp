package com.capstone.trashapp.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.trashapp.data.response.PredictResponse
import com.capstone.trashapp.domain.repository.PredictRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val predictRepository: PredictRepository
) : ViewModel() {

    private val _classificationResult = MutableLiveData<PredictResponse>()
    val classificationResult: LiveData<PredictResponse> = _classificationResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun classifyImage(imageUri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true

            predictRepository.classifyImage(imageUri).observeForever { result ->
                _isLoading.value = false
                result.fold(
                    onSuccess = {
                        _classificationResult.value = it
                    },
                    onFailure = {
                        // Handle error
                    }
                )
            }
        }
    }
}