package com.rebeccablum.alltrailsatlunch.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SearchBar(currentSearch: String, onSearchTextChanged: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = currentSearch,
            onValueChange = { onSearchTextChanged(it) },
            trailingIcon = { Icon(Icons.Filled.Search, null) }
        )
    }
}