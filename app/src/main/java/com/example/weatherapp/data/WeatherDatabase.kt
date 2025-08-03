package com.example.weatherapp.data

import androidx.room.*
import com.example.weatherapp.models.*

@Entity(tableName = "weather_data")
data class WeatherEntity(
    @PrimaryKey
    val id: Int = 0,
    val currentTemp: Double,
    val currentFeelsLike: Double,
    val currentWeatherDescription: String,
    val lastUpdated: Long,
    val hourlyForecast: String, // JSON string of hourly forecasts
    val dailyForecast: String,  // JSON string of daily forecasts
    val alerts: String?         // JSON string of alerts
)

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather_data WHERE id = 0")
    suspend fun getWeatherData(): WeatherEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherData(weather: WeatherEntity)

    @Query("DELETE FROM weather_data")
    suspend fun clearWeatherData()
}

@Database(entities = [WeatherEntity::class], version = 1, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}
