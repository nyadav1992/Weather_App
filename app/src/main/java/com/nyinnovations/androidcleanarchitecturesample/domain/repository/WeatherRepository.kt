package com.nyinnovations.androidcleanarchitecturesample.domain.repository

import com.nyinnovations.androidcleanarchitecturesample.domain.model.Forecast
import com.nyinnovations.androidcleanarchitecturesample.domain.model.Weather
import com.nyinnovations.androidcleanarchitecturesample.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getCurrentWeather(cityName: String): Flow<Result<Weather>>
    fun getForecast(cityName: String): Flow<Result<Forecast>>
    fun getSavedCities(): Flow<List<String>>
    suspend fun addCity(cityName: String): Boolean
    suspend fun removeCity(cityName: String)
    suspend fun searchCities(query: String): List<String>
    // replaces the auto (GPS) city — called every launch with detected location
    suspend fun updateAutoCity(cityName: String)
}
