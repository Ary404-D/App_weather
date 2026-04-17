package com.example.weatherapp.data.model

import com.google.gson.annotations.SerializedName

data class AirQualityResponse(
    @SerializedName("location")
    val location: Location,
    @SerializedName("current")
    val current: AirQualityCurrent
)

data class AirQualityCurrent(
    @SerializedName("air_quality")
    val airQuality: AirQualityDetails
)

data class AirQualityDetails(
    @SerializedName("co")
    val carbonMonoxide: Double,
    @SerializedName("no2")
    val nitrogenDioxide: Double,
    @SerializedName("o3")
    val ozone: Double,
    @SerializedName("so2")
    val sulphurDioxide: Double,
    @SerializedName("pm2_5")
    val pm2_5: Double,
    @SerializedName("pm10")
    val pm10: Double,
    @SerializedName("us-epa-index")
    val usEpaIndex: Int,
    @SerializedName("gb-defra-index")
    val gbDefraIndex: Int
)

