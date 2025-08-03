package com.example.weatherapp.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.weatherapp.models.WeatherResponse
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherDataManager @Inject constructor(
    @ApplicationContext context: Context,
    private val weatherRepository: WeatherRepository,
    private val gson: Gson
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    fun getLastUpdateTime(): Long {
        return prefs.getLong(KEY_LAST_UPDATE, 0)
    }

    fun shouldRefreshData(): Boolean {
        val lastUpdate = getLastUpdateTime()
        val currentTime = Instant.now().epochSecond
        return currentTime - lastUpdate > UPDATE_INTERVAL
    }

    suspend fun refreshWeatherData(lat: Double, lon: Double): Flow<WeatherResponse> = flow {
        try {
            val response = weatherRepository.getWeatherData(lat, lon, true)
            prefs.edit().putLong(KEY_LAST_UPDATE, Instant.now().epochSecond).apply()
            response.collect { emit(it) }
        } catch (e: Exception) {
            throw e
        }
    }

    fun getCachedTemperatureUnit(): String {
        return prefs.getString(KEY_TEMP_UNIT, "celsius") ?: "celsius"
    }

    fun setTemperatureUnit(unit: String) {
        prefs.edit().putString(KEY_TEMP_UNIT, unit).apply()
    }

    companion object {
        private const val PREFS_NAME = "weather_prefs"
        private const val KEY_LAST_UPDATE = "last_update"
        private const val KEY_TEMP_UNIT = "temp_unit"
        private const val UPDATE_INTERVAL = 1800L // 30 minutes in seconds
    }
}
