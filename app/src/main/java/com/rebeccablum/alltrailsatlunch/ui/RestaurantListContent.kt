package com.rebeccablum.alltrailsatlunch.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.rebeccablum.alltrailsatlunch.models.Restaurant

@Composable
fun RestaurantList(restaurants: List<Restaurant>) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            items(restaurants) { restaurant ->
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .wrapContentSize()
                ) {
                    Text(text = restaurant.name)
                }
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
                    location = LatLng(40.0, 40.0)
                )
            )
        )
    }
}