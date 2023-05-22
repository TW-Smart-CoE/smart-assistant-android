@file:Suppress("PackageNaming")

package com.thoughtworks.smartassistantapp

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.thoughtworks.assistant.SmartAssistant
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_main)

        val smartAssistant = SmartAssistant(this)
        val tts = smartAssistant.getTts()

        findViewById<Button>(R.id.btn_click).setOnClickListener {
            lifecycleScope.launch {
                tts.play("hello world")
            }

            lifecycleScope.launch {
                val file = tts.createAudioFile("hello world", "file.pcm")
                println(file)
            }
        }
    }
}