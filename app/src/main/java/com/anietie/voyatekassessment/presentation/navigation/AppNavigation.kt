package com.anietie.voyatekassessment.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.anietie.voyatekassessment.presentation.screens.FavouritesScreen
import com.anietie.voyatekassessment.presentation.screens.FoodDetailsScreen
import com.anietie.voyatekassessment.presentation.screens.addfood.AddFoodScreen
import com.anietie.voyatekassessment.presentation.screens.addfood.AddFoodViewModel
import com.anietie.voyatekassessment.presentation.screens.home.HomeScreen
import com.anietie.voyatekassessment.presentation.screens.home.HomeViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    addFoodViewModel: AddFoodViewModel,
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route,
    ) {
        composable(BottomNavItem.Home.route) {
            HomeScreen(
                viewModel = homeViewModel,
                navController = navController,
            )
        }
        composable(BottomNavItem.Add.route) {
            AddFoodScreen(
                viewModel = addFoodViewModel,
                onBackClick = { navController.popBackStack() },
            )
        }
        composable(BottomNavItem.Favourite.route) {
            // Show your FavouritesScreen
            FavouritesScreen()
        }
        composable(
            "foodDetails/{foodId}",
            arguments = listOf(navArgument("foodId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val foodId = backStackEntry.arguments?.getString("foodId")
            val foodItem = homeViewModel.getFoodItemById(foodId!!) // Fetch food item based on ID
            FoodDetailsScreen(
                foodItem = foodItem!!,
                onBackClick = { navController.popBackStack() },
                onRemoveClick = { /* Handle remove logic */ },
                onFavoriteClick = { /* Handle favorite */ },
                onEditClick = { /* Handle edit */ },
            )
        }
    }
}
