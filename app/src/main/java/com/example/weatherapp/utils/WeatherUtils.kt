package com.example.weatherapp.utils

import com.example.weatherapp.models.TemperatureUnit
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object WeatherUtils {
    fun convertTemperature(celsius: Double, unit: TemperatureUnit): Double {
        return when (unit) {
            TemperatureUnit.CELSIUS -> celsius
            TemperatureUnit.FAHRENHEIT -> (celsius * 9/5) + 32
        }
    }

    fun formatTemperature(celsius: Double, unit: TemperatureUnit): String {
        val temp = convertTemperature(celsius, unit)
        val symbol = when (unit) {
            TemperatureUnit.CELSIUS -> "°C"
            TemperatureUnit.FAHRENHEIT -> "°F"
        }
        return "${temp.toInt()}$symbol"
    }

    fun formatDateTime(timestamp: Long): String {
        val dateTime = LocalDateTime.ofInstant(
            Instant.ofEpochSecond(timestamp),
            ZoneId.systemDefault()
        )
        return dateTime.format(DateTimeFormatter.ofPattern("MMM dd, HH:mm"))
    }

    fun formatTime(timestamp: Long): String {
        val dateTime = LocalDateTime.ofInstant(
            Instant.ofEpochSecond(timestamp),
            ZoneId.systemDefault()
        )
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    fun formatDate(timestamp: Long): String {
        val dateTime = LocalDateTime.ofInstant(
            Instant.ofEpochSecond(timestamp),
            ZoneId.systemDefault()
        )
        return dateTime.format(DateTimeFormatter.ofPattern("EEE, MMM dd"))
    }

    fun isDaytime(): Boolean {
        val hour = LocalDateTime.now().hour
        return hour in 6..18
    }

    fun getWeatherAnimation(weatherCode: String): String {
        return when {
            weatherCode.contains("rain", ignoreCase = true) -> "rainy_day.json"
            weatherCode.contains("snow", ignoreCase = true) -> "snowy_day.json"
            weatherCode.contains("cloud", ignoreCase = true) -> "cloudy_day.json"
            else -> if (isDaytime()) "sunny_day.json" else "night_sky.json"
        }
    }

    fun getBackgroundColor(weatherCode: String, isDark: Boolean): Int {
        // Return color resource IDs based on weather condition and theme
        return when {
            weatherCode.contains("rain", ignoreCase = true) -> if (isDark) 0xFF0A1929 else 0xFFE3F2FD
            weatherCode.contains("snow", ignoreCase = true) -> if (isDark) 0xFF263238 else 0xFFECEFF1
            weatherCode.contains("cloud", ignoreCase = true) -> if (isDark) 0xFF1A237E else 0xFFE8EAF6
            else -> if (isDark) 0xFF121212 else 0xFFF5F5F5
        }.toInt()
    }
}
