package com.rebeccablum.alltrailsatlunch.ui

import android.Manifest
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState

// https://google.github.io/accompanist/permissions/#rememberpermissionstate-and-remembermultiplepermissionsstate-apis
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionWrapper(
    goToAppSettings: () -> Unit,
    appContent: @Composable () -> Unit
) {
    val locationPermissionState =
        rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)

    PermissionRequired(
        permissionState = locationPermissionState,
        permissionNotGrantedContent = {
            Column(modifier = Modifier.wrapContentSize()) {
                Text("Please allow access to your location so we can find you some food.")
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
                        Text("Let's go")
                    }
                }
            }
        },
        permissionNotAvailableContent = {
            Column(modifier = Modifier.wrapContentSize()) {
                Text("Location access is denied. Please grant location access in settings to use this app.")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = goToAppSettings) {
                    Text("Open settings")
                }
            }
        }) {
        appContent()
    }
}