package com.thoughtworks.assistant

import android.content.Context
import com.thoughtworks.assistant.asr.Asr
import com.thoughtworks.assistant.asr.AsrType
import com.thoughtworks.assistant.asr.ali.AliAsr
import com.thoughtworks.assistant.tts.Tts
import com.thoughtworks.assistant.tts.TtsType
import com.thoughtworks.assistant.tts.ali.AliTts
import com.thoughtworks.assistant.wakeup.WakeUp
import com.thoughtworks.assistant.wakeup.WakeUpType
import com.thoughtworks.assistant.wakeup.baidu.BaiduWakeUp

class SmartAssistant(private val context: Context) {

    fun getTts(ttsType: TtsType = TtsType.Ali, params: Map<String, Any> = emptyMap()): Tts {
        check(ttsType == TtsType.Ali) {
            "Not supported type: ${ttsType.name}!"
        }
        return AliTts(context, params)
    }

    fun getWakeUp(
        wakeUpType: WakeUpType = WakeUpType.Baidu,
        params: Map<String, String> = emptyMap()
    ): WakeUp {
        check(wakeUpType == WakeUpType.Baidu) {
            "Not supported type: ${wakeUpType.name}!"
        }
        return BaiduWakeUp(context, params)
    }

    fun getAsr(asrType: AsrType = AsrType.Ali, params: Map<String, Any> = emptyMap()): Asr {
        check(asrType == AsrType.Ali) {
            "Not supported type: ${asrType.name}!"
        }
        return AliAsr(context, params)
    }
}