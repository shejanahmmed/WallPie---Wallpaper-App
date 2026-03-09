package com.shejan.wallpie.ui.screens

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shejan.wallpie.model.Wallpaper
import com.shejan.wallpie.ui.viewmodel.WallpaperViewModel
import com.shejan.wallpie.utils.AppTheme
import com.shejan.wallpie.utils.PreferenceManager
import kotlinx.coroutines.launch
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.shejan.wallpie.ui.components.WallpaperGrid
import com.shejan.wallpie.ui.components.WallpaperCard
import com.shejan.wallpie.ui.viewmodel.WallpaperState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.GridItemSpan

data class FeaturedSection(
    val title: String,
    val description: String,
    val gradient: Brush
)

@Composable
fun ExploreScreen(
    viewModel: WallpaperViewModel,
    onWallpaperClick: (Int) -> Unit
) {
    val exploreState by viewModel.exploreState.collectAsState()
    
    val featuredSections = listOf(
        FeaturedSection(
            "Trending Now", 
            "Most popular right now", 
            Brush.linearGradient(listOf(Color(0xFF6200EE), Color(0xFF3700B3)))
        ),
        FeaturedSection(
            "Wall Of The Day", 
            "Specially picked for today", 
            Brush.linearGradient(listOf(Color(0xFF03DAC5), Color(0xFF018786)))
        ),
        FeaturedSection(
            "Wall Of The Week", 
            "Best of this week", 
            Brush.linearGradient(listOf(Color(0xFFFF0266), Color(0xFFC51162)))
        ),
        FeaturedSection(
            "Wall Of The Month", 
            "Top charts this month", 
            Brush.linearGradient(listOf(Color(0xFFFB8C00), Color(0xFFE65100)))
        )
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 120.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Featured Section Header
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "Featured",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 12.dp)
                )
                
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                ) {
                    items(featuredSections) { section ->
                        FeaturedCard(section)
                    }
                }
            }
        }

        // Popular Wallpapers Title
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = "Popular Wallpapers",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
            )
        }

        // Wallpaper Grid or States
        when (val state = exploreState) {
            is WallpaperState.Loading -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
            is WallpaperState.Success -> {
                itemsIndexed(state.wallpapers) { index, wallpaper ->
                    WallpaperCard(
                        wallpaper = wallpaper,
                        onClick = { onWallpaperClick(index) }
                    )
                }
            }
            is WallpaperState.Error -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Error: ${state.message}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FeaturedCard(section: FeaturedSection) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .fillMaxHeight(),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(section.gradient)
            )
            
            // Content Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f)),
                            startY = 300f
                        )
                    )
                    .padding(20.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Column {
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "SPECIAL",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = section.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = section.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

@Composable
fun FavouriteScreen(
    viewModel: WallpaperViewModel,
    onWallpaperClick: (Int) -> Unit
) {
    val favourites: List<Wallpaper> by viewModel.favouriteWallpapers.collectAsState(initial = emptyList())

    Column(modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()) {
        Text(
            text = "My Favourites",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        if (favourites.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No favourites yet", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            WallpaperGrid(
                wallpapers = favourites,
                onWallpaperClick = { index ->
                    onWallpaperClick(index)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(preferenceManager: PreferenceManager) {
    val currentTheme by preferenceManager.themeFlow.collectAsState(initial = AppTheme.SYSTEM)
    val scope = rememberCoroutineScope()
    var showThemeSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var showClearCacheDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // ... (Header and Sections)
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Appearance Section
        SettingSectionHeader(title = "Appearance")
        SettingsClickableItem(
            title = "Theme",
            subtitle = when (currentTheme) {
                AppTheme.SYSTEM -> "Auto (System Focus)"
                AppTheme.LIGHT -> "Light Mode"
                AppTheme.DARK -> "Dark Mode"
            },
            onClick = { showThemeSheet = true }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // About Section
        SettingSectionHeader(title = "General")
        SettingsItem(title = "Version", subtitle = "1.0.0")
        Spacer(modifier = Modifier.height(12.dp))
        
        SettingsClickableItem(
            title = "Clear Cache",
            subtitle = "Keep your app light",
            onClick = { showClearCacheDialog = true }
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        SettingsClickableItem(
            title = "About",
            subtitle = "Learn more about WallPie",
            onClick = { showAboutDialog = true }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Support Section
        SettingSectionHeader(title = "Support")
        val supportContext = androidx.compose.ui.platform.LocalContext.current
        SettingsClickableItem(
            title = "Privacy Policy",
            onClick = {
                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://www.farjan.me/WallPiePrivacyPolicy/"))
                supportContext.startActivity(intent)
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
        SettingsItem(title = "Terms of Service")
        Spacer(modifier = Modifier.height(12.dp))
        SettingsItem(title = "Contact Us")

        // Dialogs and Sheets
        if (showThemeSheet) {
            ModalBottomSheet(
                onDismissRequest = { showThemeSheet = false },
                sheetState = sheetState,
                dragHandle = { BottomSheetDefaults.DragHandle() },
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                ThemeSelectorContent(
                    currentTheme = currentTheme,
                    onThemeSelected = { theme ->
                        scope.launch {
                            preferenceManager.setTheme(theme)
                            sheetState.hide()
                            showThemeSheet = false
                        }
                    }
                )
            }
        }

        if (showClearCacheDialog) {
            AlertDialog(
                onDismissRequest = { showClearCacheDialog = false },
                title = { Text("Clear Cache") },
                text = { Text("Are you sure you want to clear the app cache? This will free up space but may slightly increase loading times for previously viewed wallpapers.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showClearCacheDialog = false
                            scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                                try {
                                    com.bumptech.glide.Glide.get(context).clearDiskCache()
                                    context.cacheDir.deleteRecursively()
                                    launch(kotlinx.coroutines.Dispatchers.Main) {
                                        com.bumptech.glide.Glide.get(context).clearMemory()
                                        android.widget.Toast.makeText(context, "Cache cleared successfully", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    launch(kotlinx.coroutines.Dispatchers.Main) {
                                        android.widget.Toast.makeText(context, "Failed to clear cache", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    ) {
                        Text("Clear", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearCacheDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showAboutDialog) {
            AlertDialog(
                onDismissRequest = { showAboutDialog = false },
                title = { 
                    Column {
                        Text("About WallPie", style = MaterialTheme.typography.headlineSmall)
                        Text("Version 1.0.0", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            "WallPie is a premium wallpaper application designed to bring stunning, high-quality visuals to your device. Hand-picked collections for a unique home screen experience.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Text(
                            "Developed by Farjan Ahmmed",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                        Text(
                            "Thank you for using WallPie! We hope these wallpapers make your day a little brighter.",
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAboutDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(120.dp))
    }
}

@Composable
fun SettingSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
    )
}

@Composable
fun SettingsClickableItem(title: String, subtitle: String? = null, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        tonalElevation = 1.dp,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SettingsItem(title: String, subtitle: String? = null) {
    Surface(
        shape = MaterialTheme.shapes.large,
        tonalElevation = 1.dp,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
@Composable
fun ThemeSelectorContent(
    currentTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = "Choose Theme",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        ThemeRadioButton(
            title = "Auto (System)",
            selected = currentTheme == AppTheme.SYSTEM,
            onClick = { onThemeSelected(AppTheme.SYSTEM) }
        )
        ThemeRadioButton(
            title = "Light Mode",
            selected = currentTheme == AppTheme.LIGHT,
            onClick = { onThemeSelected(AppTheme.LIGHT) }
        )
        ThemeRadioButton(
            title = "Dark Mode",
            selected = currentTheme == AppTheme.DARK,
            onClick = { onThemeSelected(AppTheme.DARK) }
        )
    }
}

@Composable
fun ThemeRadioButton(title: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        RadioButton(selected = selected, onClick = onClick)
    }
}
