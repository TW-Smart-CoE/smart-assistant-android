package com.thoughtworks.assistant.abilities.tts

import java.io.File

interface Tts {
    suspend fun createAudioFile(text: String, fileName: String): File

    suspend fun play(text: String)

    fun configure(params: Map<String, Any>)

    fun stopPlay()

    fun release()
}