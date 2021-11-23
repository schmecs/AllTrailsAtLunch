package com.rebeccablum.alltrailsatlunch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.rebeccablum.alltrailsatlunch.data.LocationService
import com.rebeccablum.alltrailsatlunch.data.LunchRepository
import com.rebeccablum.alltrailsatlunch.data.Response
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
    private val locationService: LocationService
) : ViewModel() {
    val nearbyRestaurants = repository.nearbyRestaurants

    val userLocation = MutableStateFlow<LatLng?>(null)
    val searchText = MutableStateFlow("")
    val errorMessage = MutableStateFlow<String?>(null)

    private var getCurrentLocationJob: Job? = null
    private var searchJob: Job? = null

    // TODO use lifecycle scope appropriately here somehow
    init {
        subscribeToLocationAndSearchChanges()
        updateCurrentLocation()
    }

    // TODO tie to user interaction
    fun updateCurrentLocation() {
        getCurrentLocationJob?.cancel()
        getCurrentLocationJob = viewModelScope.launch {
            userLocation.value = locationService.getCurrentLocation()
        }
    }

    fun onMapMoving() {
        searchJob?.cancel()
    }

    fun onMapIdle(newCenter: LatLng) {
        userLocation.value = newCenter
    }

    fun onSearchTextChanged(newText: String) {
        searchText.value = newText
    }

    @OptIn(FlowPreview::class)
    fun subscribeToLocationAndSearchChanges() {
        viewModelScope.launch {
            combine(
                searchText.debounce(500),
                userLocation.filterNotNull()
            ) { searchText, latLng ->
                searchJob?.cancel()
                searchJob = launch {
                    val response = if (searchText.isBlank()) {
                        repository.searchRestaurantsByLocation(latLng)
                    } else {
                        repository.searchRestaurantsBySearchTermAndLocation(
                            searchText,
                            latLng
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
                "Check your network connection and try again."
            else -> errorMessage.value = "Something went wrong: $error"
        }
    }
}

enum class ScreenState {
    LOADING,
    DATA,
    ERROR
}