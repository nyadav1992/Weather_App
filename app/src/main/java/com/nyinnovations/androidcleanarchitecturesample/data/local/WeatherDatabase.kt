package com.nyinnovations.androidcleanarchitecturesample.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nyinnovations.androidcleanarchitecturesample.data.local.dao.SavedCityDao
import com.nyinnovations.androidcleanarchitecturesample.data.local.dao.WeatherDao
import com.nyinnovations.androidcleanarchitecturesample.data.local.entity.SavedCityEntity
import com.nyinnovations.androidcleanarchitecturesample.data.local.entity.WeatherEntity

@Database(
    entities = [WeatherEntity::class, SavedCityEntity::class],
    version = 3,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    abstract fun savedCityDao(): SavedCityDao
}
