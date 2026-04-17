package com.example.weatherapp.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "weather_settings")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val favoriteLocations: Flow<List<String>> =
        context.dataStore.data.map { preferences ->
            preferences[FAVORITES_KEY]
                ?.toList()
                ?.sorted()
                .orEmpty()
        }

    val temperatureUnit: Flow<String> =
        context.dataStore.data.map { preferences ->
            preferences[TEMPERATURE_UNIT_KEY] ?: DEFAULT_TEMPERATURE_UNIT
        }

    suspend fun addFavoriteLocation(location: String) {
        val normalizedLocation = location.trim()
        if (normalizedLocation.isBlank()) return

        context.dataStore.edit { preferences ->
            val updatedFavorites = preferences[FAVORITES_KEY].orEmpty().toMutableSet()
            updatedFavorites.add(normalizedLocation)
            preferences[FAVORITES_KEY] = updatedFavorites
        }
    }

    suspend fun removeFavoriteLocation(location: String) {
        val normalizedLocation = location.trim()
        if (normalizedLocation.isBlank()) return

        context.dataStore.edit { preferences ->
            val updatedFavorites = preferences[FAVORITES_KEY].orEmpty().toMutableSet()
            updatedFavorites.remove(normalizedLocation)
            preferences[FAVORITES_KEY] = updatedFavorites
        }
    }

    suspend fun setTemperatureUnit(unit: String) {
        val normalizedUnit = if (unit.equals(UNIT_FAHRENHEIT, ignoreCase = true)) {
            UNIT_FAHRENHEIT
        } else {
            DEFAULT_TEMPERATURE_UNIT
        }

        context.dataStore.edit { preferences ->
            preferences[TEMPERATURE_UNIT_KEY] = normalizedUnit
        }
    }

    companion object {
        const val UNIT_CELSIUS = "celsius"
        const val UNIT_FAHRENHEIT = "fahrenheit"
        const val DEFAULT_TEMPERATURE_UNIT = UNIT_CELSIUS

        private val FAVORITES_KEY = stringSetPreferencesKey("favorite_locations")
        private val TEMPERATURE_UNIT_KEY = stringPreferencesKey("temperature_unit")
    }
}

