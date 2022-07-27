package com.example.asteroidradar

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
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
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class AsteroidRepository(
    private val database: AsteroidDatabase
) {
//    val asteroidList: LiveData<List<Asteroid>> =
//        Transformations.map(database.asteroidDao.getAsteroid()) {
//            it.asDomainAsteroid()
//        }

    suspend fun getAsteroidList(filter: AsteroidDateFilter): List<Asteroid>? =
        try {
            when (filter) {
                AsteroidDateFilter.ViewSaved ->
                    database.asteroidDao.getAsteroid()?.asDomainAsteroid()
                AsteroidDateFilter.ViewToday -> {
                    val dateFormat = DateTimeFormatter.ofPattern(Constants.API_QUERY_DATE_FORMAT)
                    val currentDate = LocalDateTime.now().format(dateFormat)
                    database.asteroidDao.getTodayAsteroid(currentDate)?.asDomainAsteroid()
                }
                AsteroidDateFilter.ViewWeek -> {
                    val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT)
                    val currentDate = dateFormat.format(Date())
                    Log.i("TAG", "getAsteroidList: $currentDate")
                    database.asteroidDao.getTodayAsteroid(currentDate)?.asDomainAsteroid()
                }
            }
        } catch (e: Exception) {
            Log.d("TAG", "refreshFeed: ${e}")
            null
        }

    suspend fun updateFeed() {
        withContext(Dispatchers.IO) {
            try {
                val feed = Network.service.getFeed()
                val jsonObject = JSONObject(feed)
                val list = parseAsteroidsJsonResult(jsonObject)
                database.asteroidDao.insertAll(
                    *list.asDatabaseAsteroid()
                )
            } catch (e: Exception) {
                Log.d("TAG", "updateFeed: ${e}")
            }
        }
    }

    suspend fun getPictureOfDay(): PictureOfDay? = try {
        Network.service.getPictureOfDay()
    } catch (e: Exception) {
        Log.d("TAG", "getPictureOfDay: ${e}")
        null
    }
}