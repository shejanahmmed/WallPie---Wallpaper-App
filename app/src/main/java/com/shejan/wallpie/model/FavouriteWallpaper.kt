package com.shejan.wallpie.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourites")
data class FavouriteWallpaper(
    @PrimaryKey val url: String,
    val name: String,
    val category: String
)

fun FavouriteWallpaper.toWallpaper() = Wallpaper(name, category, url)
fun Wallpaper.toFavourite() = FavouriteWallpaper(url, name, category)
