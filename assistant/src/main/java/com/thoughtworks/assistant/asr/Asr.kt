package com.thoughtworks.assistant.asr

interface Asr {
    suspend fun startListening(): String
    suspend fun stopListening()
    suspend fun release()
}