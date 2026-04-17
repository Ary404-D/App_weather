package com.example.weatherapp.domain.usecase

import com.example.weatherapp.data.model.WeatherInfo
import com.example.weatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeatherByCoordinatesUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    operator fun invoke(latitude: Double, longitude: Double): Flow<Result<WeatherInfo>> {
        return repository.getWeatherByCoordinates(latitude, longitude)
    }

    suspend fun getCurrentLocation(): Result<Pair<Double, Double>> {
        return repository.getCurrentLocation()
    }
}

