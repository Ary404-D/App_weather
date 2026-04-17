package com.example.weatherapp.domain.usecase

import com.example.weatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    fun observeTemperatureUnit(): Flow<String> {
        return repository.getTemperatureUnit()
    }

    suspend fun setTemperatureUnit(unit: String) {
        repository.setTemperatureUnit(unit)
    }

    companion object {
        const val UNIT_CELSIUS = "celsius"
        const val UNIT_FAHRENHEIT = "fahrenheit"
    }
}

