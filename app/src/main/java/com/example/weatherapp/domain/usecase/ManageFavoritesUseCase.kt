package com.example.weatherapp.domain.usecase

import com.example.weatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ManageFavoritesUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    fun observeFavorites(): Flow<List<String>> {
        return repository.getFavoriteLocations()
    }

    suspend fun add(location: String) {
        repository.addFavoriteLocation(location)
    }

    suspend fun remove(location: String) {
        repository.removeFavoriteLocation(location)
    }
}

