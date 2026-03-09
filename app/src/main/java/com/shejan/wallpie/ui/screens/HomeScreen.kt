package com.shejan.wallpie.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.shejan.wallpie.model.Wallpaper
import com.shejan.wallpie.ui.components.AdmobBanner
import com.shejan.wallpie.ui.viewmodel.WallpaperState
import com.shejan.wallpie.ui.viewmodel.WallpaperViewModel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close

@Composable
fun HomeScreen(
    viewModel: WallpaperViewModel,
    onWallpaperClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val categories = listOf("All", "Nature", "AMOLED", "Minimal", "Abstract", "City")
    var selectedCategory by remember { mutableStateOf("All") }

    Column(modifier = Modifier.fillMaxSize()) {
        // ... (Search Bar and Category Selection code) ...
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search wallpapers...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = MaterialTheme.shapes.large
        )

        // Category Selection
        LazyRow(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = {
                        selectedCategory = category
                        viewModel.filterByCategory(category)
                    },
                    label = { Text(category) }
                )
            }
        }


        // Wallpaper Grid
        Box(modifier = Modifier.weight(1f)) {
            when (val state = uiState) {
                is WallpaperState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is WallpaperState.Success -> {
                    WallpaperGrid(state.wallpapers, onWallpaperClick)
                }
                is WallpaperState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.fetchWallpapers() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun WallpaperGrid(
    wallpapers: List<Wallpaper>,
    onWallpaperClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 100.dp),
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(wallpapers) { index, wallpaper ->
            Card(
                modifier = Modifier
                    .aspectRatio(0.6f)
                    .clickable { onWallpaperClick(index) },
                shape = MaterialTheme.shapes.medium
            ) {
                Box {
                    GlideImage(
                        model = wallpaper.url,
                        contentDescription = wallpaper.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Surface(
                        color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f),
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = wallpaper.name,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = androidx.compose.ui.graphics.Color.White
                        )
                    }
                }
            }
        }
    }
}
