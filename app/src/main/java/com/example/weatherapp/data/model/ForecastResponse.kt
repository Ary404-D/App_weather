package com.example.weatherapp.data.model

import com.google.gson.annotations.SerializedName

data class ForecastResponse(
    @SerializedName("location")
    val location: Location,
    @SerializedName("forecast")
    val forecast: Forecast
)

data class Forecast(
    @SerializedName("forecastday")
    val forecastDay: List<ForecastDay>
)

data class ForecastDay(
    @SerializedName("date")
    val date: String,
    @SerializedName("day")
    val day: Day,
    @SerializedName("hour")
    val hour: List<Hour>
)

data class Day(
    @SerializedName("maxtemp_c")
    val maxTempC: Double,
    @SerializedName("mintemp_c")
    val minTempC: Double,
    @SerializedName("maxtemp_f")
    val maxTempF: Double,
    @SerializedName("mintemp_f")
    val minTempF: Double,
    @SerializedName("avgtemp_c")
    val avgTempC: Double,
    @SerializedName("avgtemp_f")
    val avgTempF: Double,
    @SerializedName("maxwind_kph")
    val maxWindKph: Double,
    @SerializedName("avghumidity")
    val avgHumidity: Int,
    @SerializedName("condition")
    val condition: Condition,
    @SerializedName("uv")
    val uv: Double
)

data class Hour(
    @SerializedName("time")
    val time: String,
    @SerializedName("temp_c")
    val tempC: Double,
    @SerializedName("temp_f")
    val tempF: Double,
    @SerializedName("condition")
    val condition: Condition,
    @SerializedName("wind_kph")
    val windKph: Double,
    @SerializedName("humidity")
    val humidity: Int,
    @SerializedName("cloud")
    val cloud: Int,
    @SerializedName("feelslike_c")
    val feelslikeC: Double,
    @SerializedName("chance_of_rain")
    val chanceOfRain: Int
)
