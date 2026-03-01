package com.nyinnovations.androidcleanarchitecturesample.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_cities")
data class SavedCityEntity(
    @PrimaryKey val cityName: String,
    val addedAt: Long = System.currentTimeMillis(),
    val isAutoCity: Boolean = false  // true = detected from GPS, replaced on each launch
)

