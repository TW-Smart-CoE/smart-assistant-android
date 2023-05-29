package com.thoughtworks.assistant.abilities.asr.ali

object AliAsrConstant {
    const val TAG = "AliAsr"

    // manifest 配置key
    const val META_DATA_ACCESS_KEY = "ALI_IVS_ACCESS_KEY"
    const val META_DATA_ACCESS_KEY_SECRET = "ALI_IVS_ACCESS_KEY_SECRET"
    const val META_DATA_APP_KEY = "ALI_IVS_APP_KEY"

    const val WAVE_FRAM_SIZE = 20 * 2 * 1 * 16000 / 1000 //20ms audio for 16k/16bit/mono
    const val SAMPLE_RATE = 16000

    // SDK 配置
    const val DEFAULT_URL = "wss://nls-gateway.cn-shanghai.aliyuncs.com:443/ws/v1"
}