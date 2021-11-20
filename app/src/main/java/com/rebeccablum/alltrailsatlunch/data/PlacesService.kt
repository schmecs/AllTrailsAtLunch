package com.rebeccablum.alltrailsatlunch.data

import com.rebeccablum.alltrailsatlunch.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query


interface PlacesService {
    @GET("nearbysearch/json")
    suspend fun getNearbyRestaurants(
        @Query("location", encoded = true) location: String, // <lat>%<long>
        @Query("type") type: String = "restaurant",
        @Query("radius") radius: Int = 1500,
        @Query("key") apiKey: String = BuildConfig.PLACES_API_KEY
    ): GetNearbyRestaurantsResponse

    // TODO
    // endpoint "photo" with query maxwidth, photo_reference, key
}