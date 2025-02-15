package com.anietie.voyatekassessment.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.anietie.voyatekassessment.R
import com.anietie.voyatekassessment.domain.model.FoodItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDetailsScreen(
    foodItem: FoodItem,
    onRemoveClick: (FoodItem) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Large Image
            val painter = rememberAsyncImagePainter(
                model = foodItem.images?.first() ?: ""
            )
            Image(
                painter = painter,
                contentDescription = foodItem.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Name & Remove Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = foodItem.name ?: "",
                    style = MaterialTheme.typography.headlineSmall
                )
                TextButton(onClick = { onRemoveClick(foodItem) }) {
                    Text("Remove")
                }
            }

            // Description
            Text(
                text = foodItem.description ?: "",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Tags
            if (foodItem.tags?.isNotEmpty() == true) {
                Text(
                    text = "Tags: ${foodItem.tags.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            // Ingredients
            if (foodItem.calories?.isNotEmpty() == true) {
                Text(
                    text = "Nutrition",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
                Text(foodItem.calories, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

