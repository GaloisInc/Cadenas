package com.hashapps.cadenas.ui.navigation

/**
 * Interface defining the properties of navigation destinations.
 *
 * Cadenas is a multi-screen, single-activity application - the backbone of the
 * application is its navigation graph. Every screen provides a singleton
 * implementing this interface for the purposes of constructing this graph.
 *
 * @property[route] The unique name identifying the destination to the NavHost
 * @property[titleRes] The integer ID of a string resource, used to display a
 * title on this destination's screen
 */
interface NavigationDestination {
    val route: String
    val titleRes: Int
}