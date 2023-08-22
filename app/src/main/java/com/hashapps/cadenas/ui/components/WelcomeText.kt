package com.hashapps.cadenas.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WelcomeText(
    visibleState: MutableTransitionState<Boolean>,
    text: String,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visibleState = visibleState,
        enter = scaleIn(),
    ) {
        ElevatedCard(
            modifier = modifier.fillMaxWidth(),
        ) {
            Text(
                modifier = modifier.padding(8.dp),
                text = text,
            )
        }
    }
}