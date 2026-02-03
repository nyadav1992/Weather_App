package com.nyinnovations.androidcleanarchitecturesample.domain.usecase

import com.nyinnovations.androidcleanarchitecturesample.domain.repository.WeatherRepository

class GetCurrentWeatherUseCase(private val repository: WeatherRepository) {
    operator fun invoke(cityName: String) = repository.getCurrentWeather(cityName)
}

