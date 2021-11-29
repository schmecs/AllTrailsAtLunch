package com.rebeccablum.alltrailsatlunch.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rebeccablum.alltrailsatlunch.R
import com.rebeccablum.alltrailsatlunch.ui.SearchBar

@Composable
fun LunchTopAppBar(searchText: String, onSearchTextChanged: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(0.dp),
        elevation = 4.dp
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 0.dp,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize()
                        .padding(top = 8.dp)
                ) {
                    Icon(
                        Icons.Filled.Hiking,
                        null,
                        tint = MaterialTheme.colors.primary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.app_title_1),
                        style = MaterialTheme.typography.h5,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.app_title_2),
                        style = MaterialTheme.typography.h5
                    )
                }
            }
            SearchBar(
                currentSearch = searchText,
                onSearchTextChanged = { onSearchTextChanged(it) }
            )
        }
    }
}