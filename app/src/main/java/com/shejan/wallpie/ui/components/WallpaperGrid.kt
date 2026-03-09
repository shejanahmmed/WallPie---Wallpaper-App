package com.shejan.wallpie.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.shejan.wallpie.model.Wallpaper

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun WallpaperGrid(
    wallpapers: List<Wallpaper>,
    onWallpaperClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 100.dp)
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = contentPadding,
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(wallpapers) { index, wallpaper ->
            WallpaperCard(
                wallpaper = wallpaper,
                onClick = { onWallpaperClick(index) }
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun WallpaperCard(
    wallpaper: Wallpaper,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(0.6f)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium
    ) {
        Box {
            GlideImage(
                model = wallpaper.url,
                contentDescription = wallpaper.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Surface(
                color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f),
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Text(
                    text = wallpaper.name,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = androidx.compose.ui.graphics.Color.White
                )
            }
        }
    }
}
