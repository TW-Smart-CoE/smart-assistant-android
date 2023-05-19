package com.thoughtworks.assistant.interfaces

import kotlinx.coroutines.flow.Flow

interface TtsCreator<Data : TtsData> {
    fun create(text: String): Flow<Data>

    fun release() {}
}