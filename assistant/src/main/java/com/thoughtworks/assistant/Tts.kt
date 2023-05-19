package com.thoughtworks.assistant

import java.io.File

interface Tts {
    suspend fun createAudioFile(text: String): File

    suspend fun play(text: String)

    fun release()
}