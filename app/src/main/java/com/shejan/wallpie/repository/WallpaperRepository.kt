package com.shejan.wallpie.repository

import com.shejan.wallpie.data.FavouriteDao
import com.shejan.wallpie.model.FavouriteWallpaper
import com.shejan.wallpie.model.Wallpaper
import com.shejan.wallpie.network.ApiService
import kotlinx.coroutines.flow.Flow

class WallpaperRepository(
    private val apiService: ApiService,
    private val favouriteDao: FavouriteDao
) {
    suspend fun getWallpapers(): List<Wallpaper> {
        return apiService.getWallpapers()
    }

    fun getAllFavourites(): Flow<List<FavouriteWallpaper>> {
        return favouriteDao.getAllFavourites()
    }

    suspend fun insertFavourite(wallpaper: FavouriteWallpaper) {
        favouriteDao.insertFavourite(wallpaper)
    }

    suspend fun deleteFavourite(wallpaper: FavouriteWallpaper) {
        favouriteDao.deleteFavourite(wallpaper)
    }

    suspend fun isFavourite(url: String): Boolean {
        return favouriteDao.isFavourite(url)
    }
}
