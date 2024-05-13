package com.capstone.trashapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.capstone.trashapp.BuildConfig
import com.capstone.trashapp.data.local.database.AppDatabase
import com.capstone.trashapp.data.local.entity.Scan
import com.capstone.trashapp.data.remote.ArticleApiService
import com.capstone.trashapp.data.response.Article
import com.capstone.trashapp.domain.repository.AppRepository
import com.capstone.trashapp.utils.Async

class AppRepositoryImpl(
    private val articleApi: ArticleApiService,
    private val database: AppDatabase
): AppRepository {

    override suspend fun fetchCancerHeadlines(): LiveData<Async<List<Article>>> =
        liveData {
            emit(Async.Loading)
            try {
                val response = articleApi.getHeadlines(apiKey = BuildConfig.API_KEY)
                if (response.articles.isEmpty() || response.totalResults == 0) {
                    emit(Async.Empty)
                } else {
                    emit(Async.Success(response.articles.filter { article -> article.title != "[Removed]" }))
                }
            } catch (e: Exception) {
                emit(Async.Error(e.message.toString()))
            }
        }

    override suspend fun fetchScanHistory(handleLoading: Boolean): LiveData<Async<List<Scan>>> =
        liveData {
            if (handleLoading) {
                emit(Async.Loading)
            }
            try {
                val history = database.scanDao().getALl()
                if (history.isEmpty()) {
                    emit(Async.Empty)
                } else {
                    emit(Async.Success(history))
                }
            } catch (e: Exception) {
                emit(Async.Error(e.message.toString()))
            }
        }

    override suspend fun saveScanToHistory(scan: Scan) {
        database.scanDao().insertScan(scan)
    }

    override suspend fun deleteScanHistory(scan: Scan) {
        database.scanDao().deleteScan(scan)
    }
}