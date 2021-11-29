package com.rebeccablum.alltrailsatlunch.ui

import com.rebeccablum.alltrailsatlunch.CoroutineTestRule
import com.rebeccablum.alltrailsatlunch.GENERIC_ERROR
import com.rebeccablum.alltrailsatlunch.LAT_LNG_1
import com.rebeccablum.alltrailsatlunch.NETWORK_ERROR
import com.rebeccablum.alltrailsatlunch.R
import com.rebeccablum.alltrailsatlunch.RESTAURANT_1
import com.rebeccablum.alltrailsatlunch.RESTAURANT_2
import com.rebeccablum.alltrailsatlunch.SEARCH_TEXT
import com.rebeccablum.alltrailsatlunch.data.LocationService
import com.rebeccablum.alltrailsatlunch.data.LunchRepository
import com.rebeccablum.alltrailsatlunch.data.Response
import com.rebeccablum.alltrailsatlunch.util.ResourceProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.UnknownHostException

@OptIn(ExperimentalCoroutinesApi::class)
class LunchViewModelTest {

    private lateinit var subject: LunchViewModel
    private val lunchRepository = mockk<LunchRepository>(relaxed = true)
    private val locationService = mockk<LocationService>(relaxed = true)
    private val resourceProvider = mockk<ResourceProvider>().apply {
        every { getString(R.string.network_error_message) } returns NETWORK_ERROR
        every { getString(R.string.generic_error_message) } returns GENERIC_ERROR
    }

    @get:Rule
    val coroutinesRule = CoroutineTestRule()

    @Before
    fun setUp() {
        subject = LunchViewModel(lunchRepository, locationService, resourceProvider)
    }

    @Test
    fun `given no selected location does not call repo`() {
        subject.subscribeToLocationAndSearchChanges()
        coroutinesRule.testDispatcher.advanceTimeBy(500) // debounce
        coVerify(exactly = 0) { lunchRepository.searchRestaurantsByLocation(any(), any()) }
    }

    @Test
    fun `given device location calls repo with correct method`() {
        subject.subscribeToLocationAndSearchChanges()

        subject.selectedLocation.value = LAT_LNG_1
        coroutinesRule.testDispatcher.advanceTimeBy(500)

        coVerify { lunchRepository.searchRestaurantsByLocation(LAT_LNG_1, 200) }
    }

    @Test
    fun `given device location and search text calls repo with correct method`() {
        subject.subscribeToLocationAndSearchChanges()

        subject.selectedLocation.value = LAT_LNG_1
        subject.searchText.value = SEARCH_TEXT
        coroutinesRule.testDispatcher.advanceTimeBy(500)

        coVerify {
            lunchRepository.searchRestaurantsBySearchTermAndLocation(
                SEARCH_TEXT,
                LAT_LNG_1,
                200
            )
        }
    }

    @Test
    fun `given device location and search text change within debounce, only calls repo with latest`() {
        val newText = "new search text"
        subject.subscribeToLocationAndSearchChanges()

        subject.selectedLocation.value = LAT_LNG_1
        subject.searchText.value = SEARCH_TEXT
        coroutinesRule.testDispatcher.advanceTimeBy(200)
        subject.searchText.value = newText
        coroutinesRule.testDispatcher.advanceTimeBy(500)

        coVerify {
            lunchRepository.searchRestaurantsBySearchTermAndLocation(
                newText,
                LAT_LNG_1,
                200
            )
        }
        coVerify(exactly = 0) {
            lunchRepository.searchRestaurantsBySearchTermAndLocation(
                SEARCH_TEXT,
                LAT_LNG_1,
                200
            )
        }
    }

    @Test
    fun `given repository returns success from location restaurants are updated`() {
        subject.subscribeToLocationAndSearchChanges()
        coEvery {
            lunchRepository.searchRestaurantsByLocation(
                LAT_LNG_1,
                200
            )
        } returns Response.Success(listOf(RESTAURANT_1, RESTAURANT_2))

        subject.selectedLocation.value = LAT_LNG_1
        coroutinesRule.testDispatcher.advanceTimeBy(500)

        assert(subject.nearbyRestaurants.value == listOf(RESTAURANT_1, RESTAURANT_2))
    }

    @Test
    fun `given repository returns success with search text restaurants are updated`() {
        subject.subscribeToLocationAndSearchChanges()
        coEvery {
            lunchRepository.searchRestaurantsBySearchTermAndLocation(
                SEARCH_TEXT,
                LAT_LNG_1,
                200
            )
        } returns Response.Success(listOf(RESTAURANT_1, RESTAURANT_2))

        subject.selectedLocation.value = LAT_LNG_1
        subject.searchText.value = SEARCH_TEXT
        coroutinesRule.testDispatcher.advanceTimeBy(500)

        assert(subject.nearbyRestaurants.value == listOf(RESTAURANT_1, RESTAURANT_2))
    }

    @Test
    fun `given repository returns network error message is updated`() {
        subject.subscribeToLocationAndSearchChanges()
        coEvery {
            lunchRepository.searchRestaurantsByLocation(
                any(),
                any()
            )
        } returns Response.Error(UnknownHostException())

        subject.selectedLocation.value = LAT_LNG_1
        coroutinesRule.testDispatcher.advanceTimeBy(500)

        assert(subject.errorMessage.value == NETWORK_ERROR)
    }

    @Test
    fun `given location service returns location, viewmodel updates flow`() {
        coEvery { locationService.getCurrentLocation() } returns LAT_LNG_1
        subject.updateCurrentLocation()
        assert(subject.selectedLocation.value == LAT_LNG_1)
    }

    @Test
    fun `given map is moving, search is not executed`() {
        subject.subscribeToLocationAndSearchChanges()
        subject.selectedLocation.value = LAT_LNG_1

        subject.onMapMoving()
        coroutinesRule.testDispatcher.advanceTimeBy(500)

        verify { locationService.cancelActiveRequest() }
        coVerify(exactly = 0) { lunchRepository.searchRestaurantsByLocation(any(), any()) }
    }

    @Test
    fun `given map becomes idle, flow is updated and search is executed`() {
        subject.subscribeToLocationAndSearchChanges()

        subject.onMapIdle(LAT_LNG_1, 1400)
        coroutinesRule.testDispatcher.advanceTimeBy(500)

        assert(subject.selectedLocation.value == LAT_LNG_1)
        coVerify { lunchRepository.searchRestaurantsByLocation(LAT_LNG_1, 1400) }
    }

    @Test
    fun `given search text changed, flow is updated`() {
        val newSearch = "new search"
        subject.searchText.value = SEARCH_TEXT
        subject.onSearchTextChanged(newSearch)
        assert(subject.searchText.value == newSearch)
    }
}