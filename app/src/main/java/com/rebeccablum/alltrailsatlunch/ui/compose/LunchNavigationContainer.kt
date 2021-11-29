package com.rebeccablum.alltrailsatlunch.ui.compose

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rebeccablum.alltrailsatlunch.R
import com.rebeccablum.alltrailsatlunch.ui.LunchViewModel
import com.rebeccablum.alltrailsatlunch.ui.MapViewContent
import com.rebeccablum.alltrailsatlunch.ui.RestaurantList
import timber.log.Timber
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LunchNavigationContainer(
    navController: NavHostController,
    lunchViewModel: LunchViewModel
) {
    val restaurants = lunchViewModel.nearbyRestaurants.collectAsState()
    val userLocation = lunchViewModel.selectedLocation.collectAsState(null)
    val errorMessage = lunchViewModel.errorMessage.collectAsState().value
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    errorMessage?.let {
        LaunchedEffect(errorMessage) {
            Timber.e(errorMessage)
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }
    NavHost(navController = navController, startDestination = "map") {
        composable("map") {
            LoadingLayer(isLoading = lunchViewModel.updatingSearch.collectAsState().value) {
                MapViewContent(
                    latLng = userLocation.value,
                    restaurants = restaurants.value,
                    onMapMoving = { lunchViewModel.onMapMoving() },
                    onMapIdle = { latLng, radius ->
                        lunchViewModel.onMapIdle(
                            latLng,
                            radius.roundToInt()
                        )
                    },
                    onMyLocationButtonClick = { lunchViewModel.updateCurrentLocation() },
                    closeKeyboard = { keyboardController?.hide() }
                )
            }
        }
        composable("restaurantList") {
            LoadingLayer(isLoading = lunchViewModel.updatingSearch.collectAsState().value) {
                RestaurantList(
                    restaurants = restaurants.value,
                    closeKeyboard = { keyboardController?.hide() }
                )
            }
        }
    }
}

sealed class Screen(val route: String, val titleRes: Int, val icon: ImageVector) {
    object MapView : Screen("map", R.string.map_nav_item, Icons.Filled.Place)
    object RestaurantList : Screen("restaurantList", R.string.results_nav_item, Icons.Filled.List)
}

val bottomNavItems = listOf(Screen.MapView, Screen.RestaurantList)

//https://developer.android.com/jetpack/compose/navigation#bottom-nav
@Composable
fun LunchBottomNav(navController: NavHostController) {
    BottomNavigation(modifier = Modifier.wrapContentHeight()) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        bottomNavItems.forEach { screen ->
            BottomNavigationItem(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterVertically),
                icon = { Icon(screen.icon, contentDescription = null) },
                label = { Text(text = stringResource(id = screen.titleRes)) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}