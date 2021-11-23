package com.rebeccablum.alltrailsatlunch.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        Row(modifier = Modifier.padding(16.dp)) {
            val imageUrl = restaurant.photoUrl ?: "https://www.example.com/image.jpg"
            Image(
                painter = rememberImagePainter(imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Top)
                    .size(128.dp)
                    .padding(8.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(8.dp)
            ) {
                Text(restaurant.name)
                // todo extract to model
                restaurant.stars?.let {
                    Row(modifier = Modifier.wrapContentSize()) {
                        (1..5).forEach {
                            Box(modifier = Modifier.wrapContentSize()) {
                                if (it < restaurant.stars) {
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
                            text = "(${restaurant.numRatings})",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 4.dp)
                        )
                    }
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