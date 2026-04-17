package com.example.weatherapp.ui.screens.weather

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.local.PreferencesManager
import com.example.weatherapp.data.location.LocationService
import com.example.weatherapp.domain.usecase.GetAirQualityUseCase
import com.example.weatherapp.domain.usecase.GetCurrentWeatherUseCase
import com.example.weatherapp.domain.usecase.GetForecastUseCase
import com.example.weatherapp.domain.usecase.ManageFavoritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getForecastUseCase: GetForecastUseCase,
    private val getAirQualityUseCase: GetAirQualityUseCase,
    private val manageFavoritesUseCase: ManageFavoritesUseCase,
    private val locationService: LocationService,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private var currentLocation: String = "Ha Noi"

    init {
        loadWeather("Ha Noi")
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            manageFavoritesUseCase.favoriteLocations.collect { favorites ->
                _uiState.update { state ->
                    state.copy(isFavorite = favorites.contains(state.weatherInfo?.cityName))
                }
            }
        }
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
            _uiState.update { it.copy(isSearchActive = false, searchQuery = "", isUsingGps = false) }
        }
    }

    fun loadWeatherByLocation(location: String) {
        loadWeather(location)
    }

    fun loadWeatherByCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isUsingGps = true) }
            getCurrentWeatherUseCase("$lat,$lon").collect { result ->
                result.fold(
                    onSuccess = { weatherInfo ->
                        currentLocation = weatherInfo.cityName
                        _uiState.update {
                            it.copy(
                                weatherInfo = weatherInfo,
                                isLoading = false
                            )
                        }
                        loadForecast(weatherInfo.cityName)
                        loadAirQuality(weatherInfo.cityName)
                        checkFavoriteStatus(weatherInfo.cityName)
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

    fun loadCurrentLocationWeather() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val location = locationService.getCurrentLocation()
                ?: locationService.getLastKnownLocation()

            if (location != null) {
                loadWeatherByCoordinates(location.latitude, location.longitude)
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Không thể lấy vị trí hiện tại. Vui lòng bật GPS."
                    )
                }
            }
        }
    }

    private fun loadWeather(location: String) {
        currentLocation = location
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getCurrentWeatherUseCase(location).collect { result ->
                result.fold(
                    onSuccess = { weatherInfo ->
                        _uiState.update { it.copy(weatherInfo = weatherInfo) }
                        loadForecast(location)
                        loadAirQuality(location)
                        checkFavoriteStatus(location)
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

    private fun loadAirQuality(location: String) {
        viewModelScope.launch {
            getAirQualityUseCase(location).collect { result ->
                result.fold(
                    onSuccess = { airQuality ->
                        _uiState.update { it.copy(airQuality = airQuality) }
                    },
                    onFailure = { }
                )
            }
        }
    }

    private fun checkFavoriteStatus(location: String) {
        viewModelScope.launch {
            val favorites = manageFavoritesUseCase.favoriteLocations.first()
            _uiState.update { it.copy(isFavorite = favorites.contains(location)) }
        }
    }

    fun toggleFavorite() {
        val cityName = _uiState.value.weatherInfo?.cityName ?: return
        viewModelScope.launch {
            if (_uiState.value.isFavorite) {
                manageFavoritesUseCase.removeFavorite(cityName)
            } else {
                manageFavoritesUseCase.addFavorite(cityName)
            }
            _uiState.update { it.copy(isFavorite = !it.isFavorite) }
        }
    }

    fun shareWeather(): String {
        val weather = _uiState.value.weatherInfo ?: return ""
        return buildString {
            appendLine("🌤️ Thời tiết hiện tại tại ${weather.cityName}, ${weather.country}")
            appendLine("🌡️ Nhiệt độ: ${weather.temperature.toInt()}°C (${weather.conditionText})")
            appendLine("💧 Độ ẩm: ${weather.humidity}%")
            appendLine("💨 Tốc độ gió: ${weather.windSpeed.toInt()} km/h")
            appendLine("☀️ Chỉ số UV: ${weather.uv}")
            appendLine()
            appendLine("📱 Chia sẻ từ Weather App")
        }
    }

    fun refresh() {
        if (_uiState.value.isUsingGps) {
            loadCurrentLocationWeather()
        } else {
            loadWeather(currentLocation)
        }
    }

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun getCurrentWeatherInfo() = _uiState.value.weatherInfo
    fun getCurrentLocation() = currentLocation
}