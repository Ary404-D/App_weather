package com.example.weatherapp.ui.screens.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.domain.usecase.GetCurrentWeatherUseCase
import com.example.weatherapp.domain.usecase.GetForecastUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getForecastUseCase: GetForecastUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        loadWeather("Ho Chi Minh")
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onToggleSearch() {
        _uiState.update { it.copy(isSearchActive = !it.isSearchActive) }
    }

    fun onSearch() {
        val query = _uiState.value.searchQuery.trim()
        if (query.isNotEmpty()) {
            loadWeather(query)
            _uiState.update { it.copy(isSearchActive = false, searchQuery = "") }
        }
    }

    private fun loadWeather(location: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, weatherInfo = null) }

            getCurrentWeatherUseCase(location).collect { result ->
                result.fold(
                    onSuccess = { weatherInfo ->
                        _uiState.update { it.copy(weatherInfo = weatherInfo) }
                        loadForecast(location)
                    },
                    onFailure = { exception ->
                        val errorMessage = when (exception) {
                            is UnknownHostException -> "Không có kết nối internet. Vui lòng kiểm tra lại mạng."
                            is IOException -> "Lỗi kết nối máy chủ."
                            else -> exception.message ?: "Đã xảy ra lỗi không xác định"
                        }
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = errorMessage
                            )
                        }
                    }
                )
            }
        }
    }

    private fun loadForecast(location: String) {
        viewModelScope.launch {
            getForecastUseCase(location).collect { result ->
                result.fold(
                    onSuccess = { forecastList ->
                        _uiState.update {
                            it.copy(isLoading = false, forecastList = forecastList)
                        }
                    },
                    onFailure = { exception ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = exception.message ?: "Lỗi khi tải dự báo"
                            )
                        }
                    }
                )
            }
        }
    }

    fun refresh() {
        val location = _uiState.value.weatherInfo?.cityName ?: "Ho Chi Minh"
        loadWeather(location)
    }
}
