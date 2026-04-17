package com.example.weatherapp.data.model

data class WeatherInfo(
    val cityName: String,
    val country: String,
    val localTime: String,
    val temperature: Double,
    val temperatureF: Double,
    val feelsLike: Double,
    val feelsLikeF: Double,
    val conditionText: String,
    val conditionIcon: String,
    val windSpeed: Double,
    val windDirection: String,
    val humidity: Int,
    val cloud: Int,
    val uv: Double,
    val isDay: Boolean
)

data class ForecastInfo(
    val date: String,
    val maxTemp: Double,
    val minTemp: Double,
    val avgTemp: Double,
    val maxWind: Double,
    val avgHumidity: Int,
    val conditionText: String,
    val conditionIcon: String,
    val uv: Double,
    val chanceOfRain: Int,
    val hourlyForecast: List<HourlyInfo>
)

data class HourlyInfo(
    val time: String,
    val temp: Double,
    val conditionText: String,
    val conditionIcon: String,
    val chanceOfRain: Int
)
