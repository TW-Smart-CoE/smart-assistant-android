package com.thoughtworks.assistant.abilities.chat

interface Chat {
    suspend fun chat(content: String): String
    fun clearConversationHistory()
    fun release()
}