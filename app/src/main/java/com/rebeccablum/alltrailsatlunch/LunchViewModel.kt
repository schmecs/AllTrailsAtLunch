package com.rebeccablum.alltrailsatlunch

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rebeccablum.alltrailsatlunch.data.LatLng
import com.rebeccablum.alltrailsatlunch.data.LunchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LunchViewModel @Inject constructor(private val repository: LunchRepository) : ViewModel() {
    val hasLocationPermission = mutableStateOf(false)
    val nearbyRestaurants = repository.nearbyRestaurants

    init {
        viewModelScope.launch {
            repository.updateNearbyRestaurants(LatLng(-33.8670522, 151.1957362))
        }
    }
}