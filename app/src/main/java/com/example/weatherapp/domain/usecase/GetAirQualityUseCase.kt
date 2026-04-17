package com.example.weatherapp.domain.usecase

import com.example.weatherapp.data.model.AirQuality
import com.example.weatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAirQualityUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    operator fun invoke(location: String): Flow<Result<AirQuality>> {
        return repository.getAirQuality(location)
    }
}

