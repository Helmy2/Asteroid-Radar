package com.example.asteroidradar

import android.util.Log
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.asteroidradar.api.Network
import com.example.asteroidradar.api.parseAsteroidsJsonResult
import com.example.asteroidradar.database.AsteroidDatabase
import com.example.asteroidradar.database.asDomainAsteroid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidRepository(
    private val database: AsteroidDatabase
) {
    val asteroidList: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroid()) {
            it.asDomainAsteroid()
        }


    suspend fun refreshFeed() {
        var list: List<Asteroid> = emptyList()
        withContext(Dispatchers.IO) {
            try {
                val feed = Network.service.getFeed()
                val jsonObject = JSONObject(feed)
                list = parseAsteroidsJsonResult(jsonObject)
                database.asteroidDao.insertAll(*list.asDatabaseAsteroid())
            } catch (e: Exception) {
                Log.d("TAG", "refreshFeed: ${e}")
            }
        }
    }
}