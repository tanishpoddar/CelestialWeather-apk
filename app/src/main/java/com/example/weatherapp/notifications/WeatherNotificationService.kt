package com.example.weatherapp.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.weatherapp.R
import com.example.weatherapp.models.WeatherAlert
import com.example.weatherapp.models.WeatherResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class WeatherNotificationService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Daily Summary Channel
            NotificationChannel(
                CHANNEL_DAILY_SUMMARY,
                "Daily Weather Summary",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily weather forecasts and summaries"
                notificationManager.createNotificationChannel(this)
            }

            // Severe Weather Channel
            NotificationChannel(
                CHANNEL_SEVERE_WEATHER,
                "Severe Weather Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Important weather alerts and warnings"
                notificationManager.createNotificationChannel(this)
            }
        }
    }

    fun showDailySummary(weather: WeatherResponse) {
        val notification = NotificationCompat.Builder(context, CHANNEL_DAILY_SUMMARY)
            .setSmallIcon(R.drawable.ic_weather)
            .setContentTitle("Today's Weather Summary")
            .setContentText("${weather.current.temp}°C - ${weather.current.weatherDescription}")
            .setStyle(NotificationCompat.BigTextStyle().bigText(
                """
                Current: ${weather.current.temp}°C - ${weather.current.weatherDescription}
                High: ${weather.daily[0].tempMax}°C
                Low: ${weather.daily[0].tempMin}°C
                ${if ((weather.daily[0].pop) > 0) "Precipitation: ${weather.daily[0].pop * 100}%" else ""}
                """.trimIndent()
            ))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(DAILY_SUMMARY_ID, notification)
    }

    fun showSevereWeatherAlert(alert: WeatherAlert) {
        val notification = NotificationCompat.Builder(context, CHANNEL_SEVERE_WEATHER)
            .setSmallIcon(R.drawable.ic_warning)
            .setContentTitle(alert.event)
            .setContentText(alert.description)
            .setStyle(NotificationCompat.BigTextStyle().bigText(alert.description))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(SEVERE_WEATHER_ID, notification)
    }

    companion object {
        private const val CHANNEL_DAILY_SUMMARY = "daily_summary"
        private const val CHANNEL_SEVERE_WEATHER = "severe_weather"
        private const val DAILY_SUMMARY_ID = 1
        private const val SEVERE_WEATHER_ID = 2
    }
}
