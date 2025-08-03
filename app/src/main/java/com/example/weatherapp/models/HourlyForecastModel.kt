package com.example.weatherapp.models

data class HourlyForecastModel(
    val time: String,
    val temperature: String,
    val icon: String,
    val precipitationProbability: String
)