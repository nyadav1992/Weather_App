package com.nyinnovations.androidcleanarchitecturesample.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.nyinnovations.androidcleanarchitecturesample.data.remote.GeocodingApi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class LocationRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val locationClient: FusedLocationProviderClient,
    private val geocodingApi: GeocodingApi,
    private val apiKey: String
) {

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // grabs last known or fresh location, returns city name via reverse geocoding
    suspend fun detectCurrentCity(): String? {
        if (!hasLocationPermission()) return null

        val location = getLastLocation() ?: return null
        return reverseGeocode(location.latitude, location.longitude)
    }

    @SuppressWarnings("MissingPermission")
    private suspend fun getLastLocation(): Location? {
        if (!hasLocationPermission()) return null

        return suspendCancellableCoroutine { cont ->
            val cts = CancellationTokenSource()
            locationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.token)
                .addOnSuccessListener { location ->
                    cont.resume(location)
                }
                .addOnFailureListener {
                    cont.resume(null)
                }
            cont.invokeOnCancellation { cts.cancel() }
        }
    }

    private suspend fun reverseGeocode(lat: Double, lon: Double): String? {
        return try {
            val results = geocodingApi.reverseGeocode(lat, lon, limit = 1, apiKey = apiKey)
            results.firstOrNull()?.name
        } catch (_: Exception) {
            null
        }
    }
}

