package com.example.weatherapp.data.remote

import com.example.weatherapp.data.model.AirQualityResponse
import com.example.weatherapp.data.model.ForecastResponse
import com.example.weatherapp.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("current.json")
    suspend fun getCurrentWeather(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("aqi") aqi: String = "no"
    ): WeatherResponse

    @GET("current.json")
    suspend fun getAirQuality(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("aqi") aqi: String = "yes"
    ): AirQualityResponse

    @GET("forecast.json")
    suspend fun getForecast(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("days") days: Int = 5,
        @Query("aqi") aqi: String = "no",
        @Query("alerts") alerts: String = "no"
    ): ForecastResponse

    companion object {
        const val BASE_URL = "https://api.weatherapi.com/v1/"
        const val API_KEY = "201b4ca44d044e6b97a72257260104"
    }
}
