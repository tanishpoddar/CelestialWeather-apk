package com.example.weatherapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.weatherapp.models.HourlyForecast
import com.example.weatherapp.models.TemperatureUnit
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun HourlyForecastItem(
    forecast: HourlyForecast,
    unit: TemperatureUnit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .width(72.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = formatTime(forecast.dt),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formatTemperature(forecast.temp, unit),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "${(forecast.pop * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatTemperature(temp: Double, unit: TemperatureUnit): String {
    return when (unit) {
        TemperatureUnit.CELSIUS -> "${temp.toInt()}°C"
        TemperatureUnit.FAHRENHEIT -> "${(temp * 9/5 + 32).toInt()}°F"
    }
}

private fun formatTime(timestamp: Long): String {
    val dateTime = LocalDateTime.ofInstant(
        Instant.ofEpochSecond(timestamp),
        ZoneId.systemDefault()
    )
    return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
}
