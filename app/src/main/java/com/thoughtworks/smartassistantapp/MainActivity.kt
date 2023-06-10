@file:Suppress("PackageNaming")

package com.thoughtworks.smartassistantapp

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.thoughtworks.assistant.SmartAssistant
import com.thoughtworks.assistant.abilities.asr.Asr
import com.thoughtworks.assistant.abilities.asr.AsrListener
import com.thoughtworks.assistant.abilities.asr.AsrType
import com.thoughtworks.assistant.abilities.chat.Chat
import com.thoughtworks.assistant.abilities.chat.ChatType
import com.thoughtworks.assistant.abilities.tts.Tts
import com.thoughtworks.assistant.abilities.tts.TtsType
import com.thoughtworks.assistant.abilities.wakeup.WakeUp
import com.thoughtworks.assistant.abilities.wakeup.WakeUpListener
import com.thoughtworks.assistant.abilities.wakeup.WakeUpType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var tts: Tts
    lateinit var wakeUp: WakeUp
    lateinit var asr: Asr
    lateinit var chat: Chat

    private val wakeUpListener = object : WakeUpListener {
        override fun onSuccess() {
            Log.i(TAG, "wakeUp success")
            lifecycleScope.launch(Dispatchers.IO) {
                tts.play("我在")

                delay(500)

                val text = asr.startListening()
                if (text.isEmpty()) {
                    Log.d(TAG, "asr result is empty")
                    tts.play("我什么也没听到")
                } else {
                    Log.d(TAG, "asr result: $text")
                    val response = chat.chat(text)
                    if (response.isEmpty()) {
                        tts.play("我不知道该怎么回答你")
                    } else {
                        tts.play(response)
                    }
                }
            }
        }

        override fun onError(errorCode: Int, errorMessage: String) {
            Log.e(TAG, "errorCode: $errorCode, errorMessage: $errorMessage")
            Toast.makeText(
                this@MainActivity,
                "errorCode: $errorCode, errorMessage: $errorMessage",
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onStop() {
            Log.i(TAG, "wakeUp stopped")
        }
    }

    private val permissions = arrayOf(
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private fun requestPermissions() {
        val permissionList = mutableListOf<String>()

        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionList.add(permission)
            }
        }

        if (permissionList.isEmpty()) {
            performAction()
        } else {
            ActivityCompat.requestPermissions(this, permissionList.toTypedArray(), REQUEST_CODE)
        }
    }

    private fun performAction() {
        // 执行相应操作
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE) {
            var allPermissionsGranted = true

            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false
                    break
                }
            }

            if (allPermissionsGranted) {
                // 用户授予了所有权限，执行相应操作
                performAction()
            } else {
                // 用户拒绝了某些权限，可以显示一个提示或采取其他适当的措施
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_main)
        initSmartAssistant()
        initUI()
        requestPermissions()
    }

    private fun initUI() {
        findViewById<Button>(R.id.btn_play_tts).setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
//                tts.play("hello world")
                tts.play("The early morning sun peaked through the curtains as I opened my eyes. A new day was beginning, full of promise and opportunity. What adventures would today hold? As I got out of bed and stretched, my mind wandered to the various tasks I had to accomplish. Errands to run, work to get done, chores to complete. But there were also more pleasurable things to look forward to, like having coffee with a friend, reading a new book, or getting outside for a walk. The day was a blank slate, filling up each moment little by little. While life can sometimes feel mundane or repetitive, each and every day we are given a fresh start - a chance to renew our sense of wonder and live fully in the present. A hundred tiny opportunities to find meaning, joy, and purpose. The story of this day was mine to create. I smiled and headed downstairs, eager to see how the chapter would unfold. The possibilities made me feel grateful, humble and alive, all at once. ")
                asr.stopListening()
            }

