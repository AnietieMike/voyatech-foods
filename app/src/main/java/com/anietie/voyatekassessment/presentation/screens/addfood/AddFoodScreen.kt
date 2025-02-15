package com.anietie.voyatekassessment.presentation.screens.addfood

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.anietie.voyatekassessment.R
import com.anietie.voyatekassessment.domain.model.Category
import com.anietie.voyatekassessment.domain.model.FoodItem
import com.anietie.voyatekassessment.domain.model.Tag
import com.anietie.voyatekassessment.domain.repository.FoodRepository
import com.anietie.voyatekassessment.presentation.theme.VoyatekAssessmentTheme
import com.anietie.voyatekassessment.utils.AppUtils.createImageUri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodScreen(
    viewModel: AddFoodViewModel,
    onFoodAdded: (FoodItem) -> Unit,
    onBackClick: () -> Unit
) {
    val categoryList by viewModel.categoryList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isSuccess by viewModel.foodAdded.collectAsState()

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var tags by remember { mutableStateOf(listOf<String>()) }
    val imageUris = remember { mutableStateListOf<String>() }

    val isFormComplete = name.isNotBlank() && description.isNotBlank() &&
            calories.isNotBlank() && selectedCategory != null && imageUris.isNotEmpty()

    if (isSuccess) {
        LaunchedEffect(Unit) {
            // Navigate back or show a success message
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add new food") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        // 1) Put the Add button in the bottom bar
        bottomBar = {
            Button(
                onClick = {
                    val food = FoodItem(
                        categoryId = selectedCategory?.id.toString(),
                        name = name,
                        description = description,
                        images = imageUris,
                        tags = tags,
                        calories = calories
                    )
                    viewModel.addFoodItem(food)
                },
                shape = RoundedCornerShape(8.dp),
                enabled = isFormComplete, // Disabled until form is complete
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 22.dp)
            ) {
                Text("Add Food")
            }
        }
    ) { paddingValues ->
        // 2) Make the content scrollable
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Photo Options
            PhotoOptionsRow(
                onTakePhotoClick = { uri ->
                    // Launch camera with URI and store the URI
                    imageUris.add(uri ?: return@PhotoOptionsRow)
                },
                onUploadClick = { uri ->
                    // Launch gallery
                    imageUris.add(uri ?: return@PhotoOptionsRow)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Thumbnails / placeholders
            if (imageUris.isNotEmpty()) {
                ImageThumbnailRow(
                    imageUris = imageUris,
                    onRemoveImage = { uriToRemove ->
                        imageUris.remove(uriToRemove)
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Name
            Text("Name", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = {
                    Text(
                        "Enter food name",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Gray
                    )
                },
                textStyle = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Description
            Text("Description", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Enter food description", style = MaterialTheme.typography.labelLarge, color = Color.Gray) },
                textStyle = MaterialTheme.typography.labelLarge,
                minLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Category Dropdown
            Text("Category", style = MaterialTheme.typography.labelLarge)
            CategoryDropdown(
                categoryList = categoryList.map { it },
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Calories
            Text("Calories", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = calories,
                onValueChange = { calories = it },
                placeholder = {
                    Text(
                        "Enter number of calories",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Gray
                    )
                },
                textStyle = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Tags
            Text("Tags", style = MaterialTheme.typography.labelLarge)
            TagInputField(
                tags = tags,
                onTagsChanged = { updatedTags ->
                    tags = updatedTags
                }
            )
            Text("Press enter once you've typed a tag", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
        }
    }
}


@Composable
fun PhotoOptionsRow(
    onTakePhotoClick: (String?) -> Unit,
    onUploadClick: (String?) -> Unit
) {

    val context = LocalContext.current
    // We store the URI for camera
    val cameraUri = remember { mutableStateOf<Uri?>(null) }

    // TakePicture launcher
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            onTakePhotoClick(cameraUri.value?.toString())
        } else {
            onTakePhotoClick(null)
        }
    }

    // Gallery launcher
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onUploadClick(uri?.toString())
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IconTextCard(
            iconPainter = painterResource(id = R.drawable.ic_camera),
            label = "Take photo",
            onClick = {
                // Create a URI
                val newUri = createImageUri(context)
                cameraUri.value = newUri
                if (newUri != null) {
                    takePictureLauncher.launch(newUri)
                }
            },
            modifier = Modifier.weight(1f)
        )
        IconTextCard(
            iconPainter = painterResource(id = R.drawable.ic_upload),
            label = "Upload",
            onClick = {
                // Launch gallery
                pickImageLauncher.launch("image/*")
            },
            modifier = Modifier.weight(1f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    categoryList: List<Category>,
    selectedCategory: Category?,
    onCategorySelected: (Category) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedCategory?.name ?: "",
            onValueChange = { /* No-op, read-only from dropdown */ },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categoryList.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ImageThumbnailRow(
    imageUris: List<String>,
    onRemoveImage: (String) -> Unit
) {
    // Horizontal row of images with spacing
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        imageUris.forEach { uri ->
            Box(
                modifier = Modifier
                    .size(80.dp) // Increase from 64.dp if you want a bigger thumbnail
            ) {
                // 1) The image itself
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)), // Rounded corners
                    contentScale = ContentScale.Crop
                )

                // 2) The "X" button in the bottom-right corner
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 4.dp, bottom = 4.dp) // small offset from edge
                        .size(24.dp)
                        .background(color = Color.White, shape = RoundedCornerShape(12.dp))
                        .clickable {
                            onRemoveImage(uri)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close), // or Icons.Default.Close
                        contentDescription = "Remove image",
                        tint = Color.Black, // Adjust color if needed
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AddFoodScreenPreview() {
    val fakeRepository = object : FoodRepository {
        override fun getAllFoods(): Flow<List<FoodItem>> = flow {
            emit(
                listOf(
                    FoodItem("1", "Pizza", "Delicious cheese pizza", "300", listOf(), listOf("Dinner")),
                    FoodItem("2", "Burger", "Tasty beef burger", "450", listOf(),  listOf("Lunch"))
                )
            )
        }

        override suspend fun addFood(food: FoodItem) {}
        override suspend fun removeFood(foodId: String) {}
        override suspend fun getFoodById(foodId: String): FoodItem? = null
        override suspend fun updateFood(food: FoodItem) {}
        override suspend fun getCategories(): List<Category> = emptyList()
        override suspend fun getTags(): List<Tag> = emptyList()
    }
    VoyatekAssessmentTheme {
        AddFoodScreen(
            AddFoodViewModel(fakeRepository),
            onFoodAdded = { /* navigate or show details */ },
            onBackClick = { /* navigate back */ }
        )
    }
}


