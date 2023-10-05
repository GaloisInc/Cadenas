package com.hashapps.cadenas.ui.welcome

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hashapps.cadenas.R
import com.hashapps.cadenas.ui.components.WelcomeButton
import com.hashapps.cadenas.ui.components.WelcomeText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntroScreen(
    onNavigateToAddModel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.intro)) },
                modifier = modifier,
            )
        }
    ) { innerPadding ->
        IntroBody(
            onNavigateToAddModel = onNavigateToAddModel,
            modifier = modifier.padding(innerPadding),
        )
    }
}

@Composable
fun IntroBody(
    onNavigateToAddModel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        val welcomeState = remember {
            MutableTransitionState(false).apply {
                targetState = true
            }
        }
        WelcomeText(visibleState = welcomeState, text = stringResource(R.string.intro_text))

        val addModelState = remember {
            MutableTransitionState(false)
        }
        addModelState.targetState = welcomeState.isIdle && welcomeState.currentState

        WelcomeText(visibleState = addModelState, text = stringResource(R.string.add_model_text))

        Spacer(modifier = modifier.weight(1f))

        val buttonState = remember {
            MutableTransitionState(false)
        }
        buttonState.targetState = addModelState.isIdle && addModelState.currentState

        WelcomeButton(
            visibleState = buttonState, onClick = onNavigateToAddModel, text = stringResource(
                R.string.add_model
            )
        )
    }
}
