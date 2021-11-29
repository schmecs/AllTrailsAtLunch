package com.rebeccablum.alltrailsatlunch.ui.compose.style

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val primary = Color(0xff388e3c)
val primaryLight = Color(0xff6abf69)
val primaryDark = Color(0xff00600f)
val secondary = Color(0xff616161)
val secondaryLight = Color(0xff8e8e8e)
val secondaryDark = Color(0xff373737)
val primaryText = Color(0xff000000)
val secondaryText = Color(0xffffffff)

private val LightColors =
    lightColors(
        primary = primary,
        primaryVariant = primaryLight,
        secondary = secondary,
        onPrimary = secondaryText,
        onSecondary = primaryText,
        onSurface = primaryText
    )

@Composable
fun LunchTheme(content: @Composable () -> Unit) {
    MaterialTheme(colors = LightColors) {
        content()
    }
}