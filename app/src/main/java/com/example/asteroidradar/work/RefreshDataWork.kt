package com.example.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.asteroidradar.database.getDatabase
import com.example.asteroidradar.repository.AsteroidRepository
import com.example.asteroidradar.util.Response
import retrofit2.HttpException

private const val TAG = "RefreshDataWork"

class RefreshDataWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = AsteroidRepository(database)
        return try {
            val response = repository.updateFeed()
            repository.deleteLastDayDate()
            if (response is Response.Success)
                Result.success()
            else
                Result.retry()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}
