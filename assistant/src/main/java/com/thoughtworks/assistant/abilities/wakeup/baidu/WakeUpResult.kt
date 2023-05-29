package com.thoughtworks.assistant.abilities.wakeup.baidu

import com.baidu.speech.asr.SpeechConstant
import org.json.JSONException
import org.json.JSONObject

class WakeUpResult {
    var name: String? = null
    var origalJson: String? = null
    var word: String? = null
    var desc: String? = null
    var errorCode = 0
    fun hasError(): Boolean {
        return errorCode != ERROR_NONE
    }

    companion object {
        private const val ERROR_NONE = 0
        private const val TAG = "WakeUpResult"

        fun parseJson(name: String, jsonStr: String?): WakeUpResult? {
            val result = WakeUpResult()
            result.origalJson = jsonStr
            try {
                val json = JSONObject(jsonStr)
                if (SpeechConstant.CALLBACK_EVENT_WAKEUP_SUCCESS == name) {
                    val error = json.optInt("errorCode")
                    result.errorCode = error
                    result.desc = json.optString("errorDesc")
                    if (!result.hasError()) {
                        result.word = json.optString("word")
                    }
                } else {
                    val error = json.optInt("error")
                    result.errorCode = error
                    result.desc = json.optString("desc")
                }
            } catch (e: JSONException) {
//            MyLogger.error(TAG, "Json parse error" + jsonStr);
                e.printStackTrace()
                return null
            }
            return result
        }
    }
}