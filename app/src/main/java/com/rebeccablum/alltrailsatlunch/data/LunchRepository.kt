package com.rebeccablum.alltrailsatlunch.data

import com.google.android.gms.maps.model.LatLng
import com.rebeccablum.alltrailsatlunch.models.Restaurant
import com.rebeccablum.alltrailsatlunch.models.toDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

// TODO dispatcher provider
@Singleton
class LunchRepository @Inject constructor(private val placesService: PlacesService) {
    private val _nearbyRestaurants = MutableStateFlow<List<Restaurant>>(emptyList())
    val nearbyRestaurants: StateFlow<List<Restaurant>> = _nearbyRestaurants

    suspend fun searchRestaurantsByLocation(
        currentLocation: LatLng
    ): Response<Unit> {
        return try {
            val newData = withContext(Dispatchers.IO) {
                placesService.searchNearbyRestaurants(
                    location = currentLocation.formattedAsQuery()
                )
            }
            _nearbyRestaurants.value = newData.toDomainModel()
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(e)
        }
    }

    suspend fun searchRestaurantsBySearchTermAndLocation(
        searchString: String,
        currentLocation: LatLng
    ): Response<Unit> {
        return try {
            val newData = withContext(Dispatchers.IO) {
                placesService.searchRestaurantsByText(
                    searchText = searchString,
                    location = currentLocation.formattedAsQuery()
                )
            }
            _nearbyRestaurants.value = newData.toDomainModel()
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(e)
        }
    }
}

sealed class Response<T> {
    class Success<T>(val value: T) : Response<T>()
    class Error<T>(val error: Throwable) : Response<T>()
}

fun LatLng.formattedAsQuery(): String {
    return "$latitude%2C$longitude"
}