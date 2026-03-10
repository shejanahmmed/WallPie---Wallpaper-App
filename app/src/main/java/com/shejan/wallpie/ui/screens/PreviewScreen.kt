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
import androidx.compose.ui.unit.sp
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

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import com.shejan.wallpie.utils.MetadataUtils
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Image

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
            val isLight = !isDarkTheme
            windowInsetsController.isAppearanceLightStatusBars = isLight
            windowInsetsController.isAppearanceLightNavigationBars = isLight
            onDispose {
                windowInsetsController.isAppearanceLightStatusBars = isLight
                windowInsetsController.isAppearanceLightNavigationBars = isLight
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
    var showMetadata by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var controlsVisible by remember { mutableStateOf(true) }
    
    val currentWallpaper = if (wallpapers.isNotEmpty()) wallpapers[pagerState.currentPage.coerceIn(0, (pageCount - 1).coerceAtLeast(0))] else null
    
    var isFavourite by remember(currentWallpaper?.url) { mutableStateOf(false) }
    var metadata by remember(currentWallpaper?.url) { mutableStateOf<MetadataUtils.ImageMetadata?>(null) }

    LaunchedEffect(currentWallpaper?.url) {
        currentWallpaper?.let { 
            isFavourite = viewModel.isFavourite(it.url)
            metadata = MetadataUtils.getImageMetadata(it.url)
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectVerticalDragGestures { _, dragAmount ->
                if (dragAmount < -20f && !showMetadata) {
                    showMetadata = true
                }
            }
        }
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
                        .padding(top = 16.dp, start = 16.dp)
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

        if (showMetadata) {
            ModalBottomSheet(
                onDismissRequest = { showMetadata = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface,
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 32.dp)
                ) {
                    Text(
                        text = "Wallpaper Details",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 20.dp),
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )

                    currentWallpaper?.let { wall ->
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            // Row 1
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                MetadataCard(Modifier.weight(1f), Icons.Default.Image, "Name", wall.name)
                                MetadataCard(Modifier.weight(1f), Icons.Default.Category, "Category", wall.category)
                            }
                            
                            // Row 2
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                MetadataCard(Modifier.weight(1f), Icons.Default.FileDownload, "Downloads", "${wall.downloads}")
                                
                                metadata?.let { meta ->
                                    MetadataCard(Modifier.weight(1f), Icons.Default.Height, "Resolution", meta.resolution)
                                } ?: run {
                                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                    }
                                }
                            }
                            
                            // Row 3
                            metadata?.let { meta ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    MetadataCard(Modifier.weight(1f), Icons.Default.Info, "Size", meta.size)
                                    MetadataCard(Modifier.weight(1f), Icons.Default.Info, "Format", meta.format)
                                }
                            }
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

@Composable
fun MetadataCard(modifier: Modifier = Modifier, icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Surface(
        modifier = modifier.height(80.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = label, 
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), 
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                )
                Text(
                    text = value, 
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
        }
    }
}
