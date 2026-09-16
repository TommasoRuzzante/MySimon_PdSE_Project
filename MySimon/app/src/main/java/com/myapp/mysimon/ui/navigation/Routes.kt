package com.myapp.mysimon.ui.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes for the application.
 * Each route represents a destination in the NavHost.
 */

// Route for the main home screen
@Serializable
object HomeRoute

// Route for the detail screen of a specific game, requiring its database ID
@Serializable
data class DetailRoute(
    val id: Int
)

// Route for the gameplay screen
@Serializable
object GameRoute

// Route for the user account and statistics screen
@Serializable
object AccountRoute
