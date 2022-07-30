package com.example.asteroidradar.util

object Constants {
    const val API_QUERY_DATE_FORMAT = "YYYY-MM-dd"
    const val DEFAULT_END_DATE_DAYS = 7
    const val BASE_URL = "https://api.nasa.gov/"
    val API_KEY: String = System.getenv("API_KEY")!!.toString()
}