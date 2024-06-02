package com.capstone.trashapp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.capstone.trashapp.data.response.Article
import com.capstone.trashapp.domain.repository.AppRepository
import com.capstone.trashapp.utils.Async
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    private val _articles = MutableLiveData<Async<List<Article>>>()
    val articles: LiveData<Async<List<Article>>>
        get() = _articles

    fun fetchHeadlines() {
        viewModelScope.launch {
            repository.fetchWasteHeadlines().asFlow().collect {
                _articles.postValue(it)
            }
        }
    }
}