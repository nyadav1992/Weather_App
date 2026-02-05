package com.nyinnovations.androidcleanarchitecturesample.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nyinnovations.androidcleanarchitecturesample.data.local.entity.WeatherEntity

@Dao
interface WeatherDao {

    @Query("SELECT * FROM weather WHERE cityName = :city")
    suspend fun getWeather(city: String): WeatherEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)

    @Query("DELETE FROM weather WHERE cityName = :city")
    suspend fun deleteWeather(city: String)
}

