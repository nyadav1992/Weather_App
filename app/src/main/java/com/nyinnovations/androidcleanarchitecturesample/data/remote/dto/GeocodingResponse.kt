package com.nyinnovations.androidcleanarchitecturesample.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GeocodingResponse(
    @SerializedName("name") val name: String,
    @SerializedName("country") val country: String,
    @SerializedName("state") val state: String? = null
)

