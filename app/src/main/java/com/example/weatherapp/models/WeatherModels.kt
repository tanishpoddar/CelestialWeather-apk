package com.example.weatherapp.models

data class WeatherResponse(
    val current: CurrentWeather,
    val hourly: List<HourlyForecast>,
    val daily: List<DailyForecast>,
    val alerts: List<WeatherAlert>?
)

data class CurrentWeather(
    val temp: Double,
    val feelsLike: Double,
    val humidity: Int,
    val windSpeed: Double,
    val weatherDescription: String,
    val weatherIcon: String
)

data class HourlyForecast(
    val dt: Long,
    val temp: Double,
    val feelsLike: Double,
    val humidity: Int,
    val windSpeed: Double,
    val weatherDescription: String,
    val weatherIcon: String,
    val pop: Double // Probability of precipitation
)

data class DailyForecast(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val tempDay: Double,
    val tempNight: Double,
    val tempMin: Double,
    val tempMax: Double,
    val humidity: Int,
    val windSpeed: Double,
    val weatherDescription: String,
    val weatherIcon: String,
    val pop: Double // Probability of precipitation
)

data class WeatherAlert(
    val senderName: String,
    val event: String,
    val description: String,
    val start: Long,
    val end: Long
)

enum class TemperatureUnit {
    CELSIUS,
    FAHRENHEIT
}
