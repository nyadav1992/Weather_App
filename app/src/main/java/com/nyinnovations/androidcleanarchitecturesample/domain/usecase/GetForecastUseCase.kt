package com.nyinnovations.androidcleanarchitecturesample.domain.usecase

import com.nyinnovations.androidcleanarchitecturesample.domain.repository.WeatherRepository

class GetForecastUseCase(private val repository: WeatherRepository) {
    operator fun invoke(cityName: String) = repository.getForecast(cityName)
}

