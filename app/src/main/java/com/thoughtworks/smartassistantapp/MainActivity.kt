@file:Suppress("PackageNaming")

package com.thoughtworks.smartassistantapp

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.thoughtworks.assistant.SmartAssistant
import com.thoughtworks.assistant.asr.Asr
import com.thoughtworks.assistant.asr.AsrType
import com.thoughtworks.assistant.tts.Tts
import com.thoughtworks.assistant.tts.TtsType
import com.thoughtworks.assistant.wakeup.WakeUp
import com.thoughtworks.assistant.wakeup.WakeUpListener
import com.thoughtworks.assistant.wakeup.WakeUpType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var tts: Tts
    lateinit var wakeUp: WakeUp
    lateinit var asr: Asr

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
        findViewById<Button>(R.id.btn_click).setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                tts.play("hello world")
                asr.stopListening()
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val file = tts.createAudioFile("hello world", "file.pcm")
                Log.d(TAG, file.absolutePath)
            }
        }

        findViewById<Button>(R.id.btn_start_wakeup).setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                wakeUp.setWakeUpListener(object : WakeUpListener {
                    override fun onSuccess(word: String) {
                        Log.i(TAG, "wakeUp success: $word")
                        lifecycleScope.launch(Dispatchers.IO) {
                            tts.play("我在")

                            delay(500)

                            val text = asr.startListening()
                            if (text.isEmpty()) {
                                Log.d(TAG, "asr result is empty")
                                tts.play("我什么也没听到")
                            } else {
                                Log.d(TAG, "asr result: $text")
                                tts.play(text)
                            }
                        }
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
            lifecycleScope.launch(Dispatchers.IO) {
                wakeUp.stop()
            }
        }
    }

    private fun initSmartAssistant() {
        val smartAssistant = SmartAssistant(this)
        tts = smartAssistant.getTts(TtsType.Ali)

        wakeUp = smartAssistant.getWakeUp(
            WakeUpType.Baidu,
            mapOf(Pair("kws-file", "assets:///WakeUp.bin"))
        )

        asr = smartAssistant.getAsr(
            AsrType.Ali,
            mapOf(
                Pair("enable_voice_detection", true),
                Pair("max_start_silence", 10000),
                Pair("max_end_silence", 800)
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
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val REQUEST_CODE = 1
    }
}