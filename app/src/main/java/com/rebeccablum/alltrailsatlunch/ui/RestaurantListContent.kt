package com.rebeccablum.alltrailsatlunch.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rebeccablum.alltrailsatlunch.data.LatLng
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