package com.anietie.voyatekassessment.presentation.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anietie.voyatekassessment.domain.model.Category
import com.anietie.voyatekassessment.domain.model.FoodItem
import com.anietie.voyatekassessment.domain.repository.FoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

data class HomeUiState(
    val userName: String = "Eunice", // or fetched from user profile
    val foodList: List<FoodItem> = mutableListOf(),
    val allFoods: List<FoodItem> = emptyList(),
    val categories: List<Category> = mutableListOf(Category("All", 0, "")),
    val searchQuery: String = "",
    val selectedCategory: Category = Category("All", 0, "All"),
    val isLoading: Boolean = false,
    val errorMessage: String = "",
)

class HomeViewModel(
    private val foodRepository: FoodRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadFoods()
        fetchCategories()
    }

    private fun loadFoods() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            foodRepository
                .getAllFoods()
                .onStart { _uiState.value = _uiState.value.copy(isLoading = true) }
                .catch { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = error.message ?: "An error occurred")
                }.collect { foods ->
                    _uiState.value =
                        _uiState.value.copy(
                            allFoods = foods,
                            foodList = foods,
                            isLoading = false,
                        )
                }
        }
    }

    fun getFoodItemById(foodId: String): FoodItem? = _uiState.value.allFoods.find { it.id.toString() == foodId }

    private fun fetchCategories() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val fetchedCategories = foodRepository.getCategories()
                val updatedCategories = listOf(Category("All", 0, "All")) + fetchedCategories

                _uiState.value =
                    _uiState.value.copy(
                        categories = updatedCategories,
                        isLoading = false,
                    )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Failed to load categories")
            }
        }
    }

    fun addToFavorites(item: FoodItem) {
        viewModelScope.launch {
            // If you have a repository, call remove method here
            // repository.removeFood(item.id)

            // For now, remove from local list
            val updatedList = _uiState.value.foodList.filterNot { it.name == item.name }
            _uiState.value = _uiState.value.copy(foodList = updatedList)
        }
    }

    fun onSearchQueryChanged(query: String) {
        Log.d("HomeViewModel", "Search query changed: $query")
        _uiState.value = _uiState.value.copy(searchQuery = query)
        filterBySearchQuery()
    }

    fun updateSelectedCategory(category: Category) {
        Log.d("HomeViewModel", "Category selected: ${category.name}")
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        filterByCategory()
    }

    private fun filterByCategory() {
        val selectedCategory = _uiState.value.selectedCategory
        val allFoods = _uiState.value.allFoods

        val filteredFoods =
            if (selectedCategory.name == "All") {
                allFoods
            } else {
                allFoods.filter { food -> food.category.name == selectedCategory.name }
            }

        _uiState.value = _uiState.value.copy(foodList = filteredFoods)
        Log.d("HomeViewModel", "All Foods after filtering by category: ${_uiState.value.foodList}")
    }

    private fun filterBySearchQuery() {
        val query = _uiState.value.searchQuery
        val allFoods = _uiState.value.allFoods // Always filter from the original list

        val filteredFoods =
            if (query.isEmpty()) {
                allFoods
            } else {
                allFoods.filter { food ->
                    food.name.contains(query, ignoreCase = true)
                }
            }
        _uiState.value = _uiState.value.copy(foodList = filteredFoods)
        Log.d("HomeViewModel", "Food list size after filtering by search query: ${_uiState.value.foodList.size}")
        Log.d("HomeViewModel", "All Foods after filtering by search query: ${_uiState.value.foodList}")
    }
}
