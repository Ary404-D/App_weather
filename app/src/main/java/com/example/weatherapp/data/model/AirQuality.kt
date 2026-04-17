package com.example.weatherapp.data.model

data class AirQuality(
    val carbonMonoxide: Double,
    val nitrogenDioxide: Double,
    val ozone: Double,
    val sulphurDioxide: Double,
    val pm2_5: Double,
    val pm10: Double,
    val usEpaIndex: Int,
    val gbDefraIndex: Int,
    val category: String
)

