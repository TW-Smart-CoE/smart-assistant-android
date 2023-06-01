package com.thoughtworks.assistant.abilities.wakeup.picovoice

import ai.picovoice.porcupine.PorcupineManager
import android.content.Context
import android.util.Log
import com.thoughtworks.assistant.abilities.wakeup.WakeUp
import com.thoughtworks.assistant.abilities.wakeup.WakeUpListener
import com.thoughtworks.assistant.utils.Utils.getManifestMetaData

class PicovoiceWakeUp(
    context: Context,
    params: Map<String, Any> = emptyMap(),
    wakeUpListener: WakeUpListener? = null
) :
    WakeUp {
    private var keywordCount = 0
    private var wakeUpListener: WakeUpListener? = null
    private var porcupineManager: PorcupineManager? = null

    init {
        this.wakeUpListener = wakeUpListener

        val keywordList = (params["keyword_paths"] as List<*>)
        keywordCount = keywordList.size
        val keywordArray =
            Array(keywordList.size) { i -> keywordList[i].toString() }

        try {
            porcupineManager = PorcupineManager.Builder()
                .setAccessKey(params["access_key"]?.toString() ?: context.getManifestMetaData(META_DATA_ACCESS_KEY))
//                .setKeywords(arrayOf(Porcupine.BuiltInKeyword.PORCUPINE, Porcupine.BuiltInKeyword.BUMBLEBEE))
                .setKeywordPaths(keywordArray)
                .build(context) { keywordIndex ->
                    if (keywordIndex < keywordCount) {
                        wakeUpListener?.onSuccess()
                    } else {
                        Log.e(TAG, "keywordIndex out of range")
                        wakeUpListener?.onError(-2, "keywordIndex out of range")
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, e.message ?: "unknown error")
            wakeUpListener?.onError(-1, e.message ?: "unknown error")
        }
    }

    override fun setWakeUpListener(wakeUpListener: WakeUpListener?) {
        this.wakeUpListener = wakeUpListener
    }

    override fun start() {
        porcupineManager?.start()
    }

    override fun stop() {
        porcupineManager?.stop()
    }

    override fun release() {
        porcupineManager?.delete()
        porcupineManager = null
    }

    companion object {
        private const val TAG = "SmartAssistant.PicovoiceWakeUp"
        private const val META_DATA_ACCESS_KEY = "PICOVOICE_ACCESS_KEY"
    }
}