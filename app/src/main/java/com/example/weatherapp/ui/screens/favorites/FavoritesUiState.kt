package com.example.weatherapp.ui.screens.favorites

import com.example.weatherapp.data.model.AirQuality
import com.example.weatherapp.data.model.ForecastInfo
import com.example.weatherapp.data.model.WeatherInfo

data class FavoritesUiState(
    val favorites: Set<String> = emptySet(),
    val favoriteWeatherData: Map<String, WeatherInfo> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class FavoriteLocationState(
    val isLoading: Boolean = false,
    val weatherInfo: WeatherInfo? = null,
    val airQuality: AirQuality? = null,
    val forecastList: List<ForecastInfo> = emptyList(),
    val error: String? = null
)