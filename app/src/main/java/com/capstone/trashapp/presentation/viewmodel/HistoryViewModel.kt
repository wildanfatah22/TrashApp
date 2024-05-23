package com.capstone.trashapp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.capstone.trashapp.data.local.entity.Scan
import com.capstone.trashapp.domain.repository.AppRepository
import com.capstone.trashapp.utils.Async
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HistoryViewModel @Inject constructor(private val repository: AppRepository) : ViewModel() {
    private val _history = MutableLiveData<Async<List<Scan>>>()
    val history: LiveData<Async<List<Scan>>>
        get() = _history

    fun fetchScanHistory(handleLoading: Boolean = true) {
        viewModelScope.launch {
            repository.fetchScanHistory(handleLoading).asFlow().collect {
                _history.postValue(it)
            }
        }
    }

    fun deleteScanHistory(scan: Scan) {
        viewModelScope.launch {
            repository.deleteScanHistory(scan)
            fetchScanHistory(false)
        }
    }

    fun saveScanHistory(scan: Scan) {
        viewModelScope.launch {
            repository.saveScanToHistory(scan)
            fetchScanHistory(false)
        }
    }
}