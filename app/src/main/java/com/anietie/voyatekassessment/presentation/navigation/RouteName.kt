package com.anietie.voyatekassessment.presentation.navigation

sealed class RouteName(val route: String) {
    object HomeScreen : RouteName("home")
    object AddFoodScreen : RouteName("add")
    object FoodDetailsScreen : RouteName("fooddetails")
}