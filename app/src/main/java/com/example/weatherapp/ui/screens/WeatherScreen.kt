package com.example.weatherapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.example.weatherapp.R
import com.example.weatherapp.models.*
import com.example.weatherapp.ui.components.*
import com.example.weatherapp.viewmodels.WeatherViewModel
import com.example.weatherapp.viewmodels.WeatherUiState
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val temperatureUnit by viewModel.temperatureUnit.collectAsState()

    val backgroundAnimation = if (isDarkTheme) "night_sky.json" else "sunny_day.json"
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset(backgroundAnimation)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Weather animations using Lottie
        LottieAnimation(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            composition = composition,
            progress = { progress }
        )

        when (uiState) {
            is WeatherUiState.Success -> {
                val weather = (uiState as WeatherUiState.Success).weather
                CurrentWeatherSection(weather.current)
                HourlyForecastSection(weather.hourly)
                DailyForecastSection(weather.daily)
                if (!weather.alerts.isNullOrEmpty()) {
                    AlertsSection(weather.alerts)
                }
            }
            is WeatherUiState.Loading -> {
                LoadingIndicator()
            }
            is WeatherUiState.Error -> {
                ErrorDisplay(
                    message = (uiState as WeatherUiState.Error).message,
                    onRetry = { viewModel.refreshWeatherData() }
                )
            }
            else -> { /* Initial state, do nothing */ }
        }
    }
}

@Composable
fun CurrentWeatherSection(
    current: CurrentWeather
) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formatTemperature(current.temp, TemperatureUnit.CELSIUS),
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                text = "Feels like ${formatTemperature(current.feelsLike, TemperatureUnit.CELSIUS)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = current.weatherDescription,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun HourlyForecastSection(
    hourlyForecasts: List<HourlyForecast>
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "24-Hour Forecast",
                style = MaterialTheme.typography.titleMedium
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(hourlyForecasts.take(24)) { forecast ->
                    HourlyForecastItem(forecast, TemperatureUnit.CELSIUS)
                }
            }
        }
    }
}

@Composable
fun DailyForecastSection(
    dailyForecasts: List<DailyForecast>
) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "7-Day Forecast",
                style = MaterialTheme.typography.titleMedium
            )
            dailyForecasts.take(7).forEach { forecast ->
                DailyForecastItem(forecast, TemperatureUnit.CELSIUS)
            }
        }
    }
}

@Composable
fun AlertsSection(alerts: List<WeatherAlert>) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Weather Alerts",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            alerts.forEach { alert ->
                AlertItem(alert)
            }
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
