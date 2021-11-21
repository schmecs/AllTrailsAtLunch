package com.rebeccablum.alltrailsatlunch.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
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
                        Column {
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()) {
                                Text("Search bar here")
                            }
                            LunchNavHost(navController = navController, lunchViewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}