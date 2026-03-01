package com.nyinnovations.androidcleanarchitecturesample.data.repository

import com.nyinnovations.androidcleanarchitecturesample.data.local.dao.SavedCityDao
import com.nyinnovations.androidcleanarchitecturesample.data.local.dao.WeatherDao
import com.nyinnovations.androidcleanarchitecturesample.data.local.entity.SavedCityEntity
import com.nyinnovations.androidcleanarchitecturesample.data.mapper.toDomain
import com.nyinnovations.androidcleanarchitecturesample.data.mapper.toEntity
import com.nyinnovations.androidcleanarchitecturesample.data.remote.GeocodingApi
import com.nyinnovations.androidcleanarchitecturesample.data.remote.WeatherApi
import com.nyinnovations.androidcleanarchitecturesample.domain.model.Forecast
import com.nyinnovations.androidcleanarchitecturesample.domain.model.Weather
import com.nyinnovations.androidcleanarchitecturesample.domain.repository.WeatherRepository
import com.nyinnovations.androidcleanarchitecturesample.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi,
    private val geocodingApi: GeocodingApi,
    private val weatherDao: WeatherDao,
    private val cityDao: SavedCityDao,
    private val apiKey: String
) : WeatherRepository {

    override fun getCurrentWeather(cityName: String): Flow<Result<Weather>> = flow {
        emit(Result.Loading)
        weatherDao.getWeather(cityName)?.let { emit(Result.Success(it.toDomain())) }
        try {
            val entity = api.getCurrentWeather(cityName, apiKey).toEntity()
            weatherDao.insertWeather(entity)
            emit(Result.Success(entity.toDomain()))
        } catch (e: Exception) {
            val cached = weatherDao.getWeather(cityName)
            if (cached != null) emit(Result.Success(cached.toDomain()))
            else emit(Result.Error("Couldn't load weather: ${e.localizedMessage}"))
        }
    }

    override fun getForecast(cityName: String): Flow<Result<Forecast>> = flow {
        emit(Result.Loading)
        try {
            emit(Result.Success(api.getForecast(cityName, apiKey).toDomain()))
        } catch (e: Exception) {
            emit(Result.Error("Couldn't load forecast: ${e.localizedMessage}"))
        }
    }

    override fun getSavedCities(): Flow<List<String>> {
        return cityDao.getAllCities().map { list -> list.map { it.cityName } }
    }

    override suspend fun addCity(cityName: String): Boolean {
        // only manual cities count toward the 5-city limit
        if (cityDao.getManualCount() >= 5) return false
        cityDao.insertCity(SavedCityEntity(cityName = cityName, isAutoCity = false))
        return true
    }

    override suspend fun removeCity(cityName: String) {
        cityDao.deleteCity(cityName)
    }

    // deletes the old GPS city (if any) and inserts the new one at the top
    override suspend fun updateAutoCity(cityName: String) {
        cityDao.deleteAutoCity()
        cityDao.insertCity(
            SavedCityEntity(
                cityName = cityName,
                addedAt = Long.MAX_VALUE, // always first in the list
                isAutoCity = true
            )
        )
    }

    override suspend fun searchCities(query: String): List<String> {
        return try {
            geocodingApi.searchCities(query, limit = 5, apiKey = apiKey)
                .map { geo ->
                    buildString {
                        append(geo.name)
                        geo.state?.let { append(", $it") }
                        append(", ${geo.country}")
                    }
                }
                .distinct()
        } catch (_: Exception) {
            emptyList()
        }
    }
}
