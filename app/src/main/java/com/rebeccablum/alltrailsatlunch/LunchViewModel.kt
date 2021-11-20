package com.rebeccablum.alltrailsatlunch

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rebeccablum.alltrailsatlunch.data.LatLng
import com.rebeccablum.alltrailsatlunch.data.LocationService
import com.rebeccablum.alltrailsatlunch.data.LunchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LunchViewModel @Inject constructor(
    private val repository: LunchRepository,
    private val locationService: LocationService
) : ViewModel() {
    val showLocationPermissionPrompt = mutableStateOf(true)
    val nearbyRestaurants = repository.nearbyRestaurants
    val screenState = mutableStateOf(ScreenState.LOADING)
    val userLocation = mutableStateOf<LatLng?>(null)

    private var updateRestaurantsFromLocationJob: Job? = null

    // TODO tie to user interaction
    fun updateRestaurantsFromUserLocation() {
        updateRestaurantsFromLocationJob?.cancel()
        updateRestaurantsFromLocationJob = viewModelScope.launch {
            val userLocation = locationService.getCurrentLocation().also { userLocation.value = it }
            repository.updateNearbyRestaurants(
                LatLng(
                    userLocation.latitude,
                    userLocation.longitude
                )
            )
        }
    }
}

enum class ScreenState {
    LOADING,
    DATA,
    ERROR
}