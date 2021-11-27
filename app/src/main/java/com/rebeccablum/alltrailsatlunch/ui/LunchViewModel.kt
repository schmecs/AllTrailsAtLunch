package com.rebeccablum.alltrailsatlunch.ui

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.rebeccablum.alltrailsatlunch.R
import com.rebeccablum.alltrailsatlunch.data.LocationService
import com.rebeccablum.alltrailsatlunch.data.LunchRepository
import com.rebeccablum.alltrailsatlunch.data.Response
import com.rebeccablum.alltrailsatlunch.util.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class LunchViewModel @Inject constructor(
    private val repository: LunchRepository,
    private val locationService: LocationService,
    private val resourceProvider: ResourceProvider
) : ViewModel() {
    val nearbyRestaurants = repository.nearbyRestaurants

    val selectedLocation = MutableStateFlow<LatLng?>(null)
    private val currentRadius = MutableStateFlow(200)
    private val mapMoving = MutableStateFlow(false)
    val searchText = MutableStateFlow("")
    val errorMessage = MutableStateFlow<String?>(null)

    @VisibleForTesting
    var getCurrentLocationJob: Job? = null

    @VisibleForTesting
    var searchJob: Job? = null

    // TODO tie to user interaction
    fun updateCurrentLocation() {
        getCurrentLocationJob?.cancel()
        getCurrentLocationJob = viewModelScope.launch {
            selectedLocation.value = locationService.getCurrentLocation()
        }
    }

    fun onMapMoving() {
        mapMoving.value = true
        getCurrentLocationJob?.cancel()
        searchJob?.cancel()
        locationService.cancelActiveRequest()
    }

    fun onMapIdle(newCenter: LatLng, radius: Int) {
        mapMoving.value = false
        selectedLocation.value = newCenter
        currentRadius.value = radius
    }

    fun onSearchTextChanged(newText: String) {
        searchText.value = newText
    }

    @OptIn(FlowPreview::class)
    fun subscribeToLocationAndSearchChanges() {
        viewModelScope.launch {
            combine(
                searchText.debounce(500),
                selectedLocation.filterNotNull(),
                currentRadius,
                mapMoving
            ) { searchText, latLng, radius, mapMoving ->
                searchJob?.cancel()
                if (!mapMoving) searchJob = launch {
                    val response = if (searchText.isBlank()) {
                        repository.searchRestaurantsByLocation(latLng, radius)
                    } else {
                        repository.searchRestaurantsBySearchTermAndLocation(
                            searchText,
                            latLng,
                            radius
                        )
                    }
                    if (response is Response.Error) handleError(response.error)
                }
            }.collect()
        }
    }

    private fun handleError(error: Throwable) {
        when (error) {
            is CancellationException -> Timber.d("Update job cancelled")
            is UnknownHostException -> errorMessage.value =
                resourceProvider.getString(R.string.network_error_message)
            else -> errorMessage.value = String.format(
                resourceProvider.getString(R.string.generic_error_message),
                error.message ?: ""
            )
        }
    }
}

enum class ScreenState {
    LOADING,
    DATA,
    ERROR
}