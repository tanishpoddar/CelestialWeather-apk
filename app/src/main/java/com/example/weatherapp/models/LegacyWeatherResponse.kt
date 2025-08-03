package com.example.weatherapp.models

data class LegacyWeatherResponse(
    val weather: List<LegacyWeather>,
    val main: LegacyMain,
    val wind: LegacyWind,
    val sys: LegacySys?,
    val name: String,
    val coord: LegacyCoord?
)

data class LegacyWeather(
    val description: String,
    val icon: String
)

data class LegacyMain(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val humidity: Int
)

data class LegacyWind(
    val speed: Double
)

data class LegacySys(
    val country: String
)

data class LegacyCoord(
    val lat: Double,
    val lon: Double
)