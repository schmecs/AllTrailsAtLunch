package com.rebeccablum.alltrailsatlunch.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.rebeccablum.alltrailsatlunch.ui.compose.LunchBottomNav
import com.rebeccablum.alltrailsatlunch.ui.compose.LunchNavigationContainer
import com.rebeccablum.alltrailsatlunch.ui.compose.LunchTopAppBar
import com.rebeccablum.alltrailsatlunch.ui.compose.PermissionWrapper
import com.rebeccablum.alltrailsatlunch.ui.compose.style.LunchTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LunchActivity : ComponentActivity() {

    private val viewModel: LunchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.subscribeToLocationAndSearchChanges()

        setContent {
            val navController = rememberNavController()
            LunchTheme {
                PermissionWrapper(
                    goToAppSettings = { goToAppSettings() },
                    appContent = {
                        LaunchedEffect(Unit) {
                            viewModel.updateCurrentLocation() // call once after permissions granted
                        }
                        Scaffold(
                            topBar = {
                                LunchTopAppBar(
                                    viewModel.searchText.collectAsState().value
                                ) { viewModel.onSearchTextChanged(it) }
                            },
                            bottomBar = { LunchBottomNav(navController = navController) }
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Box(modifier = Modifier.wrapContentSize()) {
                                    Column {
                                        LunchNavigationContainer(
                                            navController = navController,
                                            lunchViewModel = viewModel
                                        )
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
    }

    private fun goToAppSettings() {
        Intent(ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${packageName}")).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(this)
        }
    }
}