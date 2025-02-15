package com.anietie.voyatekassessment.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anietie.voyatekassessment.domain.model.FoodItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val userName: String = "Eunice",      // or fetched from user profile
    val foodList: List<FoodItem> = emptyList(),
    val allFoods: List<FoodItem> = emptyList(),
    val searchQuery: String = "",
    val selectedTag: String = "All",
    val isLoading: Boolean = false
)

class HomeViewModel(
    // If you have a repository or use case, inject it here, e.g.:
    // private val repository: FoodRepository
) : ViewModel() {

    // If you have a sealed class for events, you can use it here, e.g.:
    // private val _stateEvent = MutableLiveData<HomeStateEvent>()
    // val stateEvent: LiveData<HomeStateEvent>
    //     get() = _stateEvent

    // fun setStateEvent(event: HomeStateEvent){
    //     _stateEvent.value = event
    // }

    // fun getFoodItems(){
    //     viewModelScope.launch {
    //         repository.getFoodItems().collect { dataState ->
    //             when(dataState){
    //                 is DataState.Success -> {
    //                     // Handle success
    //                 }
    //                 is DataState.Error -> {
    //                     // Handle error
    //                 }
    //                 is DataState.Loading -> {
    //                     // Handle loading
    //                 }
    //             }
    //         }
    //     }
    // }

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadFoods()
    }

    private fun loadFoods() {
        // Example logic: show loading, then fetch from a repository
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Fake data fetch or call repository:
            val sampleFoods = listOf(
                FoodItem(
                    "1",
                    "Garlic Butter Shrimp Pasta",
                    "A delicious pasta dish",
                    images = listOf("https://www.jessicagavin.com/wp-content/uploads/2015/04/lemon-pepper-shrimp-pasta-in-pan.jpg"),
                    calories = "400", tags = listOf("Lunch", "Dinner")
                ),
                FoodItem(
                    "2",
                    "Lemon Herb Chicken Frittucine",
                    "Creamy and tasty frittucine",
                    images = listOf("https://www.foodandwine.com/thmb/dRikkr-K7Qf2I5vorfPlQ_l6tcM=/750x0/filters:no_upscale():max_bytes(150000):strip_icc():format(webp)/FAW-recipes-herb-and-lemon-roasted-chicken-hero-c4ba0aec56884683be482c47b1e1df11.jpg"),
                    tags = listOf("Breakfast", "Dessert"),
                    calories = "340"
                ),
                FoodItem(
                    "3",
                    "Egg Fried Rice",
                    "All loving dish - asian and african",
                    images = listOf("https://www.vincenzosplate.com/wp-content/uploads/2021/07/610x350-Photo-6_829-How-to-Make-EGG-FRIED-RICE-Approved-by-Uncle-Roger-V1.jpg"),
                    tags = listOf("Breakfast", "Lunch", "Dinner"),
                    calories = "540"
                ),
            )

            _uiState.value = _uiState.value.copy(
                foodList = sampleFoods,
                isLoading = false
            )
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
            (selectedTag == "All" || food.tags.contains(selectedTag)) &&
                    food.name.contains(query, ignoreCase = true)
        }

        _uiState.value = _uiState.value.copy(foodList = filtered)
    }
}