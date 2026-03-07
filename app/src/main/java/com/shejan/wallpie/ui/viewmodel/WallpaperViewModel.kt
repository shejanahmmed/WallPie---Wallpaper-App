package com.shejan.wallpie.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shejan.wallpie.model.Wallpaper
import com.shejan.wallpie.repository.WallpaperRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private var allWallpapers = listOf<Wallpaper>()
    private var currentCategory = "All"

    init {
        fetchWallpapers()
    }

    fun fetchWallpapers() {
        viewModelScope.launch {
            _uiState.value = WallpaperState.Loading
            try {
                allWallpapers = repository.getWallpapers()
                applyFilters()
            } catch (e: Exception) {
                _uiState.value = WallpaperState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
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
