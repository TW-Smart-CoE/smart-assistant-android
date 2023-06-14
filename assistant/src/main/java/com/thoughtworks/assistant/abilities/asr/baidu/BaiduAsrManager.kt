package com.thoughtworks.assistant.abilities.asr.baidu

import android.content.Context
import android.util.Log
import com.baidu.speech.EventListener
import com.baidu.speech.EventManager
import com.baidu.speech.EventManagerFactory
import com.baidu.speech.asr.SpeechConstant
import com.thoughtworks.assistant.abilities.asr.baidu.BaiduAsrConstant.FINAL_RESULT
import com.thoughtworks.assistant.abilities.asr.baidu.BaiduAsrConstant.NLU_RESULT
import com.thoughtworks.assistant.abilities.asr.baidu.BaiduAsrConstant.PARTIAL_RESULT
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import org.json.JSONObject

class BaiduAsrManager {
    var eventManager: EventManager? = null
    private var dataChannel: Channel<BaiduAsrData>? = null

    private val eventListener = EventListener { name, params, data, offset, length ->
        var logTxt = ""
        when (name) {
            SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL -> {
                if (params == null || params.isEmpty()) {
                    return@EventListener
                }
                if (params.contains(NLU_RESULT)) {
                    if (length > 0 && data.isNotEmpty()) {
                        logTxt += ", semantic parsing results\n：" + String(data, offset, length)
                    }
                } else if (params.contains(PARTIAL_RESULT)) {
                    logTxt += ", temporary recognition results\n：$params"
                } else if (params.contains(FINAL_RESULT)) {
                    val bestResult =
                        JSONObject(params).optString(BaiduAsrConstant.BEST_RESULT)
                    logTxt += "final recognition result：$bestResult"
                    dataChannel?.trySend(BaiduAsrData(bestResult))
                    closeDataChannel()
                } else {
                    logTxt += " params :$params"
                    if (data != null) {
                        logTxt += " data length=" + data.size
                    }
                }
            }

            SpeechConstant.CALLBACK_EVENT_ASR_ERROR -> {
                dataChannel?.trySend(BaiduAsrData("", -1, params))
                closeDataChannel()
            }

            else -> {
                logTxt += "name: $name"
                if (params != null && params.isNotEmpty()) {
                    logTxt += " ;params :$params"
                }
                if (data != null) {
                    logTxt += " ;data length=" + data.size
                }
            }
        }
        Log.d("BaiduAsrCreator", logTxt)
    }


    fun create(context: Context, params: Map<String, Any>): Flow<BaiduAsrData> {
        eventManager = EventManagerFactory.create(context, "asr").also {
            it.registerListener(eventListener)
            it.send(SpeechConstant.ASR_START, JSONObject(params).toString(), null, 0, 0)
        }
        val channel = Channel<BaiduAsrData>(Channel.UNLIMITED)
        dataChannel = channel

        return channel.consumeAsFlow()
    }

    private fun closeDataChannel() {
        dataChannel?.close()
        dataChannel = null
    }

    fun stop() {
        eventManager?.send(SpeechConstant.ASR_STOP, null, null, 0, 0)
    }

    fun release() {
        eventManager?.unregisterListener(eventListener)
    }
}