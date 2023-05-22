package com.thoughtworks.assistant.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings

object Utils {
    @SuppressLint("HardwareIds")
    fun Context.getDeviceId(): String {
        return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun Context.getManifestMetaData(key: String): String {
        val appInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getApplicationInfo(
                packageName,
                PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA.toLong())
            )
        } else {
            packageManager.getApplicationInfo(
                packageName,
                PackageManager.GET_META_DATA
            )
        }
        return appInfo.metaData.getString(key, "")
    }
}