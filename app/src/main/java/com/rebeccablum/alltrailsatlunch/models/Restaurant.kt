package com.rebeccablum.alltrailsatlunch.models

import com.rebeccablum.alltrailsatlunch.data.GetNearbyRestaurantsResponse
import com.rebeccablum.alltrailsatlunch.data.LatLng

data class Restaurant(
    val id: String,
    val name: String,
    val address: String? = null,
    val phone: String? = null,
    val location: LatLng,
    val photoId: String? = null
)

fun GetNearbyRestaurantsResponse.toDomainModel(): List<Restaurant> {
    return results.map {
        Restaurant(
            it.id,
            it.name,
            it.address,
            it.phoneNumber,
            it.geometry.location,
            it.placePhotos.firstOrNull()?.photoId
        )
    }
}