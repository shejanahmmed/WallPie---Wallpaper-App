package com.shejan.wallpie.utils

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdManager {
    private var interstitialAd: InterstitialAd? = null
    private var openCount = 0

    fun loadInterstitialAd(context: Context) {
        val adRequest = AdRequest.Builder().build()
        // Sample Interstitial Ad ID
        InterstitialAd.load(context, "ca-app-pub-3940256099942544/1033173712", adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }
            })
    }

    fun showInterstitialAd(activity: Activity) {
        openCount++
        if (openCount % 5 == 0 && interstitialAd != null) {
            interstitialAd?.show(activity)
            loadInterstitialAd(activity) // Load next one
        }
    }
}
