package com.thoughtworks.assistant.abilities.wakeup.baidu

import android.content.Context
import android.util.Log
import com.baidu.speech.EventListener
import com.baidu.speech.EventManager
import com.baidu.speech.EventManagerFactory
import com.baidu.speech.asr.SpeechConstant
import com.thoughtworks.assistant.utils.Utils.getManifestMetaData
import com.thoughtworks.assistant.abilities.wakeup.WakeUp
import com.thoughtworks.assistant.abilities.wakeup.WakeUpListener
import com.thoughtworks.assistant.abilities.wakeup.baidu.BaiduWakeUpConstant.TAG
import org.json.JSONObject

class BaiduWakeUp(
    private val context: Context,
    private val params: Map<String, Any> = emptyMap(),
    wakeUpListener: WakeUpListener? = null
    ) : WakeUp {
    private var isInited = false

    private var wakeUpListener: WakeUpListener? = null
    private val wp: EventManager = EventManagerFactory.create(context, "wp")
    private val eventListener: EventListener =
        EventListener { name, params, data, offset, length ->
            when (name) {
                SpeechConstant.CALLBACK_EVENT_WAKEUP_SUCCESS -> {
                    val result = WakeUpResult.parseJson(name, params)
                    if (result == null) {
                        this.wakeUpListener?.onError(-1, "parse json error")
                    } else {
                        this.wakeUpListener?.onSuccess()
                    }
                }

                SpeechConstant.CALLBACK_EVENT_WAKEUP_ERROR -> {
                    val errorCode = JSONObject(params).optInt("error")
                    val errorMessage = JSONObject(params).optString("desc")
                    this.wakeUpListener?.onError(errorCode, errorMessage)
                }

                SpeechConstant.CALLBACK_EVENT_WAKEUP_STOPED -> {
                    this.wakeUpListener?.onStop()
                }
            }
        }

    init {
        this.wakeUpListener = wakeUpListener
        wp.registerListener(eventListener)
    }

    override fun setWakeUpListener(wakeUpListener: WakeUpListener?) {
        this.wakeUpListener = wakeUpListener
    }

    override fun start() {
        val wpParams: MutableMap<String, Any> = mutableMapOf()
        wpParams[SpeechConstant.APP_ID] =
            params["app_id"]?.toString() ?: context.getManifestMetaData(BaiduWakeUpConstant.META_DATA_APP_ID)
        wpParams[SpeechConstant.APP_KEY] =
            params["api_key"]?.toString() ?: context.getManifestMetaData(BaiduWakeUpConstant.META_DATA_API_KEY)
        wpParams[SpeechConstant.SECRET] =
            params["secret_key"]?.toString() ?: context.getManifestMetaData(BaiduWakeUpConstant.META_DATA_SECRET_KEY)

        wpParams[SpeechConstant.WP_WORDS_FILE] =
            params[SpeechConstant.WP_WORDS_FILE] ?: "assets:///WakeUp.bin"

        val json = (wpParams as Map<*, *>?)?.let { JSONObject(it).toString() }
        wp.send(SpeechConstant.WAKEUP_START, json, null, 0, 0)
    }

    override fun stop() {
        wp.send(SpeechConstant.WAKEUP_STOP, null, null, 0, 0)
    }

    override fun release() {
        stop()
        wp.unregisterListener(eventListener)
        isInited = false
    }
}