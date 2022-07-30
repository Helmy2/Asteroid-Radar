package com.example.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
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
import kotlinx.coroutines.flow.MutableSharedFlow
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
            val today = formatDay(LocalDateTime.now())
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

    suspend fun updateFeed(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val startDate = formatDay(LocalDateTime.now())
                val endDate = formatDay(LocalDateTime.now().plusDays(7))

                val feed = Network.service.getFeed(startDate, endDate)

                val jsonObject = JSONObject(feed)
                val list = parseAsteroidsJsonResult(jsonObject)

                database.asteroidDao.insertAll(
                    *list.asDatabaseAsteroid()
                )
                return@withContext true
            } catch (e: Exception) {
                Log.i(TAG, "updateFeed: $e")
                return@withContext false
            }
        }
    }

    suspend fun deleteLastDayDate() {
        withContext(Dispatchers.IO) {
            val lastDay = formatDay(LocalDateTime.now().minusDays(1))
            database.asteroidDao.deleteAsteroids(lastDay)
        }
    }

    suspend fun getPictureOfDay(): PictureOfDay? = try {
        Network.service.getPictureOfDay()
    } catch (e: Exception) {
        Log.i(TAG, "getPictureOfDay: $e")
        null
    }

    private fun getSevenDayFromNawFormatted(): MutableList<String> {
        val nowTime = LocalDateTime.now()
        val list = mutableListOf<String>()
        repeat(7) {
            list.add(formatDay(nowTime.plusDays(it.toLong())))
        }
        return list
    }

    private fun formatDay(
        date: LocalDateTime
    ): String {
        val dateFormat =
            DateTimeFormatter.ofPattern(Constants.API_QUERY_DATE_FORMAT)
        return date.format(dateFormat)
    }
}