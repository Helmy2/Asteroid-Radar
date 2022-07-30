package com.example.asteroidradar.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.asteroidradar.database.getDatabase
import com.example.asteroidradar.models.Asteroid
import com.example.asteroidradar.models.PictureOfDay
import com.example.asteroidradar.repository.AsteroidRepository
import com.example.asteroidradar.util.AsteroidDateFilter
import com.example.asteroidradar.util.Response
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private const val TAG = "MainViewModel"

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)
    private val eventChannel = Channel<String>()

    var errorFlow = eventChannel.receiveAsFlow()
        private set

    private val _asteroidList: MutableLiveData<List<Asteroid>> = MutableLiveData(emptyList())
    val asteroidList: LiveData<List<Asteroid>>
        get() = _asteroidList

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(true)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _isSwipeRefreshLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isSwipeRefreshLoading: LiveData<Boolean>
        get() = _isSwipeRefreshLoading

    val pictureOfDay: MutableLiveData<PictureOfDay> =
        MutableLiveData()

    init {
        viewModelScope.launch {
            _isLoading.value = true
            updateFeed()
            updatePictureOfDay()
            _isLoading.value = false
        }
        filterAsteroidList(AsteroidDateFilter.ViewSaved)
    }

    fun updateAll() {
        viewModelScope.launch {
            _isSwipeRefreshLoading.value = true
            updatePictureOfDay()
            updateFeed()
            _isSwipeRefreshLoading.value = false
        }
    }

    fun filterAsteroidList(filter: AsteroidDateFilter) {
        viewModelScope.launch {
            val response = asteroidRepository.getAsteroidList(filter)
            if (response is Response.Success)
                _asteroidList.value = response.data
            else {
                Log.i(TAG, "filterAsteroidList: ${response.error?.localizedMessage}")
                eventChannel.send(response.error?.localizedMessage ?: "")
            }
        }
    }

    private suspend fun updateFeed() {
        val response = asteroidRepository.updateFeed()
        if (response is Response.Success)
            _asteroidList.value = response.data
        else {
            Log.i(TAG, "updateAll: ${response.error?.message}")
            eventChannel.send(response.error?.localizedMessage ?: "")
        }
    }

    private suspend fun updatePictureOfDay() {
        val response = asteroidRepository.getPictureOfDay()
        if (response is Response.Success)
            pictureOfDay.value = response.data
        else {
            Log.i(TAG, "updatePictureOfDay: ${response.error?.message}")
            eventChannel.send(response.error?.localizedMessage ?: "")
        }
    }
}