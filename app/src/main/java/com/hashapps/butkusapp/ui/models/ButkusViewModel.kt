package com.hashapps.butkusapp.ui.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.hashapps.butkusapp.data.ButkusScreen

/** Abstract ViewModel class for Butkus screens. Contains a singleton for state
 * shared by all screens (e.g. what screen is being displayed, whether a main
 * action is running, whether the UI should be enabled.) */
abstract class ButkusViewModel : ViewModel() {
    /** Singleton for state shared by all ButkusViewModel instances */
    object SharedViewState {
        /** State of whether ButkusCore has been initialized */
        var butkusInitialized by mutableStateOf(false)

        /** The currently-displayed screen */
        var currentScreen by mutableStateOf(ButkusScreen.Encode)

        /** State of whether the action button has been pressed and a
         * background process has been started (but not completed) */
        var isRunning by mutableStateOf(false)

        /** State of whether there is shareable text in the UI */
        var hasShareable by mutableStateOf(false)

        /** Computed property used to disable all UI when an action starts */
        val uiEnabled
            get() = !isRunning
    }

    /** True iff the input needed to perform the main action is non-empty */
    abstract val runInputNonempty: Boolean

    /** True iff the main action button associated with run() should be enabled */
    val canRun
        get() = !SharedViewState.isRunning && runInputNonempty

    /** Background process to run when main action button is pressed */
    abstract suspend fun run()

    /** Reset the UI */
    abstract fun reset()
}