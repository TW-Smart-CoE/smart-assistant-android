package com.thoughtworks.assistant.tts.ali

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.alibaba.idst.nui.CommonUtils
import com.alibaba.nls.client.AccessToken
import com.thoughtworks.assistant.tts.ali.AliTtsConstant.META_DATA_ACCESS_KEY
import com.thoughtworks.assistant.tts.ali.AliTtsConstant.META_DATA_ACCESS_KEY_SECRET
import com.thoughtworks.assistant.tts.ali.AliTtsConstant.META_DATA_APP_KEY
import com.thoughtworks.assistant.tts.ali.AliTtsConstant.MILL_SECONDS
import com.thoughtworks.assistant.tts.ali.AliTtsConstant.TAG
import com.thoughtworks.assistant.utils.SpUtils
import com.thoughtworks.assistant.utils.Utils.getDeviceId
import com.thoughtworks.assistant.utils.Utils.getManifestMetaData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

@SuppressLint("StaticFieldLeak")
object AliTtsInitializer {
    lateinit var context: Context
    var isInit = false

    var ttsConfig = AliTtsConfig()
    var ttsParams = AliTtsParams()

    val coroutineScope = MainScope()
    var initJob: Job? = null

    fun init(context: Context) {
        if (isInit) return

        initJob = coroutineScope.launch {
            copySdkAssets(context)

            AliTtsInitializer.context = context.applicationContext
            ttsConfig = createConfig(context)
            isInit = true
        }
    }

    private suspend fun createConfig(context: Context): AliTtsConfig {
        val accessKey = context.getManifestMetaData(META_DATA_ACCESS_KEY)
        val accessKeySecret = context.getManifestMetaData(META_DATA_ACCESS_KEY_SECRET)
        val appKey = context.getManifestMetaData(META_DATA_APP_KEY)
        val deviceId = context.getDeviceId()
        val workspace = CommonUtils.getModelPath(context)
        val token = getToken(accessKey, accessKeySecret)
        return AliTtsConfig(accessKey, accessKeySecret, appKey, deviceId, workspace, token)
    }

    private suspend fun copySdkAssets(context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            CommonUtils.copyAssetsData(context)
        }
    }

    private suspend fun getToken(accessKey: String, accessKeySecret: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val spUtils = SpUtils(context)
                val savedExpireTime = spUtils.getLong(SpUtils.SP_ALI_EXPIRE_TIME_KEY)
                if (savedExpireTime == 0L || savedExpireTime * MILL_SECONDS <= System.currentTimeMillis()) {
                    val accessToken = AccessToken(accessKey, accessKeySecret)
                    accessToken.apply()

                    val expireTime = accessToken.expireTime
                    val token = accessToken.token
                    spUtils.saveLong(SpUtils.SP_ALI_EXPIRE_TIME_KEY, expireTime)
                    spUtils.saveStr(SpUtils.SP_ALI_ACCESS_TOKEN_KEY, token)

                    token
                } else {
                    spUtils.getStr(SpUtils.SP_ALI_ACCESS_TOKEN_KEY)
                }
            } catch (e: IOException) {
                Log.e(TAG, "Get token failed!", e)
                ""
            }
        }
    }
}