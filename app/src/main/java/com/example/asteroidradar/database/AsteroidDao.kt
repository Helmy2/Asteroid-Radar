package com.example.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.asteroidradar.util.AsteroidDateFilter

@Dao
interface AsteroidDao {
    @Query("SELECT * FROM DatabaseAsteroid ORDER BY close_approach_date")
    suspend fun getAsteroid(): List<DatabaseAsteroid>?

    @Query("SELECT * FROM DatabaseAsteroid WHERE close_approach_date in (:list) ORDER BY close_approach_date")
    suspend fun getAsteroid(list: List<String>): List<DatabaseAsteroid>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)

    @Query("Delete FROM DatabaseAsteroid WHERE close_approach_date = :date")
    suspend fun deleteAsteroids(date:String)
}