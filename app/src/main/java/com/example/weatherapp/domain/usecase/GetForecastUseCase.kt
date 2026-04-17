package com.example.weatherapp.domain.usecase

import com.example.weatherapp.data.model.ForecastInfo
import com.example.weatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetForecastUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    operator fun invoke(location: String, days: Int = 5): Flow<Result<List<ForecastInfo>>> {
        return repository.getForecast(location, days)
    }
}
