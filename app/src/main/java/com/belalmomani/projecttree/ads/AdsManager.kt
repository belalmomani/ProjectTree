package com.belalmomani.projecttree.ads

import android.app.Application
import com.belalmomani.projecttree.BuildConfig
import com.startapp.sdk.adsbase.StartAppSDK

/** كل منطق Start.io هنا فقط. لتعطيل الإعلانات: ADS_ENABLED=false في ads.properties. */
object AdsManager {
    @Volatile
    var enabled: Boolean = false
        private set

    fun init(app: Application) {
        if (!BuildConfig.ADS_ENABLED || BuildConfig.STARTIO_APP_ID.isBlank()) return
        runCatching {
            StartAppSDK.setTestAdsEnabled(BuildConfig.TEST_ADS)
            StartAppSDK.init(app, BuildConfig.STARTIO_APP_ID, false)
            enabled = true
        }
    }
}
