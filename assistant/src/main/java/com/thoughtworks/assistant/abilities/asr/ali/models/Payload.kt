package com.thoughtworks.assistant.abilities.asr.ali.models

import com.google.gson.annotations.SerializedName

data class Payload(
    @SerializedName("result")
    var result: String = "",
    @SerializedName("duration")
    var duration: Int = 0,
)