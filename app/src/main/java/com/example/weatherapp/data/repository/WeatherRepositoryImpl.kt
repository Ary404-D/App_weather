package com.example.weatherapp.data.repository

import com.example.weatherapp.data.location.LocationService
import com.example.weatherapp.data.local.PreferencesManager
import com.example.weatherapp.data.model.AirQuality
import com.example.weatherapp.data.model.AirQualityDetails
import com.example.weatherapp.data.model.AirQualityResponse
import com.example.weatherapp.data.model.ForecastInfo
import com.example.weatherapp.data.model.ForecastResponse
import com.example.weatherapp.data.model.HourlyInfo
import com.example.weatherapp.data.model.WeatherInfo
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: WeatherApi,
    private val preferencesManager: PreferencesManager,
    private val locationService: LocationService
) : WeatherRepository {

    override fun getCurrentWeather(location: String): Flow<Result<WeatherInfo>> = flow {
        try {
            val response = weatherApi.getCurrentWeather(
                apiKey = WeatherApi.API_KEY,
                location = location
            )
            emit(Result.success(response.toWeatherInfo()))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getForecast(location: String, days: Int): Flow<Result<List<ForecastInfo>>> = flow {
        try {
            val response = weatherApi.getForecast(
                apiKey = WeatherApi.API_KEY,
                location = location,
                days = days
            )
            emit(Result.success(response.toForecastInfo()))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getAirQuality(location: String): Flow<Result<AirQuality>> = flow {
        try {
            val response = weatherApi.getAirQuality(
                apiKey = WeatherApi.API_KEY,
                location = location
            )
            emit(Result.success(response.toAirQuality()))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getWeatherByCoordinates(
        latitude: Double,
        longitude: Double
    ): Flow<Result<WeatherInfo>> {
        return getCurrentWeather("$latitude,$longitude")
    }

    override suspend fun getCurrentLocation(): Result<Pair<Double, Double>> {
        return locationService.getCurrentLocation()
    }

    override fun getFavoriteLocations(): Flow<List<String>> {
        return preferencesManager.favoriteLocations
    }

    override suspend fun addFavoriteLocation(location: String) {
        preferencesManager.addFavoriteLocation(location)
    }

    override suspend fun removeFavoriteLocation(location: String) {
        preferencesManager.removeFavoriteLocation(location)
    }

    override fun getTemperatureUnit(): Flow<String> {
        return preferencesManager.temperatureUnit
    }

    override suspend fun setTemperatureUnit(unit: String) {
        preferencesManager.setTemperatureUnit(unit)
    }

    private fun WeatherResponse.toWeatherInfo() = WeatherInfo(
        cityName = location.name,
        country = location.country,
        localTime = location.localtime,
        temperature = current.tempC,
        temperatureF = current.tempF,
        feelsLike = current.feelslikeC,
        feelsLikeF = current.feelslikeF,
        conditionText = current.condition.text,
        conditionIcon = "https:${current.condition.icon}",
        windSpeed = current.windKph,
        windDirection = current.windDir,
        humidity = current.humidity,
        cloud = current.cloud,
        uv = current.uv,
        isDay = current.isDay == 1
    )

    private fun ForecastResponse.toForecastInfo(): List<ForecastInfo> {
        return forecast.forecastDay.map { forecastDay ->
            ForecastInfo(
                date = forecastDay.date,
                maxTemp = forecastDay.day.maxTempC,
                minTemp = forecastDay.day.minTempC,
                avgTemp = forecastDay.day.avgTempC,
                maxWind = forecastDay.day.maxWindKph,
                avgHumidity = forecastDay.day.avgHumidity,
                conditionText = forecastDay.day.condition.text,
                conditionIcon = "https:${forecastDay.day.condition.icon}",
                uv = forecastDay.day.uv,
                chanceOfRain = forecastDay.hour.maxOfOrNull { it.chanceOfRain } ?: 0,
                hourlyForecast = forecastDay.hour.map { hour ->
                    HourlyInfo(
                        time = hour.time,
                        temp = hour.tempC,
                        conditionText = hour.condition.text,
                        conditionIcon = "https:${hour.condition.icon}",
                        chanceOfRain = hour.chanceOfRain
                    )
                }
            )
        }
    }

    private fun AirQualityResponse.toAirQuality(): AirQuality {
        return current.airQuality.toAirQuality()
    }

    private fun AirQualityDetails.toAirQuality(): AirQuality {
        return AirQuality(
            carbonMonoxide = carbonMonoxide,
            nitrogenDioxide = nitrogenDioxide,
            ozone = ozone,
            sulphurDioxide = sulphurDioxide,
            pm2_5 = pm2_5,
            pm10 = pm10,
            usEpaIndex = usEpaIndex,
            gbDefraIndex = gbDefraIndex,
            category = usEpaIndex.toAirQualityCategory()
        )
    }

    private fun Int.toAirQualityCategory(): String {
        return when (this) {
            1 -> "Good"
            2 -> "Moderate"
            3 -> "Unhealthy for sensitive groups"
            4 -> "Unhealthy"
            5 -> "Very unhealthy"
            6 -> "Hazardous"
            else -> "Unknown"
        }
    }
}
