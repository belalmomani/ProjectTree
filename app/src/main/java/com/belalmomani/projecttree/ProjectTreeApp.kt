package com.belalmomani.projecttree

import android.app.Application
import com.belalmomani.projecttree.ads.AdsManager

class ProjectTreeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AdsManager.init(this)
    }
}
