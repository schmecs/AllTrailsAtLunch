package com.rebeccablum.alltrailsatlunch.ui.compose

import android.widget.Toast
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rebeccablum.alltrailsatlunch.ui.LunchViewModel
import com.rebeccablum.alltrailsatlunch.ui.RestaurantList
import com.rebeccablum.alltrailsatlunch.ui.RestaurantMap
import timber.log.Timber

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LunchNavHost(
    navController: NavHostController,
    lunchViewModel: LunchViewModel
) {
    val restaurants = lunchViewModel.nearbyRestaurants.collectAsState()
    val userLocation = lunchViewModel.userLocation.collectAsState(null)
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
            RestaurantMap(
                latLng = userLocation.value,
                restaurants = restaurants.value,
                onMapMoving = { lunchViewModel.onMapMoving() },
                onMapIdle = { lunchViewModel.onMapIdle(it) },
                onMyLocationButtonClick = { lunchViewModel.updateCurrentLocation() },
                closeKeyboard = { keyboardController?.hide() }
            )
        }
        composable("restaurantList") {
            RestaurantList(
                restaurants = restaurants.value,
                closeKeyboard = { keyboardController?.hide() }
            )
        }
    }
}

// TODO change title to string resource
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object MapView : Screen("map", "Map", Icons.Filled.Place)
    object RestaurantList : Screen("restaurantList", "Results", Icons.Filled.List)
}

val bottomNavItems = listOf(Screen.MapView, Screen.RestaurantList)

//https://developer.android.com/jetpack/compose/navigation#bottom-nav
@Composable
fun LunchBottomNav(navController: NavHostController) {
    BottomNavigation {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        bottomNavItems.forEach { screen ->
            BottomNavigationItem(
                icon = { Icon(screen.icon, contentDescription = null) },
                label = { Text(screen.title) },
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