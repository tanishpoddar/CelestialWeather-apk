package com.example.weatherapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.weatherapp.models.*
import com.example.weatherapp.utils.WeatherUtils

@Composable
fun HourlyForecastItem(
    forecast: HourlyForecast,
    temperatureUnit: TemperatureUnit
) {
    Column(
        modifier = Modifier
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = WeatherUtils.formatTime(forecast.dt),
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = WeatherUtils.formatTemperature(forecast.temp, temperatureUnit),
            style = MaterialTheme.typography.bodyLarge
        )
        if (forecast.pop > 0) {
            Text(
                text = "${forecast.pop * 100}%",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun DailyForecastItem(
    forecast: DailyForecast,
    temperatureUnit: TemperatureUnit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = WeatherUtils.formatDate(forecast.dt),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.3f)
        )
        Text(
            text = forecast.weatherDescription,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.4f),
            textAlign = TextAlign.Center
        )
        Column(
            modifier = Modifier.weight(0.3f),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = WeatherUtils.formatTemperature(forecast.tempMax, temperatureUnit),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = WeatherUtils.formatTemperature(forecast.tempMin, temperatureUnit),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun AlertItem(alert: WeatherAlert) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = alert.event,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = alert.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Valid: ${WeatherUtils.formatDateTime(alert.start)} - ${WeatherUtils.formatDateTime(alert.end)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.6f)
            )
        }
    }
}
