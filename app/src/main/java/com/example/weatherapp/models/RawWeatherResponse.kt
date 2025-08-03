package com.example.weatherapp.models

// This model should match the OpenWeatherMap API response structure for 'weather' endpoint
// Only include fields you actually use in your mapping

data class RawWeatherResponse(
    val main: RawMain,
    val weather: List<RawWeather>,
    val wind: RawWind?,
    val dt: Long,
    val name: String
)

data class RawMain(
    val temp: Double,
    val feels_like: Double,
    val humidity: Int
)

data class RawWeather(
    val description: String,
    val icon: String
)

data class RawWind(
    val speed: Double
)

