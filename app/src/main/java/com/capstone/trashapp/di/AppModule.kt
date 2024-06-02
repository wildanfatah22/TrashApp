package com.capstone.trashapp.di

import android.app.Application
import androidx.room.Room
import com.capstone.trashapp.BuildConfig
import com.capstone.trashapp.data.local.database.AppDatabase
import com.capstone.trashapp.data.remote.ArticleApiService
import com.capstone.trashapp.data.repository.AppRepositoryImpl
import com.capstone.trashapp.domain.repository.AppRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun providesArticleApi(): ArticleApiService {
        val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL
            )
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(ArticleApiService::class.java)
    }

    @Provides
    @Singleton
    fun providesDatabase(application: Application): AppDatabase {
        return Room
            .databaseBuilder(
                application, AppDatabase::class.java, "TrashApp.db"
            )
            .build()
    }

    @Provides
    @Singleton
    fun providesAppRepository(
        articleApi: ArticleApiService, database: AppDatabase
    ): AppRepository {
        return AppRepositoryImpl(articleApi, database)
    }

}