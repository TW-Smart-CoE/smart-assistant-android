package com.thoughtworks.assistant

import android.content.Context
import com.thoughtworks.assistant.impl.ali.AliTtsCreator
import com.thoughtworks.assistant.impl.ali.AliTtsFileSaver
import com.thoughtworks.assistant.impl.ali.AliTtsPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import java.io.File

class AliTts(private val context: Context) : Tts {
    private val aliTtsCreator = AliTtsCreator()

    override suspend fun createAudioFile(text: String): File {
        val ttsFileSaver = AliTtsFileSaver(context)
        val file = ttsFileSaver.getFile()

        file.outputStream().use { out ->
            aliTtsCreator.create(text)
                .onEach { out.write(it.data) }
                .flowOn(Dispatchers.IO)
                .collect()
        }

        return file
    }

    override suspend fun play(text: String) {
        val ttsPlayer = AliTtsPlayer()
        aliTtsCreator.create(text)
            .onEach { ttsPlayer.writeData(it.data) }
            .flowOn(Dispatchers.IO)
            .onCompletion { ttsPlayer.release() }
            .collect()
    }

    override fun release() {
        aliTtsCreator.release()
    }
}