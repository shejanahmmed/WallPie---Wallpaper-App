package com.shejan.wallpie.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourites")
data class FavouriteWallpaper(
    @PrimaryKey val url: String,
    val name: String,
    val category: String,
    val downloads: Int = 0
)

fun FavouriteWallpaper.toWallpaper() = Wallpaper(name, category, url, downloads)
fun Wallpaper.toFavourite() = FavouriteWallpaper(url, name, category, downloads)
