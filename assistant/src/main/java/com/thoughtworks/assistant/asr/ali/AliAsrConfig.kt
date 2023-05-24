package com.thoughtworks.assistant.asr.ali

import com.alibaba.idst.nui.Constants
import com.thoughtworks.assistant.tts.ali.AliTtsConstant.DEFAULT_URL
import org.json.JSONObject

data class AliAsrConfig(
    val accessKey: String = "",
    val accessKeySecret: String = "",
    val appKey: String = "",
    var deviceId: String = "",
    var workspace: String = "",
    var debugPath: String = "",
    var token: String = "",
    val url: String = DEFAULT_URL,
) {
    fun toTicket(): String {
        if (appKey.isEmpty() || token.isEmpty()) {
            return ""
        }

        val jsonObj = JSONObject().apply {
            put("app_key", appKey)
            put("token", token)
            put("device_id", deviceId)
            put("url", url)
            put("workspace", workspace)
            if (debugPath.isNotEmpty()) {
                put("debug_path", debugPath)
            }
            put("sample_rate", "16000")
            put("format", "opus")
//          put("save_wav", "true")
            // FullMix = 0   // 选用此模式开启本地功能并需要进行鉴权注册
            // FullCloud = 1
            // FullLocal = 2 // 选用此模式开启本地功能并需要进行鉴权注册
            // AsrMix = 3    // 选用此模式开启本地功能并需要进行鉴权注册
            // AsrCloud = 4
            // AsrLocal = 5  // 选用此模式开启本地功能并需要进行鉴权注册
            put("service_mode", Constants.ModeAsrCloud)
        }
        return jsonObj.toString()
    }
}