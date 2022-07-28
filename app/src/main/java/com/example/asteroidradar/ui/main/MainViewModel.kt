package com.example.asteroidradar.ui.main

import android.app.Application
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

private const val TAG = "MainViewModel"

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)

    val asteroidList: MutableLiveData<List<Asteroid>> =
        MutableLiveData(emptyList())

    val errorMassage = asteroidRepository.error

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(true)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    val pictureOfDay: MutableLiveData<PictureOfDay?> =
        MutableLiveData()

    init {
        viewModelScope.launch {
//            asteroidRepository.updateFeed()
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
}