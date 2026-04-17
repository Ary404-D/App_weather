package com.example.weatherapp.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.domain.usecase.GetAirQualityUseCase
import com.example.weatherapp.domain.usecase.GetForecastUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getForecastUseCase: GetForecastUseCase,
    private val getAirQualityUseCase: GetAirQualityUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private var currentLocation: String = ""

    fun loadDetailData(location: String, weatherInfo: com.example.weatherapp.data.model.WeatherInfo?) {
        currentLocation = location
        weatherInfo?.let {
            _uiState.update { state ->
                state.copy(
                    weatherInfo = it,
                    isDay = it.isDay
                )
            }
        }
        loadForecast()
        loadAirQuality()
    }

    private fun loadForecast() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            getForecastUseCase(currentLocation, 5).collect { result ->
                result.fold(
                    onSuccess = { forecastList ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                forecastList = forecastList
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

    private fun loadAirQuality() {
        viewModelScope.launch {
            getAirQualityUseCase(currentLocation).collect { result ->
                result.fold(
                    onSuccess = { airQuality ->
                        _uiState.update { it.copy(airQuality = airQuality) }
                    },
                    onFailure = { }
                )
            }
        }
    }

    fun refresh() {
        loadForecast()
        loadAirQuality()
    }
}