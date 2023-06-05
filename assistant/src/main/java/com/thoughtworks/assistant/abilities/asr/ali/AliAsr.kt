package com.thoughtworks.assistant.abilities.asr.ali

import android.content.Context
import com.thoughtworks.assistant.abilities.asr.Asr
import com.thoughtworks.assistant.abilities.asr.AsrListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

class AliAsr(context: Context, params: Map<String, Any>) : Asr {
    init {
        AliAsrInitializer.init(context, params)
    }

    private val aliAsrCreator = AliAsrCreator(context, params)
    override fun setAsrListener(listener: AsrListener?) {
        aliAsrCreator.setAsrListener(listener)
    }

    override suspend fun startListening(): String {
        var result = ""
        aliAsrCreator.create()
            .flowOn(Dispatchers.IO)
            .collect {
                result = it.text
            }
        return result
    }

    override suspend fun stopListening() {
        aliAsrCreator.stop()
    }

    override fun release() {
        aliAsrCreator.release()
    }
}