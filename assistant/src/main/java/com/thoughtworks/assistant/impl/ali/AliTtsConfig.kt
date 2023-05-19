package com.thoughtworks.assistant.impl.ali

import com.thoughtworks.assistant.impl.ali.AliTtsConstant.DEFAULT_MODE
import com.thoughtworks.assistant.impl.ali.AliTtsConstant.DEFAULT_URL
import org.json.JSONObject

data class AliTtsConfig(
    val accessKey: String = "",
    val accessKeySecret: String = "",
    val appKey: String = "",
    val modeType: Int = DEFAULT_MODE,
    val url: String = DEFAULT_URL,
) {
    var deviceId: String = ""
    var workspace: String = ""
    var token: String = ""

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
            put("mode_type", modeType.toString())
        }
        return jsonObj.toString()
    }
}