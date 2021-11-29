package com.rebeccablum.alltrailsatlunch.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.LatLng
import com.rebeccablum.alltrailsatlunch.models.Restaurant
import com.rebeccablum.alltrailsatlunch.ui.RestaurantItem
import com.rebeccablum.alltrailsatlunch.ui.compose.style.LunchTheme

@Composable
fun LoadingLayer(isLoading: Boolean, content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        content()
        if (isLoading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = .2f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Preview
@Composable
fun LoadingLayerPreview() {
    LunchTheme {
        LoadingLayer(isLoading = true) {
            RestaurantItem(
                restaurant = Restaurant(
                    "1",
                    "Red Stripe",
                    location = LatLng(41.8298447, -71.38924539999999),
                    stars = 3,
                    numRatings = 232,
                    priceLevel = 2
                )
            ) {}
        }
    }
}