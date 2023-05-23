@file:Suppress("PackageNaming")

package com.thoughtworks.smartassistantapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.thoughtworks.assistant.SmartAssistant
import com.thoughtworks.assistant.tts.Tts
import com.thoughtworks.assistant.wakeup.WakeUp
import com.thoughtworks.assistant.wakeup.WakeUpListener
import com.thoughtworks.assistant.wakeup.WakeUpType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var tts: Tts
    lateinit var wakeUp: WakeUp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_main)

        val smartAssistant = SmartAssistant(this)
        tts = smartAssistant.getTts()
        wakeUp = smartAssistant.getWakeUp(
            WakeUpType.Baidu,
            mapOf(Pair("kws-file", "assets:///WakeUp.bin"))
        )

        findViewById<Button>(R.id.btn_click).setOnClickListener {
            lifecycleScope.launch {
                tts.play("hello world")
            }

            lifecycleScope.launch {
                val file = tts.createAudioFile("hello world", "file.pcm")
                Log.d(TAG, file.absolutePath)
            }
        }

        findViewById<Button>(R.id.btn_start_wakeup).setOnClickListener {
            lifecycleScope.launch {
                wakeUp.setWakeUpListener(object : WakeUpListener {
                    override fun onSuccess(word: String) {
                        Log.i(TAG, "wakeUp success: $word")
                    }

                    override fun onError(errorCode: Int, errorMessage: String) {
                        Log.e(TAG, "errorCode: $errorCode, errorMessage: $errorMessage")
                    }

                    override fun onStop() {
                        Log.i(TAG, "wakeUp stopped")
                    }
                })
                wakeUp.start()
            }
        }

        findViewById<Button>(R.id.btn_stop_wakeup).setOnClickListener {
            lifecycleScope.launch {
                wakeUp.stop()
            }
        }
    }

    override fun onDestroy() {
        cleanup()
        super.onDestroy()
    }

    private fun cleanup() {
        tts.release()
        wakeUp.release()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}