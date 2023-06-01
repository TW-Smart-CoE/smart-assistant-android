package com.thoughtworks.assistant.abilities.tts.ali

object AliTtsConstant {
    const val TAG = "SmartAssistant.AliTts"

    // manifest 配置key
    const val META_DATA_ACCESS_KEY = "ALI_IVS_ACCESS_KEY"
    const val META_DATA_ACCESS_KEY_SECRET = "ALI_IVS_ACCESS_KEY_SECRET"
    const val META_DATA_APP_KEY = "ALI_IVS_APP_KEY"

    // SDK 配置
    const val DEFAULT_URL = "wss://nls-gateway.cn-shanghai.aliyuncs.com:443/ws/v1"
    const val DEFAULT_MODE = 2 // 在线合成

    // 语音设置
    const val ENABLE_SUBTITLE = "1" // 字级别音素边界功能开关
    const val DEFAULT_FONT_NAME = "siqi" // 发音人
    const val DEFAULT_SAMPLE_RATE = 16000 // 音频采样率
    const val DEFAULT_ENCODE_TYPE = "pcm" // 音频编码格式

    // 保存路径
    const val DEFAULT_FILE_SAVE_DIR = "ali_tts"

    // SP KEY
//    const val SP_ACCESS_TOKEN_KEY = "key_access_token"
//    const val SP_EXPIRE_TIME_KEY = "key_expire_time"

    const val MILL_SECONDS = 1000
}