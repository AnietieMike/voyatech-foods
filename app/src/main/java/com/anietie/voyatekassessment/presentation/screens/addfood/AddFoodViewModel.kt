package com.anietie.voyatekassessment.presentation.screens.addfood

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anietie.voyatekassessment.domain.model.Category
import com.anietie.voyatekassessment.domain.model.FoodItem
import com.anietie.voyatekassessment.domain.model.Tag
import com.anietie.voyatekassessment.domain.repository.FoodRepository
import com.anietie.voyatekassessment.utils.AppUtils.getFileNameFromUri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class AddFoodViewModel(
    private val repository: FoodRepository,
    private val appContext: Context,
) : ViewModel() {
    private val _categoryList = MutableStateFlow<List<Category>>(emptyList())
    val categoryList: StateFlow<List<Category>> = _categoryList.asStateFlow()

    private val _tagList = MutableStateFlow<List<Tag>>(emptyList())
    val tagList: StateFlow<List<Tag>> = _tagList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _foodAdded = MutableStateFlow<Boolean>(false)
    val foodAdded: StateFlow<Boolean> = _foodAdded.asStateFlow()

    init {
        fetchCategories()
        fetchTags()
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
                val processedImagePath =
                    foodItem.images?.mapNotNull { imageItem ->
                        val uri = Uri.parse(imageItem.imageUrl)
                        val file = uriToTempFile(uri, appContext) ?: return@mapNotNull null
                        imageItem.copy(imageUrl = file.absolutePath) // Update with real file path
                    }
                repository.addFood(
                    name = foodItem.name,
                    description = foodItem.description,
                    categoryId = foodItem.categoryId,
                    calories = foodItem.calories?.toIntOrNull() ?: 0,
                    tags = foodItem.tags?.map { it.toInt() } ?: emptyList(),
                    imagePaths = processedImagePath?.map { it.imageUrl } ?: emptyList(),
                )
                _foodAdded.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add food"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun fetchTags() {
        viewModelScope.launch {
            try {
                _tagList.value = repository.getTags()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to fetch tags"
            }
        }
    }

    private fun uriToTempFile(
        uri: Uri,
        context: Context,
    ): File? {
        val contentResolver = context.contentResolver
        val fileName = getFileNameFromUri(uri, contentResolver) ?: "image.jpg"

        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val tempFile = File(context.cacheDir, fileName)

            inputStream.use { input ->
                tempFile.outputStream().use { output -> input.copyTo(output) }
            }

            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
