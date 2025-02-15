package com.anietie.voyatekassessment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.anietie.voyatekassessment.data.remote.api.FoodApiService
import com.anietie.voyatekassessment.data.repository.FoodRepositoryImpl
import com.anietie.voyatekassessment.presentation.navigation.AppNavigation
import com.anietie.voyatekassessment.presentation.screens.addfood.AddFoodViewModel
import com.anietie.voyatekassessment.presentation.screens.home.HomeViewModel
import com.anietie.voyatekassessment.presentation.theme.VoyatekAssessmentTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    private val foodApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://assessment.vgtechdemo.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FoodApiService::class.java)
    }

    private val repository by lazy {
        FoodRepositoryImpl(foodApi)
    }

    private val homeViewModel by lazy {
        HomeViewModel(foodRepository = repository)
    }

    private val addFoodViewModel by lazy {
        AddFoodViewModel(repository = repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VoyatekAssessmentTheme {
                val navController = rememberNavController()
                AppNavigation(
                    navController = navController,
                    homeViewModel = homeViewModel,
                    addFoodViewModel = addFoodViewModel
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    VoyatekAssessmentTheme {
        Greeting("Android")
    }
}