package com.shejan.wallpie.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import java.net.URL
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.webkit.MimeTypeMap
import java.net.HttpURLConnection

object MetadataUtils {

    data class ImageMetadata(
        val resolution: String,
        val size: String,
        val format: String
    )

    suspend fun getImageMetadata(imageUrl: String): ImageMetadata = withContext(Dispatchers.IO) {
        try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "HEAD"
            connection.connect()
            
            val contentLength = connection.contentLengthLong
            val size = formatFileSize(contentLength)
            
            val extension = MimeTypeMap.getFileExtensionFromUrl(imageUrl)
            val format = extension.uppercase(Locale.ROOT).ifEmpty { "JPG" }

            // To get resolution without downloading the whole image
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            val stream = url.openStream()
            BitmapFactory.decodeStream(stream, null, options)
            stream.close()
            
            val resolution = "${options.outWidth} x ${options.outHeight}"
            
            ImageMetadata(resolution, size, format)
        } catch (e: Exception) {
            ImageMetadata("Unknown", "Unknown", "Unknown")
        }
    }

    private fun formatFileSize(size: Long): String {
        if (size <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        return String.format("%.1f %s", size / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
    }
}
