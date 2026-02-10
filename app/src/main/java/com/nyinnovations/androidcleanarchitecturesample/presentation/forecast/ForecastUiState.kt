package com.nyinnovations.androidcleanarchitecturesample.presentation.forecast

import com.nyinnovations.androidcleanarchitecturesample.domain.model.Forecast

data class ForecastUiState(
    val forecast: Forecast? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

