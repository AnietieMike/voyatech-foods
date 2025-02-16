package com.anietie.voyatekassessment.presentation.screens.home

import BottomNavBar
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.anietie.voyatekassessment.R
import com.anietie.voyatekassessment.domain.model.Category
import com.anietie.voyatekassessment.domain.model.FoodImage
import com.anietie.voyatekassessment.domain.model.FoodItem
import com.anietie.voyatekassessment.domain.model.Tag
import com.anietie.voyatekassessment.domain.repository.FoodRepository
import com.anietie.voyatekassessment.presentation.theme.VoyatekAssessmentTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavController,
) {
    // Collect the current UI state from the ViewModel
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavBar(
                navController = navController,
            )
        },
    ) { paddingValues ->
        // Body content
        if (uiState.isLoading) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier =
                    Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
            ) {
                // 1) Custom header
                HeaderSection(userName = uiState.userName)
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = "Search Icon",
                        )
                    },
                    label = {
                        Text(
                            "Search foods...",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        )
                    },
                    maxLines = 1,
                    shape = RoundedCornerShape(8.dp),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    uiState.categories.forEach { category ->
                        TagButton(
                            text = category.name,
                            isSelected = (uiState.selectedCategory == category),
                            onClick = { viewModel.updateSelectedCategory(category) },
                        )
                    }
                }
                Text(
                    text = "All Foods",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(16.dp),
                )
                // Display error message or empty state text
                if (uiState.errorMessage.isNotEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "${uiState.errorMessage}\nPlease try again!",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                } else if (uiState.foodList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "No dishes available",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                } else {
                    LazyColumn {
                        items(uiState.foodList) { food ->
                            FoodItemCard(
                                food = food,
                                onItemClick = { navController.navigate("foodDetails/${food.id}") },
                                onLikeClick = { viewModel.addToFavorites(food) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderSection(userName: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background, // or any other color
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            // adjust spacing to your design
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
        ) {
            // Left side: avatar + greeting
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_avatar_placeholder),
                    contentDescription = "User Avatar",
                )
                IconButton(onClick = { /* handle notifications */ }) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_notification), // your bell icon
                        contentDescription = "Notifications",
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                Text(
                    text = "Hey there, $userName!",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "Are you excited to create a tasty dish today?",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                )
            }
        }
    }
}

@Composable
fun TagButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    // For a simple approach, use a Button that changes style if selected
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(4.dp),
        colors =
            if (isSelected) {
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                )
            },
        content = { Text(text) },
    )
}

@Composable
fun FoodItemCard(
    food: FoodItem,
    onItemClick: () -> Unit,
    onLikeClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        onClick = onItemClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column {
            // Image
            val painter =
                rememberAsyncImagePainter(
                    model = if (food.images.isNullOrEmpty()) "" else food.images.first().imageUrl,
                )
            Image(
                painter = painter,
                contentDescription = food.name,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                contentScale = ContentScale.Crop,
            )

            // Title + Remove button
            Row(
                modifier =
                    Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = food.name ?: "",
                    style = MaterialTheme.typography.titleMedium,
                )
                IconButton(onClick = onLikeClick) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_heart), // your bell icon
                        contentDescription = "Notifications",
                    )
                }
            }

            // Short description
            Text(
                text = food.description ?: "",
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val fakeRepository =
        object : FoodRepository {
            override fun getAllFoods(): Flow<List<FoodItem>> =
                flow {
                    emit(
                        listOf(
                            FoodItem(
                                id = 1,
                                name = "Spicy Beef Suya",
                                description = "A popular West African street food consisting of thinly sliced beef skewers coated in a spicy, nutty suya spice mix.",
                                categoryId = 8,
                                calories = "213",
                                tags = listOf("Spicy", "Grilled", "High-Protein"),
                                images =
                                    listOf(
                                        FoodImage(id = 1, imageUrl = "https://example.com/images/suya1.jpg"),
                                        FoodImage(id = 2, imageUrl = "https://example.com/images/suya2.jpg"),
                                    ),
                                category =
                                    Category(
                                        id = 8,
                                        name = "Meat",
                                        description = "Beef, pork, and other meats",
                                    ),
                            ),
                        ),
                    )
                }

            override suspend fun addFood(
                name: String,
                description: String,
                categoryId: Int,
                calories: Int,
                tags: List<Int>,
                imagePaths: List<String>,
            ): FoodItem =
                FoodItem(
                    1,
                    categoryId,
                    Category("Meat", 1, "Beef, pork, and other meats"),
                    name,
                    description,
                    calories.toString(),
                    imagePaths.map {
                        FoodImage(1, it)
                    },
                    tags.map { it.toString() },
                )

            override suspend fun removeFood(foodId: String) {}

            override suspend fun updateFood(food: FoodItem) {}

            override suspend fun getCategories(): List<Category> = emptyList()

            override suspend fun getTags(): List<Tag> = emptyList()
        }

    VoyatekAssessmentTheme {
        HomeScreen(
            HomeViewModel(foodRepository = fakeRepository),
            navController = NavController(LocalContext.current),
        )
    }
}
