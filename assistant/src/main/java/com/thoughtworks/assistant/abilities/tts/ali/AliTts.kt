package com.thoughtworks.assistant.abilities.tts.ali

import android.content.Context
import com.thoughtworks.assistant.abilities.tts.Tts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import java.io.File

class AliTts(private val context: Context, params: Map<String, Any>) : Tts {
    init {
        AliTtsInitializer.init(context)
    }

    private val aliTtsCreator = AliTtsCreator(params)

    override suspend fun createAudioFile(text: String, fileName: String): File {
        val savePath = File(context.cacheDir, AliTtsConstant.DEFAULT_FILE_SAVE_DIR)
        if (!savePath.exists()) {
            savePath.mkdirs()
        }
        val saveFile = File(savePath, fileName)
        saveFile.outputStream().use { out ->
            aliTtsCreator.create(text)
                .onEach { out.write(it.data) }
                .flowOn(Dispatchers.IO)
                .collect()
        }

        return saveFile
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