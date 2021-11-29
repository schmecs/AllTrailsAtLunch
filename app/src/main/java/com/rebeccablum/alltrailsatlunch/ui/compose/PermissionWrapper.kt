package com.rebeccablum.alltrailsatlunch.ui.compose

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import com.rebeccablum.alltrailsatlunch.R

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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.surface)
                    .padding(48.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.permission_prompt))
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
                        Text(stringResource(R.string.permission_prompt_button))
                    }
                }
            }
        },
        permissionNotAvailableContent = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.surface)
                    .padding(48.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.permissions_rationale))
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = goToAppSettings) {
                    Text("Open settings")
                }
            }
        }) {
        appContent()
    }
}