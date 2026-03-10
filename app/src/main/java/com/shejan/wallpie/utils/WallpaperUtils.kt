package com.shejan.wallpie.utils

import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import android.util.DisplayMetrics
import android.view.WindowManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object WallpaperUtils {

    fun setWallpaper(context: Context, imageUrl: String, type: WallpaperType) {
        Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val wallpaperManager = WallpaperManager.getInstance(context)
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                            val metrics = DisplayMetrics()
                            @Suppress("DEPRECATION")
                            windowManager.defaultDisplay.getRealMetrics(metrics)
                            
                            val screenWidth = metrics.widthPixels
                            val screenHeight = metrics.heightPixels

                            val bitmapRatio = resource.width.toFloat() / resource.height.toFloat()
                            val screenRatio = screenWidth.toFloat() / screenHeight.toFloat()

                            val newWidth: Int
                            val newHeight: Int
                            if (bitmapRatio > screenRatio) {
                                newHeight = screenHeight
                                newWidth = (newHeight * bitmapRatio).toInt()
                            } else {
                                newWidth = screenWidth
                                newHeight = (newWidth / bitmapRatio).toInt()
                            }

                            val scaledBitmap = Bitmap.createScaledBitmap(resource, newWidth, newHeight, true)
                            val xOffset = (newWidth - screenWidth) / 2
                            val yOffset = (newHeight - screenHeight) / 2

                            val croppedBitmap = Bitmap.createBitmap(scaledBitmap, xOffset, yOffset, screenWidth, screenHeight)

                            when (type) {
                                WallpaperType.HOME -> wallpaperManager.setBitmap(croppedBitmap, null, true, WallpaperManager.FLAG_SYSTEM)
                                WallpaperType.LOCK -> wallpaperManager.setBitmap(croppedBitmap, null, true, WallpaperManager.FLAG_LOCK)
                                WallpaperType.BOTH -> {
                                    wallpaperManager.setBitmap(croppedBitmap, null, true, WallpaperManager.FLAG_SYSTEM)
                                    wallpaperManager.setBitmap(croppedBitmap, null, true, WallpaperManager.FLAG_LOCK)
                                }
                            }
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Wallpaper set successfully!", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Error setting wallpaper: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    fun downloadWallpaper(context: Context, imageUrl: String, fileName: String) {
        try {
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val request = DownloadManager.Request(Uri.parse(imageUrl))
                .setTitle(fileName)
                .setDescription("Downloading wallpaper...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "WallPie/$fileName.jpg")
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

            downloadManager.enqueue(request)
            Toast.makeText(context, "Download started...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error downloading: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareWallpaper(context: Context, imageUrl: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Check out this amazing wallpaper from WallPie! $imageUrl")
        }
        context.startActivity(Intent.createChooser(intent, "Share wallpaper via"))
    }
}

enum class WallpaperType {
    HOME, LOCK, BOTH
}
