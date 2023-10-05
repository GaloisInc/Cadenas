package com.hashapps.cadenas.ui.welcome

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
fun FinalScreen(
    completeFirstRun: () -> Unit,
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
        FinalBody(
            completeFirstRun = completeFirstRun,
            modifier = modifier.padding(innerPadding),
        )
    }
}

@Composable
fun FinalBody(
    completeFirstRun: () -> Unit,
    modifier: Modifier,
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        val postProfileState = remember {
            MutableTransitionState(false).apply {
                targetState = true
            }
        }
        WelcomeText(
            visibleState = postProfileState,
            text = stringResource(R.string.after_profile_text)
        )

        val getStartedState = remember {
            MutableTransitionState(false)
        }
        getStartedState.targetState = postProfileState.isIdle && postProfileState.currentState

        WelcomeText(
            visibleState = getStartedState,
            text = stringResource(R.string.get_started_text)
        )

        Spacer(modifier = modifier.weight(1f))

        val buttonState = remember {
            MutableTransitionState(false)
        }
        buttonState.targetState = getStartedState.isIdle && getStartedState.currentState

        WelcomeButton(
            visibleState = buttonState,
            onClick = {
                completeFirstRun()
            },
            text = stringResource(R.string.get_started),
        )
    }
}
