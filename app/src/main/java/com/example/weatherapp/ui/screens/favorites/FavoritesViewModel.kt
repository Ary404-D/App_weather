package com.example.weatherapp.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.domain.usecase.GetAirQualityUseCase
import com.example.weatherapp.domain.usecase.GetCurrentWeatherUseCase
import com.example.weatherapp.domain.usecase.GetForecastUseCase
import com.example.weatherapp.domain.usecase.ManageFavoritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val manageFavoritesUseCase: ManageFavoritesUseCase,
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getForecastUseCase: GetForecastUseCase,
    private val getAirQualityUseCase: GetAirQualityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            manageFavoritesUseCase.favoriteLocations.collect { favorites ->
                _uiState.update { it.copy(favorites = favorites) }
                favorites.forEach { location ->
                    loadWeatherForLocation(location)
                }
            }
        }
    }

    private fun loadWeatherForLocation(location: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            getCurrentWeatherUseCase(location).collect { result ->
                result.fold(
                    onSuccess = { weatherInfo ->
                        _uiState.update { state ->
                            state.copy(
                                favoriteWeatherData = state.favoriteWeatherData + (location to weatherInfo),
                                isLoading = false
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = error.message
                            )
                        }
                    }
                )
            }
        }
    }

    fun removeFavorite(location: String) {
        viewModelScope.launch {
            manageFavoritesUseCase.removeFavorite(location)
        }
    }

    fun refresh() {
        _uiState.value.favorites.forEach { location ->
            loadWeatherForLocation(location)
        }
    }
}