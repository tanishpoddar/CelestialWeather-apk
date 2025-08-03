# 🌤️ Celestial Weather

A beautiful and feature-rich Android weather application built with modern Android development technologies. Get accurate weather forecasts, hourly predictions, and weather alerts with an intuitive user interface.

## ✨ Features

- **🌍 Real-time Weather Data**: Get current weather conditions for any location worldwide
- **📍 Location-based Forecasts**: Automatic weather updates based on your current location
- **🔍 City Search**: Search for weather information by city name
- **⏰ Hourly Forecast**: Hour-by-hour weather predictions for the next 24 hours
- **🔄 Background Updates**: Automatic weather data synchronization
- **📊 Detailed Metrics**: Temperature, humidity, pressure, wind speed, and more
- **🌅 Sunrise/Sunset Times**: Daily astronomical information

## 🛠️ Built With

- **Kotlin** - Primary programming language
- **Jetpack Compose** - Modern UI toolkit
- **MVVM Architecture** - Clean architecture pattern
- **Hilt** - Dependency injection
- **Room Database** - Local data persistence
- **Retrofit** - Network API calls
- **WorkManager** - Background tasks
- **Google Play Services** - Location services
- **Lottie** - Beautiful animations
- **Material Design 3** - Modern design system

## 🚀 Getting Started

### Prerequisites

- Android Studio Arctic Fox or later
- Android SDK 24+ (API level 24)
- Kotlin 1.9.22+
- JDK 17

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/tanishpoddar/CelestialWeather-apk.git
   cd CelestialWeather-apk
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory and select it

3. **Configure API Keys**
   - Create an account at [OpenWeatherMap](https://openweathermap.org/api)
   - Get your API key
   - Add your API key to `app/src/main/assets/api_keys.properties`:
     ```properties
     WEATHER_API_KEY=your_api_key_here
     ```

4. **Build and Run**
   - Connect an Android device or start an emulator
   - Click the "Run" button in Android Studio
   - The app will install and launch on your device

### For End Users

Download the latest APK from the [Releases](https://github.com/tanishpoddar/CelestialWeather-apk/releases) section and install it on your Android device.

## 📋 Requirements

- **Minimum Android Version**: Android 7.0 (API level 24)
- **Target Android Version**: Android 14 (API level 34)
- **Permissions Required**:
  - Location access (for automatic weather updates)
  - Internet access (for weather data)
  - Notification permissions (for weather alerts)

## 🏗️ Project Structure

```
app/
├── src/main/
│   ├── java/com/example/weatherapp/
│   │   ├── adapters/          # RecyclerView adapters
│   │   ├── api/               # Network API interfaces
│   │   ├── data/              # Database and data models
│   │   ├── di/                # Dependency injection
│   │   ├── models/            # Data models
│   │   ├── notifications/     # Notification services
│   │   ├── repository/        # Data repository layer
│   │   ├── ui/                # UI components and screens
│   │   ├── utils/             # Utility classes
│   │   ├── viewmodels/        # ViewModels
│   │   └── workers/           # Background workers
│   ├── res/                   # Resources (layouts, drawables, etc.)
│   └── assets/                # Configuration files
```

## 🔧 Configuration

### API Configuration
The app uses OpenWeatherMap API for weather data. Configure your API key in:
```
app/src/main/assets/api_keys.properties
```

### Build Configuration
Key build settings in `app/build.gradle`:
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Compile SDK**: 34
- **Kotlin Version**: 1.9.22

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.

## 📞 Support

If you encounter any issues or have questions:

1. Check the [Issues](https://github.com/tanishpoddar/CelestialWeather-apk/issues) section
2. Create a new issue with detailed information
3. Include device information, Android version, and steps to reproduce

## 🔄 Version History

- **v1.0** - Initial release with core weather functionality
  - Real-time weather data
  - Location-based forecasts
  - City search functionality
  - Modern Material Design 3 UI

---
⭐ **Star this repository if you find it helpful!** 