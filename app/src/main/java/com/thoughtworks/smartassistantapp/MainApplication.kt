@file:Suppress("PackageNaming")

package com.thoughtworks.smartassistantapp

import android.app.Application
import com.thoughtworks.assistant.impl.ali.AliTtsConfig
import com.thoughtworks.assistant.impl.ali.AliTtsInitializer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // accessKey和accessKeySecret请参考阿里云文档进行获取：https://help.aliyun.com/document_detail/72138.htm?spm=a2c4g.72153.0.0.7aab596b5MUAHo
        AliTtsInitializer.init(
            this,
            AliTtsConfig(
                accessKey = "",
                accessKeySecret = "",
                appKey = ""
            )
        )
    }
}