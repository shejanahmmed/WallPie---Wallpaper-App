package com.shejan.wallpie.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shejan.wallpie.model.Wallpaper
import com.shejan.wallpie.model.toFavourite
import com.shejan.wallpie.model.toWallpaper
import com.shejan.wallpie.repository.WallpaperRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class WallpaperState {
    object Loading : WallpaperState()
    data class Success(val wallpapers: List<Wallpaper>) : WallpaperState()
    data class Error(val message: String) : WallpaperState()
}

class WallpaperViewModel(private val repository: WallpaperRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<WallpaperState>(WallpaperState.Loading)
    val uiState: StateFlow<WallpaperState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val favouriteWallpapers: StateFlow<List<Wallpaper>> = repository.getAllFavourites()
        .map { list -> 
            list.map { it.toWallpaper() } 
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _exploreState = MutableStateFlow<WallpaperState>(WallpaperState.Loading)
    val exploreState: StateFlow<WallpaperState> = _exploreState.asStateFlow()

    private var allWallpapers = listOf<Wallpaper>()
    private var currentCategory = "All"

    init {
        fetchWallpapers()
    }

    fun toggleFavourite(wallpaper: Wallpaper) {
        viewModelScope.launch {
            if (repository.isFavourite(wallpaper.url)) {
                repository.deleteFavourite(wallpaper.toFavourite())
            } else {
                repository.insertFavourite(wallpaper.toFavourite())
            }
        }
    }

    suspend fun isFavourite(url: String): Boolean {
        return repository.isFavourite(url)
    }

    fun fetchWallpapers() {
        viewModelScope.launch {
            _uiState.value = WallpaperState.Loading
            _exploreState.value = WallpaperState.Loading
            try {
                // For demo, if downloads are 0, randomize them
                val fetched = repository.getWallpapers()
                allWallpapers = fetched.map { 
                    if (it.downloads == 0) it.copy(downloads = (100..5000).random()) else it 
                }
                applyFilters()
                updateExploreState()
            } catch (e: Exception) {
                _uiState.value = WallpaperState.Error(e.localizedMessage ?: "Unknown error")
                _exploreState.value = WallpaperState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    private fun updateExploreState() {
        if (allWallpapers.isEmpty()) return
        // Sorted by downloads for the Explore screen
        val sorted = allWallpapers.sortedByDescending { it.downloads }
        _exploreState.value = WallpaperState.Success(sorted)
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    fun filterByCategory(category: String) {
        currentCategory = category
        applyFilters()
    }

    private fun applyFilters() {
        if (allWallpapers.isEmpty()) return

        var filtered = if (currentCategory == "All") {
            allWallpapers
        } else {
            allWallpapers.filter { it.category.equals(currentCategory, ignoreCase = true) }
        }

        if (_searchQuery.value.isNotEmpty()) {
            filtered = filtered.filter { 
                it.name.contains(_searchQuery.value, ignoreCase = true) 
            }
        }

        _uiState.value = WallpaperState.Success(filtered)
    }
}
