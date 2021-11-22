package com.rebeccablum.alltrailsatlunch.data

import com.google.gson.annotations.SerializedName

data class SearchRestaurantsResponse(
    @SerializedName("results") val results: List<Place>
)

data class Place(
    @SerializedName("place_id") val id: String,
    @SerializedName("formatted_address") val address: String,
    @SerializedName("formatted_phone_number") val phoneNumber: String,
    @SerializedName("geometry") val geometry: Geometry,
    @SerializedName("name") val name: String,
    @SerializedName("photos") val placePhotos: List<PlacePhoto>?,
    @SerializedName("rating") val rating: Double,
    @SerializedName("user_ratings_total") val numRatings: Int,
    @SerializedName("price_level") val priceLevel: Int
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