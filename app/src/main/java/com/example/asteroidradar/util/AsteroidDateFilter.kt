package com.example.asteroidradar.util

sealed class AsteroidDateFilter{
    object ViewWeek : AsteroidDateFilter()
    object ViewToday : AsteroidDateFilter()
    object ViewSaved : AsteroidDateFilter()
}
