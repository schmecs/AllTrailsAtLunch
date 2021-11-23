package com.rebeccablum.alltrailsatlunch.ui

import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
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
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
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
    onMapMoving: () -> Unit,
    onMapIdle: (LatLng) -> Unit,
    onMyLocationButtonClick: () -> Unit,
    closeKeyboard: () -> Unit
) {
    val currentRestaurantInfo = remember { mutableStateOf<Restaurant?>(null) }
    val googleMapView =
        rememberMapViewWithLifecycle(onMarkerClick = {
            closeKeyboard()
            val restaurant = restaurants.firstOrNull { r -> r.id == it.tag }
            currentRestaurantInfo.value = restaurant
        })
    val context = LocalContext.current

    Column {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (currentRestaurantInfo.value == null) 48.dp else 0.dp)
        ) {
            if (latLng == null) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator()
                }
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
                        if (latLng != map.cameraPosition.target) {
                            if (map.cameraPosition.zoom < 14f) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                            } else {
                                map.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                            }
                        }
                        map.clear()
                        restaurants.forEach { restaurant ->
                            val markerOptions =
                                MarkerOptions().position(restaurant.location).title(restaurant.name)
                            map.addMarker(markerOptions)?.apply { tag = restaurant.id }
                        }
                        map.setOnCameraMoveListener {
                            onMapMoving()
                            closeKeyboard()
                        }
                        map.setOnCameraIdleListener {
                            onMapIdle(map.cameraPosition.target)
                        }
                    }
                }
            }
        }
        currentRestaurantInfo.value?.let {
            Box(
                modifier = Modifier
                    .padding(bottom = 48.dp)
                    .wrapContentSize()
            ) {
                // TODO do we want to do anything on click?
                RestaurantItem(restaurant = it, onItemClick = {})
            }
        }
    }
}

//https://medium.com/geekculture/google-maps-in-jetpack-compose-android-ae7b1ad84e9
@Composable
fun rememberMapViewWithLifecycle(onMarkerClick: (Marker) -> Unit): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context)
    }.apply {
        getMapAsync { map ->
            map.setOnMarkerClickListener {
                onMarkerClick(it); true
            }
        }
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