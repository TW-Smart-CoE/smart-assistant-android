package com.thoughtworks.assistant.impl.ali

import android.content.Context
import com.thoughtworks.assistant.impl.ali.AliTtsConstant.DEFAULT_FILE_SAVE_DIR
import java.io.File
import java.util.*

class AliTtsFileSaver(context: Context, fileName: String = "") {
    private var saveFile = File("")
    private val encodeType = AliTtsInitializer.params.encodeType

    init {
        val savePath = File(context.cacheDir, DEFAULT_FILE_SAVE_DIR)
        if (!savePath.exists()) {
            savePath.mkdirs()
        }
        val realFileName = fileName.ifEmpty { UUID.randomUUID().toString() }

        saveFile = File(savePath, "$realFileName.${encodeType}")
    }

    fun getFile(): File {
        return saveFile
    }
}