package com.thoughtworks.assistant.impl.ali

import com.thoughtworks.assistant.interfaces.TtsData

class AliTtsData(
    val info: String,
    val infoLen: Int,
    val data: ByteArray
) : TtsData