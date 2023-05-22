package com.thoughtworks.assistant

import android.content.Context
import com.thoughtworks.assistant.tts.Tts
import com.thoughtworks.assistant.tts.TtsType
import com.thoughtworks.assistant.tts.ali.AliTts

class SmartAssistant(private val context: Context) {

    fun getTts(ttsType: TtsType = TtsType.Ali, params: Map<String, String> = emptyMap()): Tts {
        check(ttsType == TtsType.Ali) {
            "Not supported type: ${ttsType.name}!"
        }
        return AliTts(context, params)
    }
}