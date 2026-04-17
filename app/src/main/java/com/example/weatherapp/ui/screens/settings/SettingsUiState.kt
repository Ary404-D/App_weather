package com.example.weatherapp.ui.screens.settings

data class SettingsUiState(
    val temperatureUnit: String = "celsius",
    val windSpeedUnit: String = "kph",
    val isDarkMode: Boolean = false
)

enum class TemperatureUnit(val key: String, val label: String) {
    CELSIUS("celsius", "Celsius (°C)"),
    FAHRENHEIT("fahrenheit", "Fahrenheit (°F)")
}

enum class WindSpeedUnit(val key: String, val label: String) {
    KPH("kph", "Km/h"),
    MPH("mph", "Miles/h")
}