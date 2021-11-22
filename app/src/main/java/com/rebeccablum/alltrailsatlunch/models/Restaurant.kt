package com.rebeccablum.alltrailsatlunch.models

import com.google.android.gms.maps.model.LatLng
import com.rebeccablum.alltrailsatlunch.data.SearchRestaurantsResponse

data class Restaurant(
    val id: String,
    val name: String,
    val address: String? = null,
    val phone: String? = null,
    val location: LatLng,
    val photoUrl: String? = null
)

fun SearchRestaurantsResponse.toDomainModel(): List<Restaurant> {
    return results.map {
        Restaurant(
            it.id,
            it.name,
            it.address,
            it.phoneNumber,
            LatLng(it.geometry.location.latitude, it.geometry.location.longitude),
            it.placePhotos.firstOrNull()?.photoId
        )
    }
}