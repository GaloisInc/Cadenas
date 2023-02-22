package com.hashapps.butkusapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hashapps.butkusapp.R

/** UI element for a tag that has been added to the encoded message. Displays
 * the tag itself, and a button intended for tag removal. */
@Composable
fun TagEntry(
    uiEnabled: Boolean,
    tag: String,
    onTagRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = tag, style = MaterialTheme.typography.caption)
        Button(enabled = uiEnabled, onClick = onTagRemove) {
            Text(
                text = stringResource(R.string.delete),
            )
        }
    }
}