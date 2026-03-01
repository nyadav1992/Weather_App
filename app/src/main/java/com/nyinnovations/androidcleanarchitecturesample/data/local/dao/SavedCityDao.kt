package com.nyinnovations.androidcleanarchitecturesample.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nyinnovations.androidcleanarchitecturesample.data.local.entity.SavedCityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedCityDao {

    @Query("SELECT * FROM saved_cities ORDER BY isAutoCity DESC, addedAt ASC")
    fun getAllCities(): Flow<List<SavedCityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: SavedCityEntity)

    @Query("DELETE FROM saved_cities WHERE cityName = :cityName")
    suspend fun deleteCity(cityName: String)

    @Query("DELETE FROM saved_cities WHERE isAutoCity = 1")
    suspend fun deleteAutoCity()

    @Query("SELECT cityName FROM saved_cities WHERE isAutoCity = 1 LIMIT 1")
    suspend fun getAutoCityName(): String?

    @Query("SELECT COUNT(*) FROM saved_cities WHERE isAutoCity = 0")
    suspend fun getManualCount(): Int

    @Query("SELECT COUNT(*) FROM saved_cities")
    suspend fun getCount(): Int
}

