package com.example.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.example.asteroidradar.Asteroid
import com.example.asteroidradar.AsteroidRepository
import com.example.asteroidradar.database.getDatabase
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)

    val asteroidList: LiveData<List<Asteroid>> = asteroidRepository.asteroidList

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(true)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    init {
        viewModelScope.launch {
            asteroidRepository.refreshFeed()
            _isLoading.value = false
        }
    }
}