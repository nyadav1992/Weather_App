package com.nyinnovations.androidcleanarchitecturesample.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ForecastResponse(
    @SerializedName("list") val list: List<ForecastItemResponse>,
    @SerializedName("city") val city: City
)

data class ForecastItemResponse(
    @SerializedName("dt") val dt: Long,
    @SerializedName("main") val main: MainInfo,
    @SerializedName("weather") val weather: List<WeatherInfo>,
    @SerializedName("dt_txt") val dtTxt: String
)

data class City(
    @SerializedName("name") val name: String
)

