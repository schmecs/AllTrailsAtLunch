package com.rebeccablum.alltrailsatlunch.ui

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

// TODO handle rationale / nav to app settings
@Composable
fun PermissionAlert(
    launchPermissionRequest: () -> Unit,
    goToAppSettings: () -> Unit,
    showRationale: Boolean
) {
    AlertDialog(
        text = { Text("Location is required to use this app.") },
        onDismissRequest = { },
        buttons = {
            Button(
                onClick = { if (showRationale) goToAppSettings() else launchPermissionRequest() },
                content = { Text("Set location permissions") })
        }
    )
}