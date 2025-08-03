package com.example.weatherapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.weatherapp.models.LegacyWeatherResponse as WeatherResponse

interface WeatherServiceApi {
    @GET("weather")
    fun getWeatherDetails(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") appId: String,
        @Query("units") units: String
    ): Call<WeatherResponse>

    @GET("weather")
    fun getWeatherByCity(
        @Query("q") cityName: String,
        @Query("appid") appId: String,
        @Query("units") units: String
    ): Call<WeatherResponse>
}
