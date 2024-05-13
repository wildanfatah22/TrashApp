package com.capstone.trashapp.domain.repository

import androidx.lifecycle.LiveData
import com.capstone.trashapp.data.local.entity.Scan
import com.capstone.trashapp.data.response.Article
import com.capstone.trashapp.utils.Async

interface AppRepository {
    suspend fun fetchCancerHeadlines(): LiveData<Async<List<Article>>>
    suspend fun fetchScanHistory(handleLoading: Boolean): LiveData<Async<List<Scan>>>
    suspend fun saveScanToHistory(scan: Scan)
    suspend fun deleteScanHistory(scan: Scan)
}