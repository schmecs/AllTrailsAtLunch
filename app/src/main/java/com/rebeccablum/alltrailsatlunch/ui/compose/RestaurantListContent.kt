package com.rebeccablum.alltrailsatlunch.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.rebeccablum.alltrailsatlunch.models.Restaurant

@Composable
fun RestaurantList(restaurants: List<Restaurant>, closeKeyboard: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 48.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            items(restaurants) { restaurant ->
                RestaurantItem(restaurant = restaurant, onItemClick = closeKeyboard)
            }
        }
    }
}

@Preview
@Composable
fun RestaurantListPreview() {
    MaterialTheme {
        RestaurantList(
            restaurants = listOf(
                Restaurant(
                    id = "123",
                    name = "My Restaurant",
                    location = LatLng(40.0, 40.0),
                    stars = 3,
                    numRatings = 25,
                    priceLevel = 2
                ),
                Restaurant(
                    id = "124",
                    name = "My Other Restaurant",
                    location = LatLng(40.0, 40.0),
                    stars = 4,
                    numRatings = 10,
                    priceLevel = 3
                )
            ),
            {}
        )
    }
}