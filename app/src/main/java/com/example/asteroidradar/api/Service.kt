package com.example.asteroidradar.api

import com.example.asteroidradar.models.PictureOfDay
import com.example.asteroidradar.util.Constants
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface AsteroidService {
    @GET("neo/rest/v1/feed?api_key=${Constants.API_KEY}")
    suspend fun getFeed(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): String

    @GET("planetary/apod?api_key=${Constants.API_KEY}")
    suspend fun getPictureOfDay(): PictureOfDay
}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

object Network {
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(100, TimeUnit.SECONDS)
        .readTimeout(100, TimeUnit.SECONDS).build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .client(client)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val service: AsteroidService by lazy {
        retrofit.create(AsteroidService::class.java)
    }
}

