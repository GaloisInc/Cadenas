package com.hashapps.cadenas.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun WelcomeButton(
    visibleState: MutableTransitionState<Boolean>,
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visibleState = visibleState,
        enter = scaleIn(),
    ) {
        Button(
            modifier = modifier.fillMaxWidth(),
            onClick = onClick,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}