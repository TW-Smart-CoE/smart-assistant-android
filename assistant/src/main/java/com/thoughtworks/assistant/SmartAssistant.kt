package com.thoughtworks.assistant

import android.content.Context
import com.thoughtworks.assistant.abilities.asr.Asr
import com.thoughtworks.assistant.abilities.asr.AsrType
import com.thoughtworks.assistant.abilities.asr.ali.AliAsr
import com.thoughtworks.assistant.abilities.chat.Chat
import com.thoughtworks.assistant.abilities.chat.ChatType
import com.thoughtworks.assistant.abilities.chat.chatgpt.ChatGpt
import com.thoughtworks.assistant.abilities.tts.Tts
import com.thoughtworks.assistant.abilities.tts.TtsType
import com.thoughtworks.assistant.abilities.tts.ali.AliTts
import com.thoughtworks.assistant.abilities.wakeup.WakeUp
import com.thoughtworks.assistant.abilities.wakeup.WakeUpType
import com.thoughtworks.assistant.abilities.wakeup.baidu.BaiduWakeUp

class SmartAssistant(private val context: Context) {

    fun createTts(ttsType: TtsType = TtsType.Ali, params: Map<String, Any> = emptyMap()): Tts {
        check(ttsType == TtsType.Ali) {
            "Not supported type: ${ttsType.name}!"
        }
        return AliTts(context, params)
    }

    fun createWakeUp(
        wakeUpType: WakeUpType = WakeUpType.Baidu,
        params: Map<String, String> = emptyMap()
    ): WakeUp {
        check(wakeUpType == WakeUpType.Baidu) {
            "Not supported type: ${wakeUpType.name}!"
        }
        return BaiduWakeUp(context, params)
    }

    fun createAsr(asrType: AsrType = AsrType.Ali, params: Map<String, Any> = emptyMap()): Asr {
        check(asrType == AsrType.Ali) {
            "Not supported type: ${asrType.name}!"
        }
        return AliAsr(context, params)
    }

    fun createChat(
        chatType: ChatType,
        params: Map<String, Any> = emptyMap()
    ): Chat {
        check(chatType == ChatType.ChatGpt) {
            "Not supported type: ${chatType.name}!"
        }
        return ChatGpt(context, params)
    }
}