package com.nyinnovations.androidcleanarchitecturesample.presentation.weather

import com.nyinnovations.androidcleanarchitecturesample.domain.model.Weather

data class WeatherUiState(
    val cities: List<String> = emptyList(),
    val cityWeathers: Map<String, Weather> = emptyMap(),
    val isLoading: Boolean = false,
    val isLocating: Boolean = false,  // true while GPS is resolving current city
    val error: String? = null
)
