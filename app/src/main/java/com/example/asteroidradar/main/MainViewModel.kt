package com.example.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.asteroidradar.Asteroid
import com.example.asteroidradar.AsteroidRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _asteroidList: MutableLiveData<List<Asteroid>> = MutableLiveData()
    val asteroidList: LiveData<List<Asteroid>>
        get() = _asteroidList

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(true)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    init {
        val asteroidRepository = AsteroidRepository()
        viewModelScope.launch {
            asteroidRepository.refreshFeed()
            _asteroidList.value = asteroidRepository.asteroidList.value
            _isLoading.value = false
        }
    }
}