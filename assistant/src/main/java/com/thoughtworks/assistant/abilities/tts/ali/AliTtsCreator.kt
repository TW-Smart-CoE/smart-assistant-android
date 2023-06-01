package com.thoughtworks.assistant.abilities.tts.ali

import android.util.Log
import com.alibaba.idst.nui.Constants
import com.alibaba.idst.nui.INativeTtsCallback
import com.alibaba.idst.nui.NativeNui
import com.thoughtworks.assistant.abilities.tts.ali.AliTtsConstant.TAG
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import java.util.*

class AliTtsCreator(private val params: Map<String, Any>) {
    private var isInit = false
    private val ttsInstance = NativeNui(Constants.ModeType.MODE_TTS)
    private val ttsTaskMap = mutableMapOf<String, Channel<AliTtsData>>()
    private var currentChannel: Channel<AliTtsData>? = null

    init {
        AliTtsInitializer.coroutineScope.launch {
            AliTtsInitializer.initJob?.join()
            initTTSInstance()
        }
    }

    private fun initTTSInstance() {
        val ticket = AliTtsInitializer.ttsConfig.toTicket()
        val initResult = ttsInstance.tts_initialize(
            object : INativeTtsCallback {
                override fun onTtsEventCallback(
                    event: INativeTtsCallback.TtsEvent,
                    taskId: String,
                    resultCode: Int
                ) {
                    when (event) {
                        INativeTtsCallback.TtsEvent.TTS_EVENT_START -> {
                            currentChannel = ttsTaskMap[taskId]
                        }

                        INativeTtsCallback.TtsEvent.TTS_EVENT_END -> {
                            currentChannel?.close()
                        }

                        INativeTtsCallback.TtsEvent.TTS_EVENT_ERROR -> {
                            val errorMsg = ttsInstance.getparamTts("error_msg")
                            Log.e(TAG, "TTS_EVENT_ERROR error_code:$resultCode err_msg:$errorMsg")
                        }

                        else -> {}
                    }
                }

                override fun onTtsDataCallback(info: String, infoLen: Int, data: ByteArray) {
                    if (data.isNotEmpty()) {
                        currentChannel?.trySend(AliTtsData(info, infoLen, data))
                    }
                }

                override fun onTtsVolCallback(vol: Int) {
                    // do nothing
                }
            },
            ticket,
            Constants.LogLevel.LOG_LEVEL_VERBOSE,
            true
        )

        if (initResult == Constants.NuiResultCode.SUCCESS) {
            val ttsParams = AliTtsInitializer.ttsParams.toParams(params)
            ttsParams.forEach {
                ttsInstance.setparamTts(it.key, it.value.toString())
            }
            isInit = true
        }
    }

    suspend fun create(text: String): Flow<AliTtsData> {
        if (!isInit) {
            AliTtsInitializer.initJob?.join()
            initTTSInstance()
        }

        if (text.isEmpty()) return emptyFlow()

        val channel = Channel<AliTtsData>(Channel.UNLIMITED)
        val taskId = UUID.randomUUID().toString().replace("-", "")
        ttsTaskMap[taskId] = channel

        val charNum = ttsInstance.getUtf8CharsNum(text)

        val ttsVersion = if (charNum > MAX_TEXT_NUM) 1 else 0
        ttsInstance.setparamTts("tts_version", ttsVersion.toString())
        ttsInstance.startTts("1", taskId, text)

        return channel.consumeAsFlow()
    }

    fun release() {
        ttsInstance.release()
    }

    fun configure(params: Map<String, Any>) {
        params.forEach {
            ttsInstance.setparamTts(it.key, it.value.toString())
        }
    }

    companion object {
        const val MAX_TEXT_NUM = 300
    }
}