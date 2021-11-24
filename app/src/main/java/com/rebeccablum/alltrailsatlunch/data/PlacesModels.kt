package com.rebeccablum.alltrailsatlunch.data

import com.google.gson.annotations.SerializedName

data class SearchRestaurantsResponse(
    @SerializedName("results") val results: List<Place>
)

data class Place(
    @SerializedName("place_id") val id: String,
    @SerializedName("formatted_address") val address: String? = null,
    @SerializedName("formatted_phone_number") val phoneNumber: String? = null,
    @SerializedName("geometry") val geometry: Geometry,
    @SerializedName("name") val name: String,
    @SerializedName("photos") val placePhotos: List<PlacePhoto>? = null,
    @SerializedName("rating") val rating: Double? = null,
    @SerializedName("user_ratings_total") val numRatings: Int? = null,
    @SerializedName("price_level") val priceLevel: Int? = null
)

data class Geometry(
    @SerializedName("location") val location: LatLng
)

data class LatLng(
    @SerializedName("lat") val latitude: Double,
    @SerializedName("lng") val longitude: Double
)

data class PlacePhoto(
    @SerializedName("photo_reference") val photoId: String
)