package com.rebeccablum.alltrailsatlunch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.rebeccablum.alltrailsatlunch.R
import com.rebeccablum.alltrailsatlunch.data.LocationService
import com.rebeccablum.alltrailsatlunch.data.LunchRepository
import com.rebeccablum.alltrailsatlunch.data.Response
import com.rebeccablum.alltrailsatlunch.models.Restaurant
import com.rebeccablum.alltrailsatlunch.util.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private val _nearbyRestaurants = MutableStateFlow<List<Restaurant>>(emptyList())
    val nearbyRestaurants: StateFlow<List<Restaurant>> = _nearbyRestaurants

    val selectedLocation = MutableStateFlow<LatLng?>(null)
    private val currentRadius = MutableStateFlow(200)
    private val mapMoving = MutableStateFlow(false)
    val updatingSearch = MutableStateFlow(false)
    val searchText = MutableStateFlow("")
    val errorMessage = MutableStateFlow<String?>(null)

    private var getCurrentLocationJob: Job? = null
    private var searchJob: Job? = null

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
                    updatingSearch.value = true
                    val response = if (searchText.isBlank()) {
                        repository.searchRestaurantsByLocation(latLng, radius)
                    } else {
                        repository.searchRestaurantsBySearchTermAndLocation(
                            searchText,
                            latLng,
                            radius
                        )
                    }
                    when (response) {
                        is Response.Success -> _nearbyRestaurants.value = response.value
                        is Response.Error -> handleError(response.error)
                    }
                }
                searchJob?.join()
                updatingSearch.value = false
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