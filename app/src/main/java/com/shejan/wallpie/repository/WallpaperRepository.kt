package com.shejan.wallpie.repository

import com.shejan.wallpie.model.Wallpaper
import com.shejan.wallpie.network.ApiService

class WallpaperRepository(private val apiService: ApiService) {
    suspend fun getWallpapers(): List<Wallpaper> {
        return apiService.getWallpapers()
    }
}
