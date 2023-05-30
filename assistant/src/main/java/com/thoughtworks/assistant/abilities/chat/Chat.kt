package com.thoughtworks.assistant.abilities.chat

interface Chat {
    suspend fun chat(content: String): String
    fun configure(params: Map<String, Any> = emptyMap())
    fun clearConversationHistory()
    fun release()
}