package com.example.weatherapp.domain.usecase

import com.example.weatherapp.data.model.WeatherInfo
import com.example.weatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    operator fun invoke(location: String): Flow<Result<WeatherInfo>> {
        return repository.getCurrentWeather(location)
    }
}
