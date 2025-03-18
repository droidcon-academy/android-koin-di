package com.droidcon.weatherscope.common

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Use case for getting the device's current location
 */
interface GetCurrentLocationUseCase {
    /**
     * Get the current location of the device
     * @return Flow emitting the location coordinates or an error
     */
    fun execute(): Flow<Result<Pair<Double, Double>>>
}

/**
 * Implementation that uses Android's [LocationManager] to retrieve the current location.
 */
class GetCurrentLocationUseCaseImpl(
    private val context: Context,
    private val permissionChecker: PermissionChecker
) : GetCurrentLocationUseCase {

    override fun execute(): Flow<Result<Pair<Double, Double>>> = flow {
        // Check for required location permissions.
        if (!hasLocationPermission()) {
            emit(Result.failure(IllegalStateException("Location permission not granted")))
            return@flow
        }

        try {
            val location = getDeviceLocation()
            if (location != null) {
                emit(Result.success(Pair(location.latitude, location.longitude)))
            } else {
                emit(Result.failure(IllegalStateException("Unable to get location")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    private fun hasLocationPermission(): Boolean {
        return permissionChecker.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) ||
                permissionChecker.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    /**
     * Obtains the device's location. First attempts to use the last known location;
     * if unavailable, it requests a single update.
     */
    @SuppressLint("MissingPermission")
    private suspend fun getDeviceLocation(): Location? = suspendCancellableCoroutine { continuation ->
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Obtain all enabled providers.
        val providers = locationManager.getProviders(true)
        if (providers.isEmpty()) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        // Try to obtain the best (most accurate) last known location.
        var bestLocation: Location? = null
        for (provider in providers) {
            val location = locationManager.getLastKnownLocation(provider)
            if (location != null && (bestLocation == null || location.accuracy < bestLocation.accuracy)) {
                bestLocation = location
            }
        }

        // If we got a previous location, return it immediately.
        if (bestLocation != null) {
            continuation.resume(bestLocation)
            return@suspendCancellableCoroutine
        }

        // Otherwise, request a single update.
        var resumed = false  // Guard to ensure we resume the coroutine only once.
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (!resumed) {
                    resumed = true
                    locationManager.removeUpdates(this)
                    continuation.resume(location)
                }
            }
        }

        // Choose the first available provider, or fallback to GPS.
        val bestProvider = providers.firstOrNull() ?: LocationManager.GPS_PROVIDER
        locationManager.requestLocationUpdates(
            bestProvider,
            0L,
            0f,
            locationListener,
            Looper.getMainLooper()
        )

        // Ensure that if the coroutine is cancelled, we stop receiving location updates.
        continuation.invokeOnCancellation {
            locationManager.removeUpdates(locationListener)
        }
    }
}