package com.example.asteroidradar

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.asteroidradar.api.Network
import com.example.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidRepository {
    val asteroidList: MutableLiveData<List<Asteroid>> = MutableLiveData()

    suspend fun refreshFeed() {
        var list: List<Asteroid> = emptyList()
        withContext(Dispatchers.IO) {
            try {
                val feed = Network.service.getFeed()
                val jsonObject = JSONObject(feed)
                list = parseAsteroidsJsonResult(jsonObject)
            } catch (e: Exception) {
                Log.d("TAG", "refreshFeed: ${e}")
            }
        }
        asteroidList.value = list
    }
}