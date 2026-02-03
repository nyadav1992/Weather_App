package com.nyinnovations.androidcleanarchitecturesample.domain.model

data class Weather(
    val cityName: String,
    val temperature: Double,
    val feelsLike: Double,
    val tempMin: Double,
    val tempMax: Double,
    val pressure: Int,
    val humidity: Int,
    val description: String,
    val icon: String,
    val windSpeed: Double,
    val timestamp: Long
)

