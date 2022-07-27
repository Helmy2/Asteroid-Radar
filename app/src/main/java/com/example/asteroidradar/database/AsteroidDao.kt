package com.example.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.asteroidradar.util.AsteroidDateFilter

@Dao
interface AsteroidDao {
    @Query("SELECT * FROM databaseasteroid ORDER BY close_approach_date")
    suspend fun getAsteroid(): List<DatabaseAsteroid>?

    @Query("SELECT * FROM databaseasteroid WHERE close_approach_date = :today ORDER BY close_approach_date")
    suspend fun getTodayAsteroid(today: String): List<DatabaseAsteroid>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)
}