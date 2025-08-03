package com.example.weatherapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object Constants {
    // API key will be loaded from properties file
    private var _apiKey: String? = null
    
    // Getter for API key that loads it from properties if not already loaded
    fun getApiKey(context: Context): String {
        if (_apiKey == null) {
            try {
                val properties = java.util.Properties()
                val assetManager = context.assets
                val inputStream = assetManager.open("api_keys.properties")
                properties.load(inputStream)
                _apiKey = properties.getProperty("WEATHER_API_KEY")
                inputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
                // Fallback to hardcoded key only if properties file fails
                _apiKey = "cbf26d51498f81728fd1bb69df49fa59"
            }
        }
        return _apiKey!!
    }
    
    const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    const val METRIC_UNIT = "metric"

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
            return false
        }
    }
}

