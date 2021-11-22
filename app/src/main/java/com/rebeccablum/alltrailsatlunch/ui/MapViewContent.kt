package com.rebeccablum.alltrailsatlunch.ui

import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.rebeccablum.alltrailsatlunch.R
import com.rebeccablum.alltrailsatlunch.models.Restaurant

/**
 * With help from https://johnoreilly.dev/posts/jetpack-compose-google-maps/
 */
@Composable
fun RestaurantMap(
    latLng: LatLng?,
    restaurants: List<Restaurant>,
    onMapMoved: (LatLng) -> Unit,
    onMyLocationButtonClick: () -> Unit,
    onMarkerClicked: (String) -> Unit
) {
    val googleMapView = rememberMapViewWithLifecycle()
    val context = LocalContext.current
    val readRestaurantsForCompose = restaurants
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 48.dp)
    ) {
        if (latLng == null) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            AndroidView({ googleMapView }) {
                it.getMapAsync { map ->
                    map.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                            context,
                            R.raw.map_style_json
                        )
                    )
                    map.uiSettings.isTiltGesturesEnabled = false
                    map.uiSettings.isZoomGesturesEnabled =
                        false // Ideally we'd use bounds to redefine the search, but the interaction of compose / mapView made this too difficult for the timeline
                    if (latLng != map.cameraPosition.target) {
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.5f))
                    }
                    map.clear()
                    restaurants.forEach { restaurant ->
                        val markerOptions = MarkerOptions().position(restaurant.location)
                        map.addMarker(markerOptions)?.apply { tag = restaurant.id }
                    }
                    map.setOnCameraIdleListener {
                        onMapMoved(map.cameraPosition.target)
                    }
                    map.setOnMarkerClickListener { marker -> onMarkerClicked(marker.tag as String); true }
                }
            }
        }
    }
}

//https://medium.com/geekculture/google-maps-in-jetpack-compose-android-ae7b1ad84e9
@Composable
fun rememberMapViewWithLifecycle(): com.google.android.gms.maps.MapView {
    val context = LocalContext.current
    val mapView = remember {
        com.google.android.gms.maps.MapView(context)
    }

    // Makes MapView follow the lifecycle of this composable
    val lifecycleObserver = rememberMapLifecycleObserver(mapView)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }

    return mapView
}

@Composable
fun rememberMapLifecycleObserver(mapView: com.google.android.gms.maps.MapView): LifecycleEventObserver =
    remember(mapView) {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> throw IllegalStateException()
            }
        }
    }