package com.rebeccablum.alltrailsatlunch.data

import com.google.android.gms.maps.model.LatLng
import com.rebeccablum.alltrailsatlunch.models.Restaurant
import com.rebeccablum.alltrailsatlunch.models.toDomainModel
import com.rebeccablum.alltrailsatlunch.util.DispatcherProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LunchRepository @Inject constructor(
    private val placesService: PlacesService,
    private val dispatcherProvider: DispatcherProvider
) {
    suspend fun searchRestaurantsByLocation(
        currentLocation: LatLng,
        currentRadius: Int
    ): Response<List<Restaurant>> {
        return try {
            val response = withContext(dispatcherProvider.io()) {
                placesService.searchNearbyRestaurants(
                    location = currentLocation.formattedAsQuery(),
                    radius = currentRadius
                )
            }
            Response.Success(response.toDomainModel())
        } catch (e: Exception) {
            Response.Error(e)
        }
    }

    suspend fun searchRestaurantsBySearchTermAndLocation(
        searchString: String,
        currentLocation: LatLng,
        currentRadius: Int
    ): Response<List<Restaurant>> {
        return try {
            val response = withContext(dispatcherProvider.io()) {
                placesService.searchRestaurantsByText(
                    searchText = searchString,
                    location = currentLocation.formattedAsQuery(),
                    radius = currentRadius
                )
            }
            Response.Success(response.toDomainModel())
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