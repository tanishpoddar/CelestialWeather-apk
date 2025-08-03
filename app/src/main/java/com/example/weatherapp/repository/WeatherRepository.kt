package com.example.weatherapp.repository

import com.example.weatherapp.api.WeatherApi
import com.example.weatherapp.data.WeatherDao
import com.example.weatherapp.data.WeatherEntity
import com.example.weatherapp.models.CurrentWeather
import com.example.weatherapp.models.DailyForecast
import com.example.weatherapp.models.HourlyForecast
import com.example.weatherapp.models.WeatherAlert
import com.example.weatherapp.models.WeatherResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherApi: WeatherApi,
    private val weatherDao: WeatherDao,
    private val gson: Gson
) {
    fun getWeatherData(lat: Double, lon: Double, forceRefresh: Boolean = false) = flow {
        // First, emit cached data if available
        weatherDao.getWeatherData()?.let { entity ->
            emit(convertEntityToResponse(entity))
        }

        // Then fetch fresh data if needed
        if (forceRefresh || shouldRefreshData()) {
            try {
                val weatherData = weatherApi.getWeatherData(lat, lon, "metric", API_KEY)
                weatherDao.insertWeatherData(convertResponseToEntity(weatherData))
                emit(weatherData)
            } catch (e: Exception) {
                throw e
            }
        }
    }

    private fun shouldRefreshData(): Boolean {
        return true
    }

    private fun convertEntityToResponse(entity: WeatherEntity): WeatherResponse {
        val hourly = gson.fromJson(entity.hourlyForecast, Array<HourlyForecast>::class.java).toList()
        val daily = gson.fromJson(entity.dailyForecast, Array<DailyForecast>::class.java).toList()
        val alerts = entity.alerts?.let { gson.fromJson(it, Array<WeatherAlert>::class.java).toList() }

        val current = CurrentWeather(
            temp = entity.currentTemp,
            feelsLike = entity.currentFeelsLike,
            humidity = 0,
            windSpeed = 0.0,
            weatherDescription = entity.currentWeatherDescription,
            weatherIcon = ""
        )
        return WeatherResponse(
            current = current,
            hourly = hourly,
            daily = daily,
            alerts = alerts
        )
    }

    private fun convertResponseToEntity(response: WeatherResponse): WeatherEntity {
        return WeatherEntity(
            currentTemp = response.current.temp,
            currentFeelsLike = response.current.feelsLike,
            currentWeatherDescription = response.current.weatherDescription,
            lastUpdated = System.currentTimeMillis(),
            hourlyForecast = gson.toJson(response.hourly),
            dailyForecast = gson.toJson(response.daily),
            alerts = response.alerts?.let { gson.toJson(it) }
        )
    }

    companion object {
        private const val API_KEY = "cbf26d51498f81728fd1bb69df49fa59"
    }
}
