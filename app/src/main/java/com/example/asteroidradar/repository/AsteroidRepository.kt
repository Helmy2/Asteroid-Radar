package com.example.asteroidradar.repository

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
import com.example.asteroidradar.util.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val TAG = "AsteroidRepository"

class AsteroidRepository(
    private val database: AsteroidDatabase
) {
    suspend fun getAsteroidList(filter: AsteroidDateFilter): Response<List<Asteroid>> =
        when (filter) {
            AsteroidDateFilter.ViewSaved ->
                getSavedAsteroid()
            AsteroidDateFilter.ViewToday ->
                getTodayAsteroid()
            AsteroidDateFilter.ViewWeek ->
                getWeekAsteroids()
        }


    private suspend fun getSavedAsteroid(): Response<List<Asteroid>> {
        return withContext(Dispatchers.IO) {
            try {
                val data = database.asteroidDao.getAsteroid().asDomainAsteroid()
                Response.Success(data)
            } catch (e: Exception) {
                Log.i(TAG, "getSavedAsteroid: $e")
                Response.Error(e)
            }
        }
    }

    private suspend fun getTodayAsteroid(): Response<List<Asteroid>> {
        return withContext(Dispatchers.IO) {
            val today = formatDay(LocalDateTime.now())
            try {
                val data = database.asteroidDao.getAsteroid(
                    listOf(today)
                ).asDomainAsteroid()
                Response.Success(data)
            } catch (e: Exception) {
                Log.i(TAG, "getTodayAsteroid: $e")
                Response.Error(e)
            }
        }
    }

    private suspend fun getWeekAsteroids(): Response<List<Asteroid>> {
        return withContext(Dispatchers.IO) {
            val sevenDay = getSevenDayFromNawFormatted()
            try {
                val data = database.asteroidDao.getAsteroid(
                    sevenDay
                ).asDomainAsteroid()
                Response.Success(data)
            } catch (e: Exception) {
                Log.i(TAG, "getWeekAsteroids: $e")
                Response.Error(e)
            }
        }
    }

    suspend fun updateFeed(): Response<List<Asteroid>> {
        return withContext(Dispatchers.IO) {
            val startDate = formatDay(LocalDateTime.now())
            val endDate = formatDay(LocalDateTime.now().plusDays(7))
            try {
                val feed = Network.service.getFeed(startDate, endDate)

                val jsonObject = JSONObject(feed)
                val list = parseAsteroidsJsonResult(jsonObject)

                database.asteroidDao.insertAll(
                    *list.asDatabaseAsteroid()
                )

                getSavedAsteroid()
            } catch (e: Exception) {
                Log.i(TAG, "updateAll: $e")
                Response.Error(e)
            }
        }
    }


    suspend fun deleteLastDayDate() {
        withContext(Dispatchers.IO) {
            val lastDay = formatDay(LocalDateTime.now().minusDays(1))
            database.asteroidDao.deleteAsteroids(lastDay)
        }
    }

    suspend fun getPictureOfDay(): Response<PictureOfDay> = try {
        val data = Network.service.getPictureOfDay()
        Response.Success(data)
    } catch (e: Exception) {
        Response.Error(e)
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