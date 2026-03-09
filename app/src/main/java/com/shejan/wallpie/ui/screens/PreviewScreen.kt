package com.shejan.wallpie.ui.screens

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.shejan.wallpie.model.Wallpaper
import com.shejan.wallpie.ui.viewmodel.WallpaperState
import com.shejan.wallpie.ui.viewmodel.WallpaperViewModel
import com.shejan.wallpie.utils.WallpaperType
import com.shejan.wallpie.utils.WallpaperUtils
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(
    viewModel: WallpaperViewModel,
    initialIndex: Int,
    source: String = "all",
    isDarkTheme: Boolean = false,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val exploreState by viewModel.exploreState.collectAsState()
    val favourites: List<Wallpaper> by viewModel.favouriteWallpapers.collectAsState(initial = emptyList())
    
    val wallpapers = when (source) {
        "fav" -> favourites
        "explore" -> (exploreState as? WallpaperState.Success)?.wallpapers ?: emptyList()
        else -> (uiState as? WallpaperState.Success)?.wallpapers ?: emptyList()
    }
    
    val context = LocalContext.current
    val window = (context as? Activity)?.window
    
    // Edge swipe protection logic
    val configuration = LocalConfiguration.current
    val density = configuration.densityDpi / 160f
    val edgeThresholdPx = 40 * density
    var userScrollEnabled by remember { mutableStateOf(true) }

    // Transparent system bars for edge-to-edge preview
    if (window != null) {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        DisposableEffect(Unit) {
            windowInsetsController.isAppearanceLightStatusBars = !isDarkTheme
            windowInsetsController.isAppearanceLightNavigationBars = !isDarkTheme
            onDispose {
                windowInsetsController.isAppearanceLightStatusBars = !isDarkTheme
                windowInsetsController.isAppearanceLightNavigationBars = !isDarkTheme
            }
        }
    }

    if (wallpapers.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val pageCount = wallpapers.size
    val safeIndex = initialIndex.coerceIn(0, (pageCount - 1).coerceAtLeast(0))
    val pagerState = rememberPagerState(initialPage = safeIndex, pageCount = { pageCount })
    var showDialog by remember { mutableStateOf(false) }
    var controlsVisible by remember { mutableStateOf(true) }
    
    val currentWallpaper = if (wallpapers.isNotEmpty()) wallpapers[pagerState.currentPage.coerceIn(0, pageCount - 1)] else null
    
    var isFavourite by remember(currentWallpaper?.url) { mutableStateOf(false) }
    LaunchedEffect(currentWallpaper?.url) {
        currentWallpaper?.let { 
            isFavourite = viewModel.isFavourite(it.url)
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val isNearEdge = down.position.x < edgeThresholdPx || down.position.x > (size.width - edgeThresholdPx)
                    userScrollEnabled = !isNearEdge
                    waitForUpOrCancellation()
                    userScrollEnabled = true
                }
            }
        }
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) { 
            controlsVisible = !controlsVisible 
        }
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = userScrollEnabled,
            key = { index -> wallpapers[index].url }
        ) { page ->
            GlideImage(
                model = wallpapers[page].url,
                contentDescription = wallpapers[page].name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        AnimatedVisibility(
            visible = controlsVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(top = 48.dp, start = 16.dp)
                        .align(Alignment.TopStart)
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                Surface(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    color = Color.Black.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        IconButton(onClick = { 
                            currentWallpaper?.let {
                                viewModel.toggleFavourite(it)
                                isFavourite = !isFavourite
                            }
                        }) {
                            Icon(
                                imageVector = if (isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Toggle Favourite",
                                tint = if (isFavourite) Color.Red else Color.White
                            )
                        }
                        IconButton(onClick = { if (currentWallpaper != null) showDialog = true }) {
                            Icon(Icons.Default.Wallpaper, contentDescription = "Set Wallpaper", tint = Color.White)
                        }
                        IconButton(onClick = { 
                            currentWallpaper?.let { 
                                viewModel.incrementDownloadCount(it)
                                WallpaperUtils.downloadWallpaper(context, it.url, it.name) 
                            }
                        }) {
                            Icon(Icons.Default.Download, contentDescription = "Download", tint = Color.White)
                        }
                        IconButton(onClick = { 
                            currentWallpaper?.let { 
                                WallpaperUtils.shareWallpaper(context, it.url) 
                            }
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Set Wallpaper") },
                text = { Text("Where would you like to set this wallpaper?") },
                confirmButton = {},
                dismissButton = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        TextButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                currentWallpaper?.let {
                                    WallpaperUtils.setWallpaper(context, it.url, WallpaperType.HOME)
                                }
                                showDialog = false
                            }
                        ) { Text("Home Screen") }
                        TextButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                currentWallpaper?.let {
                                    WallpaperUtils.setWallpaper(context, it.url, WallpaperType.LOCK)
                                }
                                showDialog = false
                            }
                        ) { Text("Lock Screen") }
                        TextButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                currentWallpaper?.let {
                                    WallpaperUtils.setWallpaper(context, it.url, WallpaperType.BOTH)
                                }
                                showDialog = false
                            }
                        ) { Text("Both Screens") }
                    }
                }
            )
        }
    }
}
