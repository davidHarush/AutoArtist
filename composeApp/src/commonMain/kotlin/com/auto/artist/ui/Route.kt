package com.auto.artist.ui

import androidx.navigation.NavController


/**
 * Represents the routes in the app
 */
sealed class Route(val route: String) {
    data object Start : Route("start")
    data object Color : Route("color")
    data object Mode : Route("Mode")
    data object Style : Route("style")
    data object Concepts : Route("Concepts")
    data object Final : Route("final")  //LoadingImageScreen
    data object Image : Route("image")
}

private val ImageQuestionnaireRoutes = listOf(
    Route.Start,
    Route.Color,
    Route.Mode,
    Route.Style,
    Route.Concepts,
    Route.Final
)

/**
 * Navigate to the next route in the list
 */
fun NavController.imageRouteNext() {
    val currentRoute = this.currentBackStackEntry?.destination?.route
    val currentIndex = ImageQuestionnaireRoutes.indexOfFirst { it.route == currentRoute }
    if (currentIndex != -1 && currentIndex < ImageQuestionnaireRoutes.size - 1) {
        val nextRoute = ImageQuestionnaireRoutes[currentIndex + 1]

        if (nextRoute.route == Route.Concepts.route) {
            // navigate to nextRoute and remove the previous route from the backstack
            navigate(nextRoute.route) {
                popUpTo(Route.Start.route) {
                    inclusive = true
                }
            }
        } else {
            navigate(nextRoute.route)
        }
    }
}

fun NavController.back() {
    this.popBackStack()
}

