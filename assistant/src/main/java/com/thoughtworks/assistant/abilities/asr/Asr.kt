package com.thoughtworks.assistant.abilities.asr

interface Asr {
    suspend fun startListening(): String
    suspend fun stopListening()
    fun release()
}