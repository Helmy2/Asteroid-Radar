package com.example.asteroidradar.api

import com.example.asteroidradar.models.PictureOfDay
import com.example.asteroidradar.util.Constants
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

interface AsteroidService {
    @GET("neo/rest/v1/feed?api_key=${Constants.API_KEY}")
    suspend fun getFeed(): String

    @GET("planetary/apod?api_key=${Constants.API_KEY}")
    suspend fun getPictureOfDay(): PictureOfDay
}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

object Network {
    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addConverterFactory(ScalarsConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val service: AsteroidService by lazy {
        retrofit.create(AsteroidService::class.java)
    }
}

