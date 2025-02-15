package com.anietie.voyatekassessment.presentation.screens.addfood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anietie.voyatekassessment.domain.model.Category
import com.anietie.voyatekassessment.domain.model.FoodItem
import com.anietie.voyatekassessment.domain.repository.FoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddFoodViewModel(private val repository: FoodRepository) : ViewModel() {

    private val _categoryList = MutableStateFlow<List<Category>>(emptyList())
    val categoryList: StateFlow<List<Category>> = _categoryList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _foodAdded = MutableStateFlow<Boolean>(false)
    val foodAdded: StateFlow<Boolean> = _foodAdded.asStateFlow()

    init {
        fetchCategories()
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            try {
                _categoryList.value = repository.getCategories()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load categories"
            }
        }
    }

    fun addFoodItem(foodItem: FoodItem) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.addFood(foodItem)
                _foodAdded.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add food"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
