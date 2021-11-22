package com.rebeccablum.alltrailsatlunch.data

import com.rebeccablum.alltrailsatlunch.BuildConfig.PLACES_API_KEY
import retrofit2.http.GET
import retrofit2.http.Query


interface PlacesService {

    @GET("nearbysearch/json")
    suspend fun searchNearbyRestaurants(
        @Query("location", encoded = true) location: String, // <lat>%<long>
        @Query("type") type: String = "restaurant",
        @Query("keyword") keyword: String = "lunch",
        @Query("radius") radius: Int = 200,
        @Query("key") apiKey: String = PLACES_API_KEY
    ): SearchRestaurantsResponse

    @GET("textsearch/json")
    suspend fun searchRestaurantsByText(
        @Query("query") searchText: String,
        @Query("location", encoded = true) location: String, // <lat>%<long>
        @Query("type") type: String = "restaurant",
        @Query("radius") radius: Int = 200,
        @Query("key") apiKey: String = PLACES_API_KEY
    ): SearchRestaurantsResponse
}