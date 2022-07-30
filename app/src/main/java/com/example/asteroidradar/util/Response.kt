package com.example.asteroidradar.util

sealed class Response<T>(
    val data: T? = null,
    val error: Throwable? = null
) {
    class Success<T>(data: T) : Response<T>(data)
    class Loading<T>(data: T? = null) : Response<T>(data)
    class Error<T>(throwable: Throwable, data: T? = null) : Response<T>(data, throwable)
}