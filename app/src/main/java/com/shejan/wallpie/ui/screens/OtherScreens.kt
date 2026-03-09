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
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*

@Composable
fun ExploreScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Explore Screen", style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
fun FavouriteScreen(
    viewModel: WallpaperViewModel,
    onWallpaperClick: (Int) -> Unit
) {
    val favourites: List<Wallpaper> by viewModel.favouriteWallpapers.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize()) {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 100.dp)
            .verticalScroll(rememberScrollState())
    ) {
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
        SettingsItem(title = "Clear Cache", subtitle = "Keep your app light")

        Spacer(modifier = Modifier.height(24.dp))

        // Support Section
        SettingSectionHeader(title = "Support")
        val context = androidx.compose.ui.platform.LocalContext.current
        SettingsClickableItem(
            title = "Privacy Policy",
            onClick = {
                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://www.farjan.me/WallPiePrivacyPolicy/"))
                context.startActivity(intent)
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
        SettingsItem(title = "Terms of Service")
        Spacer(modifier = Modifier.height(12.dp))
        SettingsItem(title = "Contact Us")

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
