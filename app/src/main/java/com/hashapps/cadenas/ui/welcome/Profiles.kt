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
import com.hashapps.cadenas.ui.navigation.NavigationDestination

object ProfilesDestination : NavigationDestination {
    override val route = "profiles"
    override val titleRes = R.string.intro
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilesScreen(
    onNavigateToAddProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(IntroDestination.titleRes)) },
                modifier = modifier,
            )
        }
    ) { innerPadding ->
        ProfilesBody(
            onNavigateToAddProfile = onNavigateToAddProfile,
            modifier = modifier.padding(innerPadding),
        )
    }
}

@Composable
fun ProfilesBody(
    onNavigateToAddProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        val postModelState = remember {
            MutableTransitionState(false).apply {
                targetState = true
            }
        }
        WelcomeText(visibleState = postModelState, text = stringResource(R.string.after_model_text))

        val addProfileState = remember {
            MutableTransitionState(false)
        }
        addProfileState.targetState = postModelState.isIdle && postModelState.currentState

        WelcomeText(visibleState = addProfileState, text = stringResource(R.string.add_profile_text))

        Spacer(modifier = modifier.weight(1f))

        val buttonState = remember {
            MutableTransitionState(false)
        }
        buttonState.targetState = addProfileState.isIdle && addProfileState.currentState

        WelcomeButton(
            visibleState = buttonState, onClick = onNavigateToAddProfile, text = stringResource(
                R.string.profile_entry
            )
        )
    }
}