package com.example.weatherapp.domain.repository

import com.example.weatherapp.data.model.AirQuality
import com.example.weatherapp.data.model.ForecastInfo
import com.example.weatherapp.data.model.WeatherInfo
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getCurrentWeather(location: String): Flow<Result<WeatherInfo>>
    fun getForecast(location: String, days: Int = 5): Flow<Result<List<ForecastInfo>>>
    fun getAirQuality(location: String): Flow<Result<AirQuality>>
    fun getWeatherByCoordinates(latitude: Double, longitude: Double): Flow<Result<WeatherInfo>>
    suspend fun getCurrentLocation(): Result<Pair<Double, Double>>
    fun getFavoriteLocations(): Flow<List<String>>
    suspend fun addFavoriteLocation(location: String)
    suspend fun removeFavoriteLocation(location: String)
    fun getTemperatureUnit(): Flow<String>
    suspend fun setTemperatureUnit(unit: String)
}
