package com.hkmw.roboassistant.ability.speech.ali.models

import com.google.gson.annotations.SerializedName

data class Header(
    @SerializedName("namespace")
    var namespace: String? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("status")
    var status: Int? = null,
    @SerializedName("message_id")
    var messageId: String? = null,
    @SerializedName("task_id")
    var taskId: String? = null,
    @SerializedName("status_text")
    var statusText: String? = null,
)