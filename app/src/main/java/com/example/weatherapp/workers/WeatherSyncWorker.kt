package com.example.weatherapp.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.example.weatherapp.notifications.WeatherNotificationService
import com.example.weatherapp.repository.WeatherDataManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

@HiltWorker
class WeatherSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val weatherDataManager: WeatherDataManager,
    private val notificationService: WeatherNotificationService
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val lat = inputData.getDouble(KEY_LATITUDE, DEFAULT_LAT)
            val lon = inputData.getDouble(KEY_LONGITUDE, DEFAULT_LON)

            // Only refresh if needed or forced
            if (weatherDataManager.shouldRefreshData()) {
                val weatherResponse = weatherDataManager.refreshWeatherData(lat, lon).first()

                // Show daily summary notification
                notificationService.showDailySummary(weatherResponse)

                // Check and show severe weather alerts if any
                weatherResponse.alerts?.forEach { alert ->
                    notificationService.showSevereWeatherAlert(alert)
                }
            }

            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    companion object {
        private const val DEFAULT_LAT = 0.0
        private const val DEFAULT_LON = 0.0
        const val KEY_LATITUDE = "latitude"
        const val KEY_LONGITUDE = "longitude"

        fun setupPeriodicSync(workManager: WorkManager, lat: Double, lon: Double) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val inputData = workDataOf(
                KEY_LATITUDE to lat,
                KEY_LONGITUDE to lon
            )

            val request = PeriodicWorkRequestBuilder<WeatherSyncWorker>(
                30, TimeUnit.MINUTES,
                15, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setInputData(inputData)
                .build()

            workManager.enqueueUniquePeriodicWork(
                "weather_sync",
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }
    }
}
