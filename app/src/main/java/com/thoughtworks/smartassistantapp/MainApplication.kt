@file:Suppress("PackageNaming")

package com.thoughtworks.smartassistantapp

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val nativeDir = applicationInfo.nativeLibraryDir
        Log.d("MainActivity", "so 库的存放库为：$nativeDir")
    }
}