package com.example.weatherapp.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.domain.usecase.SettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsUseCase: SettingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            launch {
                settingsUseCase.temperatureUnit.collect { unit ->
                    _uiState.update { it.copy(temperatureUnit = unit) }
                }
            }
            viewModelScope.launch {
                settingsUseCase.windSpeedUnit.collect { unit ->
                    _uiState.update { it.copy(windSpeedUnit = unit) }
                }
            }
            viewModelScope.launch {
                settingsUseCase.isDarkMode.collect { isDark ->
                    _uiState.update { it.copy(isDarkMode = isDark) }
                }
            }
        }
    }

    fun setTemperatureUnit(unit: String) {
        viewModelScope.launch {
            settingsUseCase.setTemperatureUnit(unit)
        }
    }

    fun setWindSpeedUnit(unit: String) {
        viewModelScope.launch {
            settingsUseCase.setWindSpeedUnit(unit)
        }
    }

    fun setDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            settingsUseCase.setDarkMode(isDark)
        }
    }

    fun toggleDarkMode() {
        setDarkMode(!_uiState.value.isDarkMode)
    }
}