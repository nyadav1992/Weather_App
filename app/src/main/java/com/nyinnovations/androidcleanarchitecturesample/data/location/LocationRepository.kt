package com.nyinnovations.androidcleanarchitecturesample.data.location

import android.Manifest
import android.annotation.SuppressLint
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
import kotlinx.coroutines.withTimeoutOrNull
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

    // returns city name from GPS, or null if unavailable
    suspend fun detectCurrentCity(): String? {
        if (!hasLocationPermission()) return null
        val location = getBestLocation() ?: return null
        return reverseGeocode(location.latitude, location.longitude)
    }

    @SuppressLint("MissingPermission")
    private suspend fun getBestLocation(): Location? {
        if (!hasLocationPermission()) return null

        // HIGH_ACCURACY forces a real GPS fix — avoids stale Google coarse cache (Mountain View)
        val fresh = withTimeoutOrNull(8_000L) {
            suspendCancellableCoroutine { cont ->
                val cts = CancellationTokenSource()
                locationClient
                    .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                    .addOnSuccessListener { cont.resume(it) }
                    .addOnFailureListener { cont.resume(null) }
                cont.invokeOnCancellation { cts.cancel() }
            }
        }
        if (fresh != null) return fresh

        // fall back to last known location only if fresh failed
        return suspendCancellableCoroutine { cont ->
            locationClient.lastLocation
                .addOnSuccessListener { cont.resume(it) }
                .addOnFailureListener { cont.resume(null) }
        }
    }

    private suspend fun reverseGeocode(lat: Double, lon: Double): String? {
        return try {
            geocodingApi.reverseGeocode(lat, lon, limit = 1, apiKey = apiKey)
                .firstOrNull()?.name
        } catch (_: Exception) {
            null
        }
    }
}

