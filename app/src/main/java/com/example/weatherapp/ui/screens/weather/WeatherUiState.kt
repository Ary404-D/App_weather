package com.example.weatherapp.ui.screens.weather

import com.example.weatherapp.data.model.ForecastInfo
import com.example.weatherapp.data.model.WeatherInfo

data class WeatherUiState(
    val isLoading: Boolean = false,
    val weatherInfo: WeatherInfo? = null,
    val forecastList: List<ForecastInfo> = emptyList(),
    val error: String? = null,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false
)
