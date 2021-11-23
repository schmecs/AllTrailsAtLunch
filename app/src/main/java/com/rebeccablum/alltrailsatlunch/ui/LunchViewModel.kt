package com.rebeccablum.alltrailsatlunch.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.rebeccablum.alltrailsatlunch.data.LocationService
import com.rebeccablum.alltrailsatlunch.data.LunchRepository
import com.rebeccablum.alltrailsatlunch.data.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LunchViewModel @Inject constructor(
    private val repository: LunchRepository,
    private val locationService: LocationService
) : ViewModel() {
    val showLocationPermissionPrompt = mutableStateOf(true)
    val nearbyRestaurants = repository.nearbyRestaurants

    val userLocation = MutableStateFlow<LatLng?>(null)
    val searchText = MutableStateFlow("")

    val errorMessage = mutableStateOf<String?>(null) // TODO

    private var getCurrentLocationJob: Job? = null
    var currentJob: Job? = null

    // TODO use lifecycle scope appropriately here somehow
    init {
        subscribeToLocationAndSearchChanges()
    }

    // TODO tie to user interaction
    fun updateCurrentLocation() {
        getCurrentLocationJob?.cancel()
        getCurrentLocationJob = viewModelScope.launch {
            userLocation.value = locationService.getCurrentLocation()
        }
    }

    fun onMapMoving() {
        currentJob?.cancel()
    }

    fun onMapMoved(newCenter: LatLng) {
        userLocation.value = newCenter
    }

    fun onSearchTextChanged(newText: String) {
        searchText.value = newText
    }

    private suspend fun updateRestaurantsBySearchTermAndUserLocation(
        searchTerm: String,
        latLng: LatLng
    ) {
        val response =
            repository.searchRestaurantsBySearchTermAndLocation(searchTerm, latLng)
        if (response is Response.Error) {
            errorMessage.value = "Something went wrong: ${response.error}"
        }
    }

    @OptIn(FlowPreview::class)
    fun subscribeToLocationAndSearchChanges() {
        viewModelScope.launch {
            combine(
                searchText.debounce(500),
                userLocation.filterNotNull()
            ) { searchText, latLng ->
                currentJob?.cancel()
                currentJob = launch {
                    val response = if (searchText.isBlank()) {
                        repository.searchRestaurantsByLocation(latLng)
                    } else {
                        repository.searchRestaurantsBySearchTermAndLocation(
                            searchText,
                            latLng
                        )
                    }
                    if (response is Response.Error) errorMessage.value =
                        "Something went wrong: $errorMessage"
                }
            }.collect()
        }
    }
}

enum class ScreenState {
    LOADING,
    DATA,
    ERROR
}