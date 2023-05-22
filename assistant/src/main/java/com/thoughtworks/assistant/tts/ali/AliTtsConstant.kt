package com.thoughtworks.assistant.tts.ali

object AliTtsConstant {
    const val TAG = "AliTTS"

    // manifest 配置key
    const val META_DATA_ACCESS_KEY = "ALI_TTS_ACCESS_KEY"
    const val META_DATA_ACCESS_KEY_SECRET = "ALI_TTS_ACCESS_KEY_SECRET"
    const val META_DATA_APP_KEY = "ALI_TTS_APP_KEY"

    // SDK 配置
    const val DEFAULT_URL = "wss://nls-gateway.cn-shanghai.aliyuncs.com:443/ws/v1"
    const val DEFAULT_MODE = 2 // 在线合成

    // 语音设置
    const val ENABLE_SUBTITLE = "1"
    const val DEFAULT_FONT_NAME = "siqi"
    const val DEFAULT_SAMPLE_RATE = 16000
    const val DEFAULT_ENCODE_TYPE = "pcm"

    // 保存路径
    const val DEFAULT_FILE_SAVE_DIR = "ali_tts"
}