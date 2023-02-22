package com.hashapps.butkusapp.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.hashapps.butkusapp.R

/** A Butkus screen, defined by a title to display and an informative icon for
 * navigation. */
enum class ButkusScreen(@StringRes val title: Int, val icon: ImageVector) {
    Encode(title = R.string.encode, icon = Icons.Filled.Lock),
    Decode(title = R.string.decode, icon = Icons.Filled.LockOpen),
    Settings(title = R.string.settings, icon = Icons.Filled.Settings),
}