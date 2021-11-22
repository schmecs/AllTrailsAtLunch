package com.rebeccablum.alltrailsatlunch.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.google.android.gms.maps.model.LatLng
import com.rebeccablum.alltrailsatlunch.models.Restaurant

@Composable
fun RestaurantItem(restaurant: Restaurant) {
    Card(modifier = Modifier.padding(8.dp)) {
        Row(modifier = Modifier.padding(16.dp)) {
            val imageUrl = restaurant.photoUrl ?: "https://www.example.com/image.jpg"
            Image(
                painter = rememberImagePainter(imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(128.dp)
                    .padding(8.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Text(restaurant.name)
                // todo extract to model
                restaurant.stars?.let {
                    Row(modifier = Modifier.wrapContentSize()) {
                        (1..5).forEach {
                            val icon =
                                if (it < restaurant.stars) Icons.Filled.Star else Icons.Outlined.Star
                            Icon(icon, null, modifier = Modifier.padding(2.dp))
                        }
                        Text(text = "(${restaurant.numRatings})")
                    }
                }
                restaurant.priceLevel?.let {
                    val dollarSigns = StringBuilder("")
                    repeat(it) { dollarSigns.append("$") }
                    Row {
                        Text(dollarSigns.toString())
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun RestaurantItemPreview() {
    MaterialTheme {
        RestaurantItem(
            restaurant = Restaurant(
                "1",
                "Red Stripe",
                location = LatLng(41.8298447, -71.38924539999999),
                stars = 3,
                numRatings = 232,
                priceLevel = 2
            )
        )
    }
}