package com.rebeccablum.alltrailsatlunch.data

import com.google.android.gms.maps.model.LatLng
import com.rebeccablum.alltrailsatlunch.CoroutineTestRule
import com.rebeccablum.alltrailsatlunch.TestDispatcherProvider
import com.rebeccablum.alltrailsatlunch.models.toDomainModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private val LAT_LNG_1 = LatLng(41.1234, 73.1234)
private val LAT_LNG_2 = LatLng(41.1235, 73.1235)
private val PLACE_1 = Place(
    "1", "123 Sesame Street", "800-555-5555", Geometry(
        com.rebeccablum.alltrailsatlunch.data.LatLng(
            LAT_LNG_1.latitude, LAT_LNG_1.longitude
        )
    ), "Place 1"
)
private val PLACE_2 = Place(
    "2", "14 Elm Street", "123-456-7890", Geometry(
        com.rebeccablum.alltrailsatlunch.data.LatLng(
            LAT_LNG_2.latitude, LAT_LNG_2.longitude
        )
    ), "Place 2"
)
private val RESTAURANT_RESPONSE = SearchRestaurantsResponse(listOf(PLACE_1, PLACE_2))

@OptIn(ExperimentalCoroutinesApi::class)
class LunchRepositoryTest {

    @get:Rule
    val coroutinesRule = CoroutineTestRule()

    private lateinit var subject: LunchRepository
    private val placesService = mockk<PlacesService>()

    @Before
    fun setUp() {
        subject = LunchRepository(placesService, TestDispatcherProvider())
    }

    @After
    fun cleanUp() {

    }

    @Test
    fun `searchRestaurantsByLocation given success response updates restaurants`() {
        coEvery { placesService.searchNearbyRestaurants(LAT_LNG_1.formattedAsQuery()) } returns RESTAURANT_RESPONSE
        runBlockingTest {
            val response = subject.searchRestaurantsByLocation(LAT_LNG_1)
            assert(response is Response.Success)
        }
        assert(subject.nearbyRestaurants.value == RESTAURANT_RESPONSE.toDomainModel())
    }

    @Test
    fun `searchRestaurantsByLocation given error response returns error`() {
        coEvery { placesService.searchNearbyRestaurants(LAT_LNG_1.formattedAsQuery()) } throws IllegalArgumentException()
        runBlockingTest {
            val response = subject.searchRestaurantsByLocation(LAT_LNG_1)
            assert(response is Response.Error)
        }
        assert(subject.nearbyRestaurants.value.isNullOrEmpty())
    }

    @Test
    fun `searchRestaurantsBySearchTermAndLocation given success response updates restaurants`() {
        val searchTerm = "italian"
        coEvery {
            placesService.searchRestaurantsByText(
                searchTerm,
                LAT_LNG_1.formattedAsQuery()
            )
        } returns RESTAURANT_RESPONSE
        runBlockingTest {
            subject.searchRestaurantsBySearchTermAndLocation(searchTerm, LAT_LNG_1)
        }
        assert(subject.nearbyRestaurants.value == RESTAURANT_RESPONSE.toDomainModel())
    }

    @Test
    fun `searchRestaurantsBySearchTermAndLocation given error response returns error`() {
        val searchTerm = "italian"
        coEvery {
            placesService.searchRestaurantsByText(
                searchTerm,
                LAT_LNG_1.formattedAsQuery()
            )
        } throws IllegalArgumentException()
        runBlockingTest {
            val response = subject.searchRestaurantsBySearchTermAndLocation(searchTerm, LAT_LNG_1)
            assert(response is Response.Error)
        }
        assert(subject.nearbyRestaurants.value.isNullOrEmpty())
    }
}