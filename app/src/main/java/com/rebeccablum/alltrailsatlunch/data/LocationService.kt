package com.rebeccablum.alltrailsatlunch.data

import android.annotation.SuppressLint
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationService @Inject constructor(private val fusedLocationProviderClient: FusedLocationProviderClient) {

    private val cancellationTokenSource = CancellationTokenSource()

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LatLng {
        val currentLocation = withContext(Dispatchers.IO) {
            fusedLocationProviderClient.getCurrentLocation(
                PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).asDeferred().await()
        }
        return LatLng(currentLocation.latitude, currentLocation.longitude)
    }

    // TODO find the right place to call this
    fun cancelRequestsInFlight() {
        cancellationTokenSource.cancel()
    }
}