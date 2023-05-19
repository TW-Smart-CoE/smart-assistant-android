package com.thoughtworks.assistant.impl.ali

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.alibaba.idst.nui.CommonUtils
import com.alibaba.nls.client.AccessToken
import com.thoughtworks.assistant.impl.ali.AliTtsConstant.TAG
import com.thoughtworks.assistant.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("StaticFieldLeak")
object AliTtsInitializer {
    lateinit var context: Context
    var isInit = false

    var config = AliTtsConfig()
    var params = AliTtsParams()

    val coroutineScope = MainScope()
    var initJob: Job? = null

    fun init(
        context: Context,
        config: AliTtsConfig = AliTtsConfig(),
        params: AliTtsParams = AliTtsParams()
    ) {
        initJob = coroutineScope.launch {
            CommonUtils.copyAssetsData(context)

            this@AliTtsInitializer.context = context.applicationContext
            this@AliTtsInitializer.config = config.apply {
                token = withContext(Dispatchers.IO) {
                    getToken()
                }
                deviceId = Utils.getDeviceId(context)
                workspace = CommonUtils.getModelPath(context)
            }
            this@AliTtsInitializer.params = params
            this@AliTtsInitializer.isInit = true
        }
    }

    private fun getToken(): String {
        return try {
            val accessToken = AccessToken(config.accessKey, config.accessKeySecret)
            accessToken.apply()
            accessToken.token
        } catch (e: Exception) {
            Log.e(TAG, "Get token failed!")
            return ""
        }
    }
}