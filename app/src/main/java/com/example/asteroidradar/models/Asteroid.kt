package com.example.asteroidradar.models

import android.os.Parcelable
import com.example.asteroidradar.database.DatabaseAsteroid
import kotlinx.parcelize.Parcelize


@Parcelize
data class Asteroid(
    val id: Long,
    val codename: String,
    val closeApproachDate: String,
    val absoluteMagnitude: Double,
    val estimatedDiameter: Double,
    val relativeVelocity: Double,
    val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
) : Parcelable

fun List<Asteroid>.asDatabaseAsteroid(): Array<DatabaseAsteroid>
        = map {
    DatabaseAsteroid(
        id = it.id,
        codename = it.codename,
        closeApproachDate = it.closeApproachDate,
        absoluteMagnitude = it.absoluteMagnitude,
        estimatedDiameter = it.estimatedDiameter,
        relativeVelocity = it.relativeVelocity,
        distanceFromEarth = it.distanceFromEarth,
        isPotentiallyHazardous = it.isPotentiallyHazardous
    )
}.toTypedArray()