package com.shejan.wallpie.data

import androidx.room.*
import com.shejan.wallpie.model.FavouriteWallpaper
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteDao {
    @Query("SELECT * FROM favourites")
    fun getAllFavourites(): Flow<List<FavouriteWallpaper>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavourite(wallpaper: FavouriteWallpaper): Long

    @Delete
    suspend fun deleteFavourite(wallpaper: FavouriteWallpaper): Int

    @Query("SELECT EXISTS(SELECT * FROM favourites WHERE url = :url)")
    suspend fun isFavourite(url: String): Boolean
}
