package com.thoughtworks.assistant.abilities.asr

interface AsrListener {
    fun onVolumeChanged(volume: Float)
}

interface Asr {
    fun setAsrListener(listener: AsrListener?)
    suspend fun startListening(): String
    suspend fun stopListening()
    fun release()
}