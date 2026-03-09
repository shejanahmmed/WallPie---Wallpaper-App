package com.shejan.wallpie.model

data class Wallpaper(
    val name: String,
    val category: String,
    val url: String,
    val downloads: Int = 0
)
