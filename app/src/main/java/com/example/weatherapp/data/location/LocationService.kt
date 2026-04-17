package com.example.weatherapp.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) {
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Result<Pair<Double, Double>> {
        if (!hasLocationPermission()) {
            return Result.failure(IllegalStateException("Thiếu quyền truy cập vị trí"))
        }

        return suspendCancellableCoroutine { continuation ->
            val cancellationTokenSource = CancellationTokenSource()

            fusedLocationProviderClient
                .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
                .addOnSuccessListener { location ->
                    val result = if (location != null) {
                        Result.success(location.latitude to location.longitude)
                    } else {
                        Result.failure(IllegalStateException("Không lấy được vị trí hiện tại"))
                    }
                    continuation.resume(result)
                }
                .addOnFailureListener { exception ->
                    continuation.resume(Result.failure(exception))
                }

            continuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        val hasFineLocation = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        val hasCoarseLocation = context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

        return hasFineLocation || hasCoarseLocation
    }
}
