package com.shejan.wallpie.network

import com.shejan.wallpie.model.Wallpaper
import retrofit2.http.GET

interface ApiService {
    // The path should be relative to the base URL
    @GET("wallpapers.json")
    suspend fun getWallpapers(): List<Wallpaper>
}
