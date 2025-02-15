package com.anietie.voyatekassessment.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anietie.voyatekassessment.domain.model.FoodItem
import com.anietie.voyatekassessment.domain.repository.FoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

data class HomeUiState(
    val userName: String = "Eunice",      // or fetched from user profile
    val foodList: List<FoodItem> = emptyList(),
    val allFoods: List<FoodItem> = emptyList(),
    val searchQuery: String = "",
    val selectedTag: String = "All",
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)

class HomeViewModel(
     private val foodRepository: FoodRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadFoods()
    }


    private fun loadFoods() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            foodRepository.getAllFoods()
                .onStart { _uiState.value = _uiState.value.copy(isLoading = true) }
                .catch { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = error.message ?: "An error occurred")
                }
                .collect { foods ->
                    _uiState.value = _uiState.value.copy(
                        foodList = foods,
                        isLoading = false
                    )
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
        _uiState.value = _uiState.value.copy(searchQuery = query)
        filterFoods()
    }

    fun updateSelectedTag(tag: String) {
        _uiState.value = _uiState.value.copy(selectedTag = tag)
        filterFoods()
    }

    private fun filterFoods() {
        val allFoods = _uiState.value.allFoods  // original list
        val query = _uiState.value.searchQuery
        val selectedTag = _uiState.value.selectedTag

        val filtered = allFoods.filter { food ->
            (selectedTag == "All" || (!food.tags.isNullOrEmpty() && food.tags.contains(selectedTag)) && food.name!!.contains(query, ignoreCase = true))
        }

        _uiState.value = _uiState.value.copy(foodList = filtered)
    }
}