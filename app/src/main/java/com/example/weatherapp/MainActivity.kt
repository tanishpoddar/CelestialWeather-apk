package com.example.weatherapp
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.adapters.HourlyForecastAdapter
import com.example.weatherapp.models.HourlyForecastModel
import com.example.weatherapp.models.LegacyWeatherResponse as WeatherResponse
import com.example.weatherapp.utils.Constants
import com.example.weatherapp.utils.WeatherIconMapper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val requestLocationCode = 123123
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var progressBar: ProgressBar
    private lateinit var mainContainer: RelativeLayout
    private lateinit var errorText: TextView
    private lateinit var mHourlyForecastRecyclerView: RecyclerView
    private var locationCallback: LocationCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        progressBar = findViewById(R.id.progressbar)
        mainContainer = findViewById(R.id.mainContainer)
        errorText = findViewById(R.id.errorText)
        mHourlyForecastRecyclerView = findViewById(R.id.hourlyForecastRecyclerView)

        // Setup search functionality
        val searchButton = findViewById<Button>(R.id.btnSearch)
        val searchEditText = findViewById<EditText>(R.id.etSearchCity)

        searchButton.setOnClickListener {
            val cityName = searchEditText.text.toString().trim()
            if (cityName.isNotEmpty()) {
                showProgressDialog()
                getWeatherByCity(cityName)
            } else {
                Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show()
            }
        }

        // Setup footer click handlers
        findViewById<TextView>(R.id.tanishLink).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://tanish-poddar.is-a.dev/"))
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.githubIcon).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/tanishpoddar/CelestialWeather-apk"))
            startActivity(intent)
        }

        if (!isLocationEnabled()) {
            Toast.makeText(this@MainActivity, "Please enable location", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        } else {
            checkLocationPermissions()
        }
    }

    private fun checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                requestLocationCode
            )
        } else {
            // Check if location is actually enabled
            if (!isLocationEnabled()) {
                Toast.makeText(
                    this,
                    "Please enable location services to get weather for your location",
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            } else {
                requestLocationData()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestLocationCode) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Check if location is enabled after permissions are granted
                if (isLocationEnabled()) {
                    requestLocationData()
                } else {
                    Toast.makeText(
                        this,
                        "Please enable location services to get weather for your location",
                        Toast.LENGTH_LONG
                    ).show()
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }
            } else {
                Toast.makeText(
                    this,
                    "Location permission denied. You can search for cities manually.",
                    Toast.LENGTH_LONG
                ).show()
                
                // Show error card with helpful message
                showError("Location permission denied")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationData() {
        showProgressDialog()

        // Cancel any ongoing location request
        locationCallback?.let {
            mFusedLocationClient.removeLocationUpdates(it)
            locationCallback = null
        }

        // Create location request with high accuracy settings
        val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            3000 // 3 seconds
        )
            .setMinUpdateDistanceMeters(0f)  // Get updates regardless of distance change
            .setMaxUpdateDelayMillis(5000)   // Force updates every 5 seconds
            .setMinUpdateIntervalMillis(2000) // Don't update more than every 2 seconds
            .setWaitForAccurateLocation(true)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null) {
                    // Log location for debugging
                    Log.d("LocationUpdate", "Lat: ${location.latitude}, Lng: ${location.longitude}, Accuracy: ${location.accuracy}")

                    if (location.accuracy <= 100) { // Only use location if accuracy is within 100 meters
                        getLocationWeatherDetails(location.latitude, location.longitude)
                        mFusedLocationClient.removeLocationUpdates(this)
                        locationCallback = null
                    }
                }
            }
        }

        try {
            locationCallback?.let { callback ->
                mFusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    callback,
                    Looper.getMainLooper()
                )

                // Add timeout
                Handler(Looper.getMainLooper()).postDelayed({
                    if (mainContainer.visibility != View.VISIBLE) {
                        mFusedLocationClient.removeLocationUpdates(callback)
                        locationCallback = null
                        showError("Unable to get accurate location. Please ensure you are outdoors or near a window.")
                    }
                }, 15000) // 15 second timeout
            }
        } catch (e: Exception) {
            showError("Error getting location: ${e.message}")
        }
    }

    private fun getLocationWeatherDetails(latitude: Double, longitude: Double) {
        if (Constants.isNetworkAvailable(this)) {
            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service: WeatherServiceApi = retrofit.create(WeatherServiceApi::class.java)
            val call = service.getWeatherDetails(
                latitude, longitude,
                Constants.getApiKey(this),
                Constants.METRIC_UNIT
            )

            showProgressDialog()
            call.enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { setupUI(it) }
                    } else {
                        showError("Error: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    showError(t.message ?: "An error occurred")
                }
            })
        } else {
            showError("No internet connection available")
        }
    }

    private fun getWeatherByCity(cityName: String) {
        if (Constants.isNetworkAvailable(this)) {
            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service: WeatherServiceApi = retrofit.create(WeatherServiceApi::class.java)
            val call = service.getWeatherByCity(cityName, Constants.getApiKey(this), Constants.METRIC_UNIT)

            call.enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            setupUI(it)
                            // Stop location updates when searching for a city
                            mFusedLocationClient.removeLocationUpdates(object : LocationCallback() {})
                        }
                    } else {
                        when (response.code()) {
                            404 -> showError("City not found")
                            else -> showError("Error: ${response.message()}")
                        }
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    showError(t.message ?: "An error occurred")
                }
            })
        } else {
            showError("No internet connection available")
        }
    }

    private fun setupUI(weatherResponse: WeatherResponse) {
        hideProgressDialog()
        mainContainer.visibility = View.VISIBLE
        errorText.visibility = View.GONE

        // Get weather description and icon
        val weatherDescription = weatherResponse.weather.firstOrNull()?.description ?: "Unknown"
        val weatherIcon = weatherResponse.weather.firstOrNull()?.icon ?: "01d"

        // Set weather background image based on weather condition
        val weatherBackgroundImage = findViewById<android.widget.ImageView>(R.id.weatherBackgroundImage)
        val weatherImageResource = WeatherIconMapper.getWeatherImageResource(weatherDescription, weatherIcon)
        weatherBackgroundImage.setImageResource(weatherImageResource)

        // Weather status
        findViewById<TextView>(R.id.tv_main).text = weatherDescription.replaceFirstChar { it.uppercase() }
        findViewById<TextView>(R.id.tv_main_description).text = weatherDescription.replaceFirstChar { it.uppercase() }

        // Location details
        findViewById<TextView>(R.id.tv_location).text = getString(R.string.format_location, weatherResponse.name, weatherResponse.sys?.country ?: "")
        findViewById<TextView>(R.id.tv_exact_location).text = weatherResponse.coord?.let {
            getString(R.string.format_coordinates, it.lat, it.lon)
        } ?: ""

        // Temperature and weather details
        findViewById<TextView>(R.id.tv_temp).text = getString(R.string.format_temperature, weatherResponse.main.temp.toInt())
        findViewById<TextView>(R.id.tv_min_max).text = getString(R.string.format_minmax_temp, weatherResponse.main.temp_min, weatherResponse.main.temp_max)
        
        // Setup mock hourly forecast data (since we don't have actual hourly data in the current API response)
        setupMockHourlyForecast()
    }
    
    private fun setupMockHourlyForecast() {
        val hourlyList = ArrayList<HourlyForecastModel>()
        
        // Get current hour
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val timeFormat = SimpleDateFormat("h a", Locale.getDefault())
        
        // Generate mock data for the next 24 hours
        for (i in 0 until 24) {
            calendar.set(Calendar.HOUR_OF_DAY, currentHour + i)
            val time = timeFormat.format(calendar.time)
            
            // Generate random temperature between 20-30°C
            val temp = (20 + (Math.random() * 10)).toInt()
            
            // Alternate between a few weather icons
            val icons = arrayOf("01d", "02d", "03d", "04d", "10d")
            val icon = icons[(Math.random() * icons.size).toInt()]
            
            // Generate random precipitation probability
            val precip = (Math.random() * 100).toInt()
            
            hourlyList.add(HourlyForecastModel(
                time,
                "${temp}°C",
                icon,
                "${precip}%"
            ))
        }
        
        // Set up RecyclerView with horizontal layout
        val adapter = HourlyForecastAdapter(this, hourlyList)
        mHourlyForecastRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mHourlyForecastRecyclerView.adapter = adapter
    }
    


    private fun showProgressDialog() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressDialog() {
        progressBar.visibility = View.GONE
    }

    private fun showError(message: String) {
        hideProgressDialog()
        mainContainer.visibility = View.GONE
        findViewById<View>(R.id.errorCard).visibility = View.VISIBLE
        errorText.text = message
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as android.location.LocationManager
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)
    }
}

