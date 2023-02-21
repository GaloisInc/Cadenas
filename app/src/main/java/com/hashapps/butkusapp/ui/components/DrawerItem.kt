package com.hashapps.butkusapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hashapps.butkusapp.data.ButkusScreen
import com.hashapps.butkusapp.ui.models.ButkusViewModel

/** An individual drawer item, whose composition is determined by the current
 * screen and an action to be taken when the item is clicked.
 *
 * A drawer item consists of an icon and text describing the destination, determined by the
 * ButkusScreen. */
@Composable
fun DrawerItem(
    modifier: Modifier = Modifier,
    screen: ButkusScreen,
    onDestinationClicked: (String) -> Unit,
) {
    val screenName = stringResource(screen.title)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onDestinationClicked(screenName) }
            .background(
                color = if (ButkusViewModel.currentScreen == screen) {
                    MaterialTheme.colors.primary
                } else {
                    Color.Transparent
                }
            )
            .padding(8.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = screen.icon,
            contentDescription = screenName,
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = screenName,
            color = if (ButkusViewModel.currentScreen == screen) {
                MaterialTheme.colors.onPrimary
            } else {
                LocalContentColor.current
            },
            style = MaterialTheme.typography.h6,
        )
    }
}