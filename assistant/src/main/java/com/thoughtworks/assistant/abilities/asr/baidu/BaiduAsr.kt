package com.thoughtworks.assistant.abilities.asr.baidu

import android.content.Context
import com.baidu.speech.asr.SpeechConstant
import com.thoughtworks.assistant.abilities.asr.Asr
import com.thoughtworks.assistant.abilities.asr.AsrListener
import com.thoughtworks.assistant.abilities.wakeup.baidu.BaiduWakeUpConstant
import com.thoughtworks.assistant.utils.Utils.getManifestMetaData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

class BaiduAsr(val context: Context, val params: Map<String, Any> = mapOf()) : Asr {
    private var asrListener: AsrListener? = null
    private val baiduAsrManager = BaiduAsrManager()
    private val bdAsrParams: MutableMap<String, Any> = mutableMapOf()

    init {
        params.forEach {
            bdAsrParams[it.key] = it.value
        }
        bdAsrParams[SpeechConstant.APP_ID] =
            params["app_id"]?.toString()
                ?: context.getManifestMetaData(BaiduWakeUpConstant.META_DATA_APP_ID)
        bdAsrParams[SpeechConstant.APP_KEY] =
            params["api_key"]?.toString()
                ?: context.getManifestMetaData(BaiduWakeUpConstant.META_DATA_API_KEY)
        bdAsrParams[SpeechConstant.SECRET] =
            params["secret_key"]?.toString()
                ?: context.getManifestMetaData(BaiduWakeUpConstant.META_DATA_SECRET_KEY)
    }

    override fun setAsrListener(listener: AsrListener?) {
        asrListener = listener
    }

    override suspend fun startListening(): String {
        var result = ""
        baiduAsrManager.create(context, bdAsrParams)
            .flowOn(Dispatchers.IO)
            .collect {
                result = it.text
            }
        return result
    }

    override suspend fun stopListening() {
        baiduAsrManager.stop()

    }

    override fun release() {
        baiduAsrManager.release()
    }
}