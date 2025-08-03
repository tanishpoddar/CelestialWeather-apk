package com.example.weatherapp.api

import com.example.weatherapp.models.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("onecall")
    suspend fun getWeatherData(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String,
        @Query("exclude") exclude: String = "minutely"
    ): WeatherResponse
}
