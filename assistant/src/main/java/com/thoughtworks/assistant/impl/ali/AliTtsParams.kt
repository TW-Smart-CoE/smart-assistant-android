package com.thoughtworks.assistant.impl.ali

import com.thoughtworks.assistant.impl.ali.AliTtsConstant.DEFAULT_ENCODE_TYPE
import com.thoughtworks.assistant.impl.ali.AliTtsConstant.DEFAULT_FONT_NAME
import com.thoughtworks.assistant.impl.ali.AliTtsConstant.DEFAULT_SAMPLE_RATE

data class AliTtsParams(
    val fontName: String = DEFAULT_FONT_NAME,
    val enableSubtitle: Boolean = true,
    val sampleRate: Int = DEFAULT_SAMPLE_RATE,
    val encodeType: String = DEFAULT_ENCODE_TYPE,
    val extraParam: Map<String, String> = emptyMap()
) {
    fun toParams(): Map<String, String> {
        val result = mutableMapOf<String, String>()
        result["font_name"] = fontName
        result["enable_subtitle"] = if (enableSubtitle) "1" else "0"
        result["sample_rate"] = sampleRate.toString()
        result["encode_type"] = encodeType
        result.putAll(extraParam)
        return result
    }
}