package com.nyinnovations.androidcleanarchitecturesample.data.mapper

import com.nyinnovations.androidcleanarchitecturesample.data.local.entity.WeatherEntity
import com.nyinnovations.androidcleanarchitecturesample.data.remote.dto.ForecastResponse
import com.nyinnovations.androidcleanarchitecturesample.data.remote.dto.WeatherResponse
import com.nyinnovations.androidcleanarchitecturesample.domain.model.Forecast
import com.nyinnovations.androidcleanarchitecturesample.domain.model.ForecastItem
import com.nyinnovations.androidcleanarchitecturesample.domain.model.Weather
import java.text.SimpleDateFormat
import java.util.Locale

fun WeatherResponse.toEntity(): WeatherEntity {
    return WeatherEntity(
        cityName = name,
        temperature = main.temp,
        feelsLike = main.feelsLike,
        tempMin = main.tempMin,
        tempMax = main.tempMax,
        pressure = main.pressure,
        humidity = main.humidity,
        description = weather.firstOrNull()?.description.orEmpty(),
        icon = weather.firstOrNull()?.icon.orEmpty(),
        windSpeed = wind.speed,
        timestamp = System.currentTimeMillis()
    )
}

fun WeatherEntity.toDomain(): Weather {
    return Weather(
        cityName = cityName,
        temperature = temperature,
        feelsLike = feelsLike,
        tempMin = tempMin,
        tempMax = tempMax,
        pressure = pressure,
        humidity = humidity,
        description = description,
        icon = icon,
        windSpeed = windSpeed,
        timestamp = timestamp
    )
}

fun ForecastResponse.toDomain(): Forecast {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val outputFormat = SimpleDateFormat("EEE, MMM dd • HH:mm", Locale.getDefault())

    return Forecast(
        cityName = city.name,
        forecasts = list.map { item ->
            val formattedDate = try {
                inputFormat.parse(item.dtTxt)?.let { outputFormat.format(it) } ?: item.dtTxt
            } catch (_: Exception) {
                item.dtTxt
            }
            ForecastItem(
                date = formattedDate,
                temperature = item.main.temp,
                tempMin = item.main.tempMin,
                tempMax = item.main.tempMax,
                description = item.weather.firstOrNull()?.description.orEmpty(),
                icon = item.weather.firstOrNull()?.icon.orEmpty(),
                timestamp = item.dt * 1000
            )
        }
    )
}
