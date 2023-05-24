package com.thoughtworks.assistant.tts.ali

import com.thoughtworks.assistant.tts.ali.AliTtsConstant.DEFAULT_ENCODE_TYPE
import com.thoughtworks.assistant.tts.ali.AliTtsConstant.DEFAULT_FONT_NAME
import com.thoughtworks.assistant.tts.ali.AliTtsConstant.DEFAULT_SAMPLE_RATE
import com.thoughtworks.assistant.tts.ali.AliTtsConstant.ENABLE_SUBTITLE

data class AliTtsParams(
    val fontName: String = DEFAULT_FONT_NAME,
    val enableSubtitle: String = ENABLE_SUBTITLE,
    val sampleRate: Int = DEFAULT_SAMPLE_RATE,
    val encodeType: String = DEFAULT_ENCODE_TYPE
) {
    fun toParams(params: Map<String, Any>): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        result["font_name"] = fontName
        result["enable_subtitle"] = enableSubtitle
        result["sample_rate"] = sampleRate.toString()
        result["encode_type"] = encodeType
        result.putAll(params)
        return result
    }
}