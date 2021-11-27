package com.rebeccablum.alltrailsatlunch.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.google.android.gms.maps.model.LatLng
import com.rebeccablum.alltrailsatlunch.models.Restaurant

@Composable
fun RestaurantItem(restaurant: Restaurant, onItemClick: () -> Unit) {
    Card(modifier = Modifier
        .padding(8.dp)
        .clickable { onItemClick() }) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Image if url available or generic icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(.35f)
            ) {
                if (restaurant.photoUrl != null) {
                    ImageLoader(imageUrl = restaurant.photoUrl)
                } else {
                    Icon(
                        Icons.Filled.Restaurant,
                        null,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                            .height(64.dp),
                        tint = Color.LightGray
                    )
                }
            }
            // Name, rating and price info
            Box(
                modifier = Modifier
                    .align(Alignment.Top)
                    .weight(.65f)
            ) {
                RestaurantDetails(restaurant = restaurant)
            }
        }
    }
}

@Composable
fun RestaurantDetails(restaurant: Restaurant) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
    ) {
        Text(restaurant.name)
        restaurant.stars?.let {
            StarRow(numStars = it, numRatings = restaurant.numRatings ?: 0)
        }
        restaurant.priceLevel?.let {
            val dollarSigns = StringBuilder("")
            repeat(it) { dollarSigns.append("$") }
            Row(modifier = Modifier.padding(4.dp)) {
                Text(dollarSigns.toString())
            }
        }
    }
}

// https://www.youtube.com/watch?v=_y3P2-ciEIE
@Composable
fun ImageLoader(imageUrl: String) {
    Image(
        painter = rememberImagePainter(imageUrl),
        contentDescription = null,
        modifier = Modifier
            .size(128.dp)
            .padding(8.dp),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun StarRow(numStars: Int, numRatings: Int) {
    Row(modifier = Modifier.wrapContentSize()) {
        (1..5).forEach {
            Box(modifier = Modifier.wrapContentSize()) {
                if (it < numStars) {
                    Icon(
                        Icons.Filled.Star,
                        null,
                        modifier = Modifier.padding(2.dp),
                        tint = Color.Green // TODO
                    )
                }
                Icon(
                    Icons.Outlined.StarOutline,
                    null,
                    modifier = Modifier.padding(2.dp),
                    tint = Color.DarkGray
                )
            }
        }
        Text(
            text = "($numRatings)",
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 4.dp)
        )
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
            ),
            {}
        )
    }
}