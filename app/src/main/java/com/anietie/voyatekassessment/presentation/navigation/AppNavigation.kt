package com.anietie.voyatekassessment.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.anietie.voyatekassessment.domain.model.FoodItem
import com.anietie.voyatekassessment.presentation.screens.addfood.AddFoodScreen
import com.anietie.voyatekassessment.presentation.screens.FavouritesScreen
import com.anietie.voyatekassessment.presentation.screens.FoodDetailsScreen
import com.anietie.voyatekassessment.presentation.screens.addfood.AddFoodViewModel
import com.anietie.voyatekassessment.presentation.screens.home.HomeScreen
import com.anietie.voyatekassessment.presentation.screens.home.HomeViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    addFoodViewModel: AddFoodViewModel
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route,
    ) {
        composable(BottomNavItem.Home.route) {
            HomeScreen(
                viewModel = homeViewModel,
                /* foodId -> navController.navigate("details/$foodId") */
                onFoodItemClick = { navController.navigate(RouteName.FoodDetailsScreen.route) },
                navController = navController
            )

        }
        composable(BottomNavItem.Add.route) {
            AddFoodScreen(
                viewModel = addFoodViewModel,
                onFoodAdded = { /* ... */ },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(BottomNavItem.Favourite.route) {
            // Show your FavouritesScreen
            FavouritesScreen()
        }
        composable(RouteName.FoodDetailsScreen.route) {
            // Show your FoodDetailsScreen
            // You can get the foodId from the arguments
            //            val foodId = it.arguments?.getString("foodId") ?: ""
            //            val name = it.arguments?.getString("name") ?: ""
            FoodDetailsScreen(
                foodItem = FoodItem(
                    name = "Rice",
                    description = "Rice is a staple food in Nigeria",
                    images = listOf("https://www.maggi.ng/sites/default/files/styles/home_stage_1500_700/public/srh_recipes/d5c7e1466958e40d34f7522dcc2d92f1.jpg?h=10d202d3&itok=s_R4KWvy", "https://www.google.com"),
                    calories = "100",
                    tags = listOf("All", "Dessert", "Cakes"),
                    categoryId = "1"
                ),
                onBackClick = { navController.popBackStack() },
                onRemoveClick = { navController.popBackStack() }
            )
        }
    }
}
