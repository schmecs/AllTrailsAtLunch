package com.rebeccablum.alltrailsatlunch.data

import com.rebeccablum.alltrailsatlunch.CoroutineTestRule
import com.rebeccablum.alltrailsatlunch.LAT_LNG_1
import com.rebeccablum.alltrailsatlunch.PLACE_1
import com.rebeccablum.alltrailsatlunch.PLACE_2
import com.rebeccablum.alltrailsatlunch.SEARCH_TEXT
import com.rebeccablum.alltrailsatlunch.TestDispatcherProvider
import com.rebeccablum.alltrailsatlunch.models.toDomainModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

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

    @Test
    fun `searchRestaurantsByLocation given success response updates restaurants`() {
        coEvery {
            placesService.searchNearbyRestaurants(
                LAT_LNG_1.formattedAsQuery(),
                radius = 200
            )
        } returns RESTAURANT_RESPONSE
        runBlockingTest {
            val response = subject.searchRestaurantsByLocation(LAT_LNG_1, 200)
            assert(response is Response.Success)
        }
        assert(subject.nearbyRestaurants.value == RESTAURANT_RESPONSE.toDomainModel())
    }

    @Test
    fun `searchRestaurantsByLocation given error response returns error`() {
        coEvery {
            placesService.searchNearbyRestaurants(
                LAT_LNG_1.formattedAsQuery(),
                radius = 200
            )
        } throws IllegalArgumentException()
        runBlockingTest {
            val response = subject.searchRestaurantsByLocation(LAT_LNG_1, 200)
            assert(response is Response.Error)
        }
        assert(subject.nearbyRestaurants.value.isNullOrEmpty())
    }

    @Test
    fun `searchRestaurantsBySearchTermAndLocation given success response updates restaurants`() {
        coEvery {
            placesService.searchRestaurantsByText(
                SEARCH_TEXT,
                LAT_LNG_1.formattedAsQuery(),
                radius = 200
            )
        } returns RESTAURANT_RESPONSE
        runBlockingTest {
            subject.searchRestaurantsBySearchTermAndLocation(SEARCH_TEXT, LAT_LNG_1, 200)
        }
        assert(subject.nearbyRestaurants.value == RESTAURANT_RESPONSE.toDomainModel())
    }

    @Test
    fun `searchRestaurantsBySearchTermAndLocation given error response returns error`() {
        val searchTerm = "italian"
        coEvery {
            placesService.searchRestaurantsByText(
                searchTerm,
                LAT_LNG_1.formattedAsQuery(),
                radius = 200
            )
        } throws IllegalArgumentException()
        runBlockingTest {
            val response =
                subject.searchRestaurantsBySearchTermAndLocation(searchTerm, LAT_LNG_1, 200)
            assert(response is Response.Error)
        }
        assert(subject.nearbyRestaurants.value.isNullOrEmpty())
    }
}