package com.thoughtworks.assistant.abilities.asr.ali.models

import com.google.gson.annotations.SerializedName

data class Header(
    @SerializedName("namespace")
    var namespace: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("status")
    var status: Int = 0,
    @SerializedName("message_id")
    var messageId: String = "",
    @SerializedName("task_id")
    var taskId: String = "",
    @SerializedName("status_text")
    var statusText: String = "",
)