//            lifecycleScope.launch(Dispatchers.IO) {
//                val file = tts.createAudioFile("hello world", "file.pcm")
//                Log.d(TAG, file.absolutePath)
//            }
        }

        findViewById<Button>(R.id.btn_stop_tts).setOnClickListener {
            tts.stopPlay()
        }

        findViewById<Button>(R.id.btn_start_wakeup).setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                wakeUp.setWakeUpListener(wakeUpListener)
                wakeUp.start()
            }
        }

        findViewById<Button>(R.id.btn_stop_wakeup).setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                wakeUp.stop()
            }
        }

        findViewById<Button>(R.id.btn_start_asr).setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val text = asr.startListening()
                Log.d(TAG, "asr result: $text")
            }
        }

        findViewById<Button>(R.id.btn_stop_asr).setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                asr.stopListening()
            }
        }
    }

    private fun initSmartAssistant() {
        val smartAssistant = SmartAssistant(this)

        tts = smartAssistant.createTts(TtsType.Ali,
            mapOf(
                Pair("font_name", "siqi"),
                Pair("enable_subtitle", "1"),
                Pair("sample_rate", 16000),
                Pair("encode_type", "pcm"),
//                Pair("access_key", ""),
//                Pair("access_key_secret", ""),
//                Pair("app_key", "")
            )
        )

//        tts = smartAssistant.createTts(
//            TtsType.Google,
//            mapOf(
//                Pair(
//                    "credentials", ByteArrayInputStream(
//                        """
//                            // your google credentials in json format
//                        """.toByteArray()
//                    )
//                ),
//                Pair("language_code", "en-US"),
//                Pair("name", "en-US-Wavenet-F"),
//                Pair("speaking_rate", 1.0), // Speech speed. Default is 1.0. Range is 0.25 to 4.0.
//            )
//        )

        wakeUp = smartAssistant.createWakeUp(
            WakeUpType.Baidu,
            mapOf(
                Pair("kws-file", "assets:///WakeUp.bin"),
//                Pair("app_id", ""),
//                Pair("api_key", ""),
//                Pair("secret_key", "")
            )
        )
//        wakeUp = smartAssistant.createWakeUp(
//            WakeUpType.Picovoice,
//            mapOf(
//                Pair(
//                    "keyword_paths",
//                    listOf(
//                        "wakeup/picovoice/Hi-Joey_en_android_v2_2_0.ppn",
//                        "wakeup/picovoice/Hello-Joey_en_android_v2_2_0.ppn"
//                    ),
//                ),
////                Pair("access_key", "")
//            ),
//            wakeUpListener
//        )

        asr = smartAssistant.createAsr(
            AsrType.Ali,
            mapOf(
                Pair("enable_voice_detection", true),
                Pair("max_start_silence", 10000),
                Pair("max_end_silence", 800),
//                Pair("access_key", ""),
//                Pair("access_key_secret", ""),
//                Pair("app_key", ""),
            )
        )
        asr.setAsrListener(object : AsrListener {
            override fun onVolumeChanged(volume: Float) {
                Log.d(TAG, "onVolumeChanged: $volume")
            }
        })

        chat = smartAssistant.createChat(
            ChatType.ChatGpt,
            mapOf(
                Pair("base_url", "https://api.openai.com"),
                Pair("model", "gpt-3.5-turbo-0301"),
                Pair("temperature", 1.0f),
                Pair("max_history_len", 20),
//                Pair("api_key", ""),
                Pair(
                    "system_prompt", listOf(
                        "你是一个资深的游戏玩家，你所回答的问题，都必须限定在这个知识领域之内",
                        "如果被问你是谁，你要说自己是一名资深的游戏玩家",
                        "回答都要限定到50字以内"
                    )
                ),
            )
        )
    }

    override fun onDestroy() {
        cleanup()
        super.onDestroy()
    }

    private fun cleanup() {
        tts.release()
        wakeUp.release()
        asr.release()
        chat.release()
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val REQUEST_CODE = 1
    }
}