package com.example.weatherapp.ui.screens.detail

import com.example.weatherapp.data.model.AirQuality
import com.example.weatherapp.data.model.ForecastInfo
import com.example.weatherapp.data.model.WeatherInfo

data class DetailUiState(
    val isLoading: Boolean = false,
    val weatherInfo: WeatherInfo? = null,
    val forecastList: List<ForecastInfo> = emptyList(),
    val airQuality: AirQuality? = null,
    val error: String? = null,
    val isDay: Boolean = true
)