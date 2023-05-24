package com.thoughtworks.assistant.asr.ali

import android.content.Context
import com.thoughtworks.assistant.asr.Asr
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

class AliAsr(context: Context, params: Map<String, Any>) : Asr {
    init {
        AliAsrInitializer.init(context)
    }

    private val aliAsrCreator = AliAsrCreator(context, params)

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

    override suspend fun release() {
        aliAsrCreator.release()
    }
}