package com.example.asteroidradar

import android.util.Log
import com.example.asteroidradar.api.Network
import com.example.asteroidradar.api.parseAsteroidsJsonResult
import com.example.asteroidradar.database.AsteroidDatabase
import com.example.asteroidradar.database.asDomainAsteroid
import com.example.asteroidradar.models.Asteroid
import com.example.asteroidradar.models.PictureOfDay
import com.example.asteroidradar.models.asDatabaseAsteroid
import com.example.asteroidradar.util.AsteroidDateFilter
import com.example.asteroidradar.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val TAG = "AsteroidRepository"

class AsteroidRepository(
    private val database: AsteroidDatabase
) {
    suspend fun getAsteroidList(filter: AsteroidDateFilter): List<Asteroid>? =
        when (filter) {
            AsteroidDateFilter.ViewSaved ->
                getSavedAsteroid()
            AsteroidDateFilter.ViewToday ->
                getTodayAsteroid()
            AsteroidDateFilter.ViewWeek ->
                getWeekAsteroids()
        }


    private suspend fun getSavedAsteroid(): List<Asteroid>? {
        return withContext(Dispatchers.IO) {
            try {
                database.asteroidDao.getAsteroid()?.asDomainAsteroid()
            } catch (e: Exception) {
                Log.i(TAG, "getSavedAsteroid: $e")
                null
            }
        }
    }

    private suspend fun getTodayAsteroid(): List<Asteroid>? {
        return withContext(Dispatchers.IO) {
            val today = getTodayFormatted()
            try {
                database.asteroidDao.getAsteroid(
                    listOf(today)
                )?.asDomainAsteroid()
            } catch (e: Exception) {
                Log.i(TAG, "getTodayAsteroid: $e")
                null
            }
        }
    }

    private suspend fun getWeekAsteroids(): List<Asteroid>? {
        return withContext(Dispatchers.IO) {
            val sevenDay = getSevenDayFromNawFormatted()
            try {
                database.asteroidDao.getAsteroid(
                    sevenDay
                )?.asDomainAsteroid()
            } catch (e: Exception) {
                Log.i(TAG, "getWeekAsteroids: $e")
                null
            }
        }
    }

    suspend fun updateFeed() {
        withContext(Dispatchers.IO) {
            try {
                val dateFormat =
                    DateTimeFormatter.ofPattern(Constants.API_QUERY_DATE_FORMAT)
                val startDate = LocalDateTime.now().format(dateFormat)
                val endDate = LocalDateTime.now().plusDays(7).format(dateFormat)
                val feed = Network.service.getFeed(startDate, endDate)
                val jsonObject = JSONObject(feed)
                val list = parseAsteroidsJsonResult(jsonObject)
                database.asteroidDao.insertAll(
                    *list.asDatabaseAsteroid()
                )
            } catch (e: Exception) {
                Log.i(TAG, "updateFeed: $e")
            }
        }
    }

    suspend fun getPictureOfDay(): PictureOfDay? = try {
        Network.service.getPictureOfDay()
    } catch (e: Exception) {
        Log.i(TAG, "getPictureOfDay: $e")
        null
    }

    private fun getSevenDayFromNawFormatted(): MutableList<String> {
        val dateFormat =
            DateTimeFormatter.ofPattern(Constants.API_QUERY_DATE_FORMAT)
        val nowTime = LocalDateTime.now()
        val list = mutableListOf<String>()
        repeat(7) {
            list.add(nowTime.plusDays(it.toLong()).format(dateFormat))
        }
        return list
    }

    private fun getTodayFormatted(): String {
        val dateFormat =
            DateTimeFormatter.ofPattern(Constants.API_QUERY_DATE_FORMAT)
        return LocalDateTime.now().format(dateFormat)
    }
}