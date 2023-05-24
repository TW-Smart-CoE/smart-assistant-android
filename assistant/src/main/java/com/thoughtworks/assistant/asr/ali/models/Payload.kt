package com.hkmw.roboassistant.ability.speech.ali.models

import com.google.gson.annotations.SerializedName

data class Payload(
    @SerializedName("result")
    var result: String? = null,
    @SerializedName("duration")
    var duration: Int? = null,
)