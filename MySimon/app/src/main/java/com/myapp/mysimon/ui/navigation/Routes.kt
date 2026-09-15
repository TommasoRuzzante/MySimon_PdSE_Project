package com.myapp.mysimon.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Serializable
data class DetailRoute(
    val id: Int
)

@Serializable
object GameRoute

@Serializable
object AccountRoute
