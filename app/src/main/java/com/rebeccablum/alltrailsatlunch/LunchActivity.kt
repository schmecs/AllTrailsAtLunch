package com.rebeccablum.alltrailsatlunch

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.rebeccablum.alltrailsatlunch.data.LatLng
import com.rebeccablum.alltrailsatlunch.models.Restaurant
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LunchActivity : ComponentActivity() {

    private val viewModel: LunchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) -> viewModel.hasLocationPermission.value = true
            }
            MaterialTheme {
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (!viewModel.hasLocationPermission.value) {
                        // https://stackoverflow.com/questions/60608101/how-request-permissions-with-jetpack-compose
                        val launcher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.RequestPermission()
                        ) { isGranted ->
                            if (isGranted) viewModel.hasLocationPermission.value = true
                        }
                        // TODO handle rationale / nav to app settings
                        AlertDialog(
                            onDismissRequest = { },
                            buttons = {
                                Button(
                                    onClick = { launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
                                    content = { Text("Set location permissions") })
                            }
                        )
                    }
                    RestaurantList(restaurants = viewModel.nearbyRestaurants.collectAsState().value)
                }
            }
        }
    }
}

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