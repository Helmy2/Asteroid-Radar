package com.example.asteroidradar.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.asteroidradar.models.Asteroid

@Entity
data class DatabaseAsteroid(
    @PrimaryKey
    val id: Long,
    val codename: String,
    @ColumnInfo("close_approach_date")
    val closeApproachDate: String,
    @ColumnInfo("absolute_magnitude")
    val absoluteMagnitude: Double,
    @ColumnInfo("estimated_diameter")
    val estimatedDiameter: Double,
    @ColumnInfo("relative_velocity")
    val relativeVelocity: Double,
    @ColumnInfo("distance_from_earth")
    val distanceFromEarth: Double,
    @ColumnInfo("is_potentially_hazardous")
    val isPotentiallyHazardous: Boolean
)

fun List<DatabaseAsteroid>.asDomainAsteroid(): List<Asteroid> = map {
    Asteroid(
        id = it.id,
        codename = it.codename,
        closeApproachDate = it.closeApproachDate,
        absoluteMagnitude = it.absoluteMagnitude,
        estimatedDiameter = it.estimatedDiameter,
        relativeVelocity = it.relativeVelocity,
        distanceFromEarth = it.distanceFromEarth,
        isPotentiallyHazardous = it.isPotentiallyHazardous
    )
}