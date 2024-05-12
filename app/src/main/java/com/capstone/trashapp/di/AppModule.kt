package com.capstone.trashapp.di

import com.capstone.trashapp.data.remote.MlApiService
import com.capstone.trashapp.data.repository.PredictRepositoryImpl
import com.capstone.trashapp.domain.repository.PredictRepository
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
    fun providesApiML(): MlApiService {
        val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://127.0.0.1:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(MlApiService::class.java)
    }

    @Provides
    @Singleton
    fun providesPredictRepository(
        mlApiService: MlApiService
    ): PredictRepository{
        return PredictRepositoryImpl(mlApiService)
    }


}