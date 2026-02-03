package com.nyinnovations.androidcleanarchitecturesample.domain.model

data class Forecast(
    val cityName: String,
    val forecasts: List<ForecastItem>
)

data class ForecastItem(
    val date: String,
    val temperature: Double,
    val tempMin: Double,
    val tempMax: Double,
    val description: String,
    val icon: String,
    val timestamp: Long
)

