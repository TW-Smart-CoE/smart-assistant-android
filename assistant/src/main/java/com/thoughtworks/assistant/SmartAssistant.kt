package com.thoughtworks.assistant

import android.content.Context
import com.thoughtworks.assistant.abilities.asr.Asr
import com.thoughtworks.assistant.abilities.asr.AsrType
import com.thoughtworks.assistant.abilities.asr.ali.AliAsr
import com.thoughtworks.assistant.abilities.asr.baidu.BaiduAsr
import com.thoughtworks.assistant.abilities.chat.Chat
import com.thoughtworks.assistant.abilities.chat.ChatType
import com.thoughtworks.assistant.abilities.chat.chatgpt.ChatGpt
import com.thoughtworks.assistant.abilities.tts.Tts
import com.thoughtworks.assistant.abilities.tts.TtsType
import com.thoughtworks.assistant.abilities.tts.ali.AliTts
import com.thoughtworks.assistant.abilities.tts.google.GoogleTts
import com.thoughtworks.assistant.abilities.wakeup.WakeUp
import com.thoughtworks.assistant.abilities.wakeup.WakeUpListener
import com.thoughtworks.assistant.abilities.wakeup.WakeUpType
import com.thoughtworks.assistant.abilities.wakeup.baidu.BaiduWakeUp
import com.thoughtworks.assistant.abilities.wakeup.picovoice.PicovoiceWakeUp

class SmartAssistant(private val context: Context) {
    fun createTts(ttsType: TtsType = TtsType.Ali, params: Map<String, Any> = emptyMap()): Tts {
        check(
            ttsType == TtsType.Ali ||
                    ttsType == TtsType.Google
        ) {
            "Not supported type: ${ttsType.name}!"
        }

        return when (ttsType) {
            TtsType.Ali -> AliTts(context, params)
            TtsType.Google -> GoogleTts(context, params)
        }
    }

    fun createWakeUp(
        wakeUpType: WakeUpType = WakeUpType.Baidu,
        params: Map<String, Any> = emptyMap(),
        wakepListener: WakeUpListener? = null
    ): WakeUp {
        check(
            wakeUpType == WakeUpType.Baidu ||
                    wakeUpType == WakeUpType.Picovoice
        ) {
            "Not supported type: ${wakeUpType.name}!"
        }

        return when (wakeUpType) {
            WakeUpType.Baidu -> BaiduWakeUp(context, params, wakepListener)
            WakeUpType.Picovoice -> PicovoiceWakeUp(context, params, wakepListener)
        }
    }

    fun createAsr(asrType: AsrType = AsrType.Ali, params: Map<String, Any> = emptyMap()): Asr {
        check(asrType == AsrType.Ali || asrType == AsrType.BaiDu) {
            "Not supported type: ${asrType.name}!"
        }
        return when (asrType) {
            AsrType.Ali -> AliAsr(context, params)
            AsrType.BaiDu -> BaiduAsr(context, params)
        }
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