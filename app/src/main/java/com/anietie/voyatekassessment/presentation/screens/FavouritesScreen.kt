package com.anietie.voyatekassessment.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FavouritesScreen() {
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues),
        ) {
            Text(
                text = "Favourites",
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
