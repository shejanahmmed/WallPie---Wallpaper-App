package com.shejan.wallpie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.ads.MobileAds
import com.shejan.wallpie.data.AppDatabase
import com.shejan.wallpie.network.RetrofitInstance
import com.shejan.wallpie.repository.WallpaperRepository
import com.shejan.wallpie.ui.screens.*
import com.shejan.wallpie.ui.theme.WallPieTheme
import com.shejan.wallpie.ui.viewmodel.WallpaperViewModel
import com.shejan.wallpie.ui.viewmodel.WallpaperViewModelFactory
import com.shejan.wallpie.utils.AdManager
import com.shejan.wallpie.utils.PreferenceManager
import com.shejan.wallpie.utils.AppTheme
import androidx.compose.foundation.background
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.isSystemInDarkTheme

class MainActivity : ComponentActivity() {

    private val database by lazy { AppDatabase.getDatabase(this) }
    private val repository by lazy { WallpaperRepository(RetrofitInstance.api, database.favouriteDao()) }
    private val viewModel: WallpaperViewModel by viewModels { WallpaperViewModelFactory(repository) }
    private val preferenceManager by lazy { PreferenceManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize AdMob
        MobileAds.initialize(this) {}
        AdManager.loadInterstitialAd(this)

        setContent {
            val currentTheme by preferenceManager.themeFlow.collectAsState(initial = AppTheme.SYSTEM)
            val isDarkTheme = when (currentTheme) {
                AppTheme.SYSTEM -> isSystemInDarkTheme()
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
            }
            WallPieTheme(appTheme = currentTheme) {
                WallPieApp(viewModel, this, preferenceManager, isDarkTheme)
            }
        }
    }
}

@Composable
fun WallPieApp(
    viewModel: WallpaperViewModel, 
    activity: ComponentActivity,
    preferenceManager: PreferenceManager,
    isDarkTheme: Boolean
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(
        NavigationItem("Home", "home", Icons.Filled.Home, Icons.Outlined.Home),
        NavigationItem("Explore", "explore", Icons.Filled.Explore, Icons.Outlined.Explore),
        NavigationItem("Favourite", "favourite", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
        NavigationItem("Settings", "settings", Icons.Filled.Settings, Icons.Outlined.Settings)
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            val showBottomBar = items.any { it.route == currentDestination?.route }
            if (showBottomBar) {
                WallPieBottomBar(
                    items = items,
                    currentDestination = currentDestination,
                    onItemClick = { item ->
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier
        ) {
            composable("home") {
                HomeScreen(
                    viewModel = viewModel,
                    onWallpaperClick = { index ->
                        navController.navigate("preview/$index/all")
                        AdManager.showInterstitialAd(activity)
                    }
                )
            }
            composable("explore") { 
                ExploreScreen(
                    viewModel = viewModel,
                    onWallpaperClick = { index ->
                        navController.navigate("preview/$index/explore")
                    }
                ) 
            }
            composable("favourite") { 
                FavouriteScreen(
                    viewModel = viewModel,
                    onWallpaperClick = { index ->
                        navController.navigate("preview/$index/fav")
                    }
                ) 
            }
            composable("settings") { SettingsScreen(preferenceManager) }
            composable(
                route = "preview/{index}/{source}",
                arguments = listOf(
                    navArgument("index") { type = NavType.IntType },
                    navArgument("source") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val index = backStackEntry.arguments?.getInt("index") ?: 0
                val source = backStackEntry.arguments?.getString("source") ?: "all"
                PreviewScreen(
                    viewModel = viewModel,
                    initialIndex = index,
                    source = source,
                    isDarkTheme = isDarkTheme,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

data class NavigationItem(
    val title: String,
    val route: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
private fun WallPieBottomBar(
    items: List<NavigationItem>,
    currentDestination: NavDestination?,
    onItemClick: (NavigationItem) -> Unit
) {
    val barShape = RoundedCornerShape(28.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = barShape,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            shadowElevation = 8.dp,
            border = BorderStroke(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items.forEach { item ->
                    val selected =
                        currentDestination?.hierarchy?.any { it.route == item.route } == true

                    WallPieBottomBarItem(
                        item = item,
                        selected = selected,
                        modifier = Modifier.weight(1f),
                        onClick = { onItemClick(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun WallPieBottomBarItem(
    item: NavigationItem,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(22.dp)
    val itemContainerColor by animateColorAsState(
        targetValue = if (selected) {
            colorScheme.secondaryContainer.copy(alpha = 0.9f)
        } else {
            Color.Transparent
        },
        label = "bottom_bar_item_container"
    )
    val iconContainerColor by animateColorAsState(
        targetValue = if (selected) {
            colorScheme.primary
        } else {
            colorScheme.surfaceVariant.copy(alpha = 0.75f)
        },
        label = "bottom_bar_icon_container"
    )
    val iconTint by animateColorAsState(
        targetValue = if (selected) colorScheme.onPrimary else colorScheme.onSurfaceVariant,
        label = "bottom_bar_icon_tint"
    )
    val textColor by animateColorAsState(
        targetValue = if (selected) colorScheme.onSecondaryContainer else colorScheme.onSurfaceVariant,
        label = "bottom_bar_text"
    )
    val iconContainerSize = 32.dp

    Surface(
        modifier = modifier
            .height(64.dp)
            .animateContentSize()
            .clip(shape)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.Tab
            ),
        shape = shape,
        color = itemContainerColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(iconContainerSize),
                shape = CircleShape,
                color = iconContainerColor
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text(
                text = item.title,
                modifier = Modifier.padding(top = 2.dp),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                color = textColor,
                maxLines = 1
            )
        }
    }
}
