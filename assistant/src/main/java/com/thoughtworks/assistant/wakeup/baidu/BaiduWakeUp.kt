package com.thoughtworks.assistant.wakeup.baidu

import android.content.Context
import android.util.Log
import com.baidu.speech.EventListener
import com.baidu.speech.EventManager
import com.baidu.speech.EventManagerFactory
import com.baidu.speech.asr.SpeechConstant
import com.thoughtworks.assistant.utils.Utils.getManifestMetaData
import com.thoughtworks.assistant.wakeup.WakeUp
import com.thoughtworks.assistant.wakeup.WakeUpListener
import org.json.JSONObject

class BaiduWakeUp(private val context: Context, private val params: Map<String, String>) : WakeUp {
    private var isInited = false

    private var wakeUpListener: WakeUpListener? = null
    private val wp: EventManager = EventManagerFactory.create(context, "wp")
    private val eventListener: EventListener =
        EventListener { name, params, data, offset, length ->
            when (name) {
                SpeechConstant.CALLBACK_EVENT_WAKEUP_SUCCESS -> {
                    Log.d("MainActivity", "wakeup success")
                    val result = WakeUpResult.parseJson(name, params)
                    if (result == null) {
                        wakeUpListener?.onError(-1, "parse json error")
                    } else {
                        wakeUpListener?.onSuccess(result.word ?: "")
                    }
                }

                SpeechConstant.CALLBACK_EVENT_WAKEUP_ERROR -> {
                    Log.d("MainActivity", "wakeup error$params")
                    val errorCode = JSONObject(params).optInt("error")
                    val errorMessage = JSONObject(params).optString("desc")
                    wakeUpListener?.onError(errorCode, errorMessage)
                }

                SpeechConstant.CALLBACK_EVENT_WAKEUP_STOPED -> {
                    Log.d("MainActivity", "wakeup stop")
                    wakeUpListener?.onStop()
                }
            }
        }

    init {
        wp.registerListener(eventListener)
    }

    override fun setWakeUpListener(wakeUpListener: WakeUpListener?) {
        this.wakeUpListener = wakeUpListener
    }

    override fun start() {
        val wpParams: MutableMap<String, Any> = mutableMapOf()
        wpParams[SpeechConstant.APP_ID] =
            context.getManifestMetaData(BaiduWakeUpConstant.META_DATA_APP_ID)
        wpParams[SpeechConstant.APP_KEY] =
            context.getManifestMetaData(BaiduWakeUpConstant.META_DATA_API_KEY)
        wpParams[SpeechConstant.SECRET] =
            context.getManifestMetaData(BaiduWakeUpConstant.META_DATA_SECRET_KEY)
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