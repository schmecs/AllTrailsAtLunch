package com.rebeccablum.alltrailsatlunch.models

import com.google.android.gms.maps.model.LatLng
import com.rebeccablum.alltrailsatlunch.BuildConfig
import com.rebeccablum.alltrailsatlunch.data.SearchRestaurantsResponse
import kotlin.math.roundToInt

data class Restaurant(
    val id: String,
    val name: String,
    val address: String? = null,
    val phone: String? = null,
    val location: LatLng,
    val photoUrl: String? = null,
    val stars: Int? = null,
    val numRatings: Int? = null,
    val priceLevel: Int? = null
)

fun SearchRestaurantsResponse.toDomainModel(): List<Restaurant> {
    return results.map {
        Restaurant(
            it.id,
            it.name,
            it.address,
            it.phoneNumber,
            LatLng(it.geometry.location.latitude, it.geometry.location.longitude),
            it.placePhotos?.firstOrNull()?.photoId?.let(::getPlacePhotoUrl),
            it.rating.roundToInt(),
            it.numRatings,
            it.priceLevel
        )
    }
}

fun getPlacePhotoUrl(reference: String): String {
    return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=$reference&key=${BuildConfig.PLACES_API_KEY}"
}