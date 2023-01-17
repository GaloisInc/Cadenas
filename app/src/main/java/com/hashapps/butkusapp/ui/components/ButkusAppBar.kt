package com.hashapps.butkusapp.ui.components

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hashapps.butkusapp.R
import com.hashapps.butkusapp.data.ButkusScreen
import com.hashapps.butkusapp.ui.models.ButkusViewModel

/** A top bar defined by a navigation action, a reset button, and a share
 * button that is only displayed if the current visible screen is Encode. */
@Composable
fun ButkusAppBar(
    onOpenDrawer: () -> Unit,
    onReset: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = { Text(stringResource(ButkusViewModel.SharedViewState.currentScreen.title)) },
        backgroundColor = MaterialTheme.colors.primary,
        modifier = modifier,
        navigationIcon = {
            IconButton(
                onClick = onOpenDrawer,
                enabled = ButkusViewModel.SharedViewState.uiEnabled,
            ) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = stringResource(R.string.switch_button),
                )
            }
        },
        actions = {
            IconButton(
                onClick = onReset,
                enabled = ButkusViewModel.SharedViewState.uiEnabled,
            ) {
                Icon(
                    imageVector = Icons.Filled.RestartAlt,
                    contentDescription = stringResource(R.string.reset)
                )
            }

            if (ButkusViewModel.SharedViewState.currentScreen == ButkusScreen.Encode) {
                IconButton(
                    onClick = onShare,
                    enabled = ButkusViewModel.SharedViewState.uiEnabled && ButkusViewModel.SharedViewState.hasShareable,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = stringResource(R.string.share_button),
                    )
                }
            }
        }
    )
}