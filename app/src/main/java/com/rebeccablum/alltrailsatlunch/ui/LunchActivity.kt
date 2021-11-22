package com.rebeccablum.alltrailsatlunch.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
                    viewModel.updateCurrentLocation()
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
                                    viewModel.updateCurrentLocation()
                                }
                            }
                            // TODO handle rationale / nav to app settings
                            AlertDialog(
                                text = { Text("Location is required to use this app.") },
                                onDismissRequest = { },
                                buttons = {
                                    Button(
                                        onClick = { launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
                                        content = { Text("Set location permissions") })
                                }
                            )
                        }
                        Column {
                            SearchBar(
                                currentSearch = viewModel.searchText.collectAsState().value,
                                onSearchTextChanged = { viewModel.onSearchTextChanged(it) })
                            LunchNavHost(
                                navController = navController,
                                lunchViewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(currentSearch: String, onSearchTextChanged: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        OutlinedTextField(value = currentSearch, onValueChange = { onSearchTextChanged(it) })
    }
}