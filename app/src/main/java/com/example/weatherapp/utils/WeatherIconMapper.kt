package com.example.weatherapp.utils

import com.example.weatherapp.R

object WeatherIconMapper {
    
    fun getWeatherImageResource(weatherDescription: String, iconCode: String): Int {
        val description = weatherDescription.lowercase()
        
        return when {
            // Clear sky conditions
            description.contains("clear") || iconCode.startsWith("01") -> R.drawable.clear
            
            // Cloudy conditions
            description.contains("cloud") || iconCode.startsWith("02") || 
            iconCode.startsWith("03") || iconCode.startsWith("04") -> R.drawable.cloudy
            
            // Rain conditions
            description.contains("rain") || description.contains("drizzle") || 
            iconCode.startsWith("09") || iconCode.startsWith("10") -> {
                if (description.contains("drizzle") || iconCode.startsWith("09")) {
                    R.drawable.drizzle
                } else {
                    R.drawable.rainy
                }
            }
            
            // Snow conditions
            description.contains("snow") || iconCode.startsWith("13") -> R.drawable.snowy
            
            // Thunderstorm conditions
            description.contains("thunder") || description.contains("storm") || 
            iconCode.startsWith("11") -> R.drawable.thunderstorm
            
            // Mist/Fog conditions
            description.contains("mist") || description.contains("fog") || 
            description.contains("haze") || iconCode.startsWith("50") -> R.drawable.mist
            
            // Tornado conditions
            description.contains("tornado") -> R.drawable.tornado
            
            // Default fallback
            else -> R.drawable.clear
        }
    }
    
    fun getWeatherBackgroundResource(weatherDescription: String, iconCode: String): Int {
        val description = weatherDescription.lowercase()
        
        return when {
            // Clear sky conditions
            description.contains("clear") || iconCode.startsWith("01") -> R.drawable.clear
            
            // Cloudy conditions
            description.contains("cloud") || iconCode.startsWith("02") || 
            iconCode.startsWith("03") || iconCode.startsWith("04") -> R.drawable.cloudy
            
            // Rain conditions
            description.contains("rain") || description.contains("drizzle") || 
            iconCode.startsWith("09") || iconCode.startsWith("10") -> {
                if (description.contains("drizzle") || iconCode.startsWith("09")) {
                    R.drawable.drizzle
                } else {
                    R.drawable.rainy
                }
            }
            
            // Snow conditions
            description.contains("snow") || iconCode.startsWith("13") -> R.drawable.snowy
            
            // Thunderstorm conditions
            description.contains("thunder") || description.contains("storm") || 
            iconCode.startsWith("11") -> R.drawable.thunderstorm
            
            // Mist/Fog conditions
            description.contains("mist") || description.contains("fog") || 
            description.contains("haze") || iconCode.startsWith("50") -> R.drawable.mist
            
            // Tornado conditions
            description.contains("tornado") -> R.drawable.tornado
            
            // Default fallback
            else -> R.drawable.clear
        }
    }
} 