package com.anietie.voyatekassessment.presentation.screens.addfood

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AddFoodViewModel(
) : ViewModel() {

    // Example categories from an API
    private val _categoryList = MutableStateFlow<List<String>>(emptyList())
    val categoryList: StateFlow<List<String>> = _categoryList.asStateFlow()

    init {
        // Suppose you fetch categories from an API or repository
        // For now, just a placeholder
        _categoryList.value = listOf("Breakfast", "Lunch", "Dinner")
    }
}
