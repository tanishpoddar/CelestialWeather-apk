package com.example.weatherapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.models.*
import com.example.weatherapp.repository.WeatherDataManager
import com.example.weatherapp.utils.WeatherUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherDataManager: WeatherDataManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState

    private val _temperatureUnit = MutableStateFlow(
        if (weatherDataManager.getCachedTemperatureUnit() == "celsius")
            TemperatureUnit.CELSIUS
        else TemperatureUnit.FAHRENHEIT
    )
    val temperatureUnit: StateFlow<TemperatureUnit> = _temperatureUnit

    private val _isDarkTheme = MutableStateFlow(WeatherUtils.isDaytime().not())
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    private val _currentAnimation = MutableStateFlow("sunny_day.json")
    val currentAnimation: StateFlow<String> = _currentAnimation

    init {
        refreshWeatherData()
        startThemeUpdates()
    }

    fun refreshWeatherData() {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            try {
                weatherDataManager.refreshWeatherData(DEFAULT_LAT, DEFAULT_LON)
                    .collect { response ->
                        _uiState.value = WeatherUiState.Success(response)
                        updateWeatherAnimation(response.current.weatherDescription)
                    }
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error("Failed to fetch weather data")
            }
        }
    }

    fun toggleTemperatureUnit() {
        val newUnit = when (_temperatureUnit.value) {
            TemperatureUnit.CELSIUS -> TemperatureUnit.FAHRENHEIT
            TemperatureUnit.FAHRENHEIT -> TemperatureUnit.CELSIUS
        }
        _temperatureUnit.value = newUnit
        weatherDataManager.setTemperatureUnit(
            if (newUnit == TemperatureUnit.CELSIUS) "celsius" else "fahrenheit"
        )
    }

    private fun startThemeUpdates() {
        viewModelScope.launch {
            while (true) {
                _isDarkTheme.value = WeatherUtils.isDaytime().not()
                kotlinx.coroutines.delay(60_000) // Update every minute
            }
        }
    }

    private fun updateWeatherAnimation(weatherDescription: String) {
        _currentAnimation.value = WeatherUtils.getWeatherAnimation(weatherDescription)
    }

    companion object {
        private const val DEFAULT_LAT = 40.7128 // New York City coordinates
        private const val DEFAULT_LON = -74.0060
    }
}

sealed class WeatherUiState {
    data object Loading : WeatherUiState()
    data class Success(val weather: WeatherResponse) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}
