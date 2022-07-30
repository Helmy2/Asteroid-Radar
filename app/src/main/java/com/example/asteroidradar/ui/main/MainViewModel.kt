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
import kotlinx.coroutines.launch
import kotlin.reflect.jvm.internal.impl.load.kotlin.PackagePartProvider

private const val TAG = "MainViewModel"

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)

    val asteroidList: MutableLiveData<List<Asteroid>> =
        MutableLiveData(emptyList())

    val errorMassage = MutableLiveData<String>()

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(true)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _isSwipeRefreshLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isSwipeRefreshLoading: LiveData<Boolean>
        get() = _isSwipeRefreshLoading

    val pictureOfDay: MutableLiveData<PictureOfDay?> =
        MutableLiveData()

    init {
        viewModelScope.launch {
            pictureOfDay.value = asteroidRepository.getPictureOfDay()
            asteroidList.value =
                asteroidRepository.getAsteroidList(AsteroidDateFilter.ViewSaved)
            _isLoading.value = false
        }
    }

    fun filterAsteroidList(filter: AsteroidDateFilter) {
        viewModelScope.launch {
            asteroidList.value =
                asteroidRepository.getAsteroidList(filter)
        }
    }

    fun updateFeed() {
        viewModelScope.launch {
            _isSwipeRefreshLoading.value = true
            asteroidRepository.updateFeed()
            pictureOfDay.value = asteroidRepository.getPictureOfDay()
            asteroidList.value =
                asteroidRepository.getAsteroidList(AsteroidDateFilter.ViewSaved)
            _isSwipeRefreshLoading.value = false
        }
    }
}