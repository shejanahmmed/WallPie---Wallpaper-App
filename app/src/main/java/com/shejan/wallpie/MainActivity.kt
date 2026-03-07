package com.shejan.wallpie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.ads.MobileAds
import com.shejan.wallpie.network.RetrofitInstance
import com.shejan.wallpie.repository.WallpaperRepository
import com.shejan.wallpie.ui.screens.HomeScreen
import com.shejan.wallpie.ui.screens.PreviewScreen
import com.shejan.wallpie.ui.theme.WallPieTheme
import com.shejan.wallpie.ui.viewmodel.WallpaperViewModel
import com.shejan.wallpie.ui.viewmodel.WallpaperViewModelFactory
import com.shejan.wallpie.utils.AdManager
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {

    private val repository by lazy { WallpaperRepository(RetrofitInstance.api) }
    private val viewModel: WallpaperViewModel by viewModels { WallpaperViewModelFactory(repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize AdMob
        MobileAds.initialize(this) {}
        AdManager.loadInterstitialAd(this)

        setContent {
            WallPieTheme {
                WallPieApp(viewModel, this)
            }
        }
    }
}

@Composable
fun WallPieApp(viewModel: WallpaperViewModel, activity: ComponentActivity) {
    val navController = rememberNavController()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    viewModel = viewModel,
                    onWallpaperClick = { index ->
                        navController.navigate("preview/$index")
                        
                        // Show interstitial ad
                        AdManager.showInterstitialAd(activity)
                    }
                )
            }
            composable(
                route = "preview/{index}",
                arguments = listOf(
                    navArgument("index") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val index = backStackEntry.arguments?.getInt("index") ?: 0
                PreviewScreen(
                    viewModel = viewModel,
                    initialIndex = index,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
