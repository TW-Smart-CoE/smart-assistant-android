package com.thoughtworks.assistant.abilities.chat.chatgpt.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatGptRequest(
    val model: String,
    val messages: List<GptMessage>,
    val temperature: Float,
    @SerializedName("max_tokens") val maxTokens: Int,
    val stream: Boolean = false
) : Parcelable

@Parcelize
data class GptMessage(
    val role: String,
    val content: String
) : Parcelable
