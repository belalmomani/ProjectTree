package com.belalmomani.projecttree.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.startapp.sdk.ads.banner.Banner

@Composable
fun BannerAd(modifier: Modifier = Modifier) {
    if (!AdsManager.enabled) return
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context -> Banner(context) },
    )
}
