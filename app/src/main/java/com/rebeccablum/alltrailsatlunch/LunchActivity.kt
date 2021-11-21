package com.rebeccablum.alltrailsatlunch

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rebeccablum.alltrailsatlunch.data.LatLng
import com.rebeccablum.alltrailsatlunch.models.Restaurant
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LunchActivity : ComponentActivity() {

    private val viewModel: LunchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val context = LocalContext.current
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) -> {
                    viewModel.showLocationPermissionPrompt.value = false
                    viewModel.updateRestaurantsFromUserLocation()
                }
            }
            MaterialTheme {
                Scaffold(
                    bottomBar = { LunchBottomNav(navController = navController) }
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (viewModel.showLocationPermissionPrompt.value) {
                            // https://stackoverflow.com/questions/60608101/how-request-permissions-with-jetpack-compose
                            val launcher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.RequestPermission()
                            ) { isGranted ->
                                if (isGranted) {
                                    viewModel.showLocationPermissionPrompt.value = false
                                    viewModel.updateRestaurantsFromUserLocation()
                                }
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
                        LunchNavHost(navController = navController, lunchViewModel = viewModel)
                    }
                }
            }
        }
    }
}

// TODO break up viewmodel (?) which would prob mean a location repository
@Composable
fun LunchNavHost(navController: NavHostController, lunchViewModel: LunchViewModel) {
    NavHost(navController = navController, startDestination = "map") {
        composable("map") { Map() }
        composable("restaurantList") { RestaurantList(restaurants = lunchViewModel.nearbyRestaurants.collectAsState().value) }
    }
}

// TODO change title to string resource
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object MapView : Screen("map", "Map", Icons.Filled.Map)
    object RestaurantList : Screen("restaurantList", "Restaurants", Icons.Filled.List)
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

@Composable
fun Map() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Green)
    )
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