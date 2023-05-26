# smart-assistant

## 依赖配置

### kotlin
```build.gradle.kts
repositories {
    // ...
    maven {
        url = uri("http://10.205.215.4:8081/repository/maven-releases/")
        isAllowInsecureProtocol = true
        credentials {
            username = "admin"
            password = "IoT1234"
        }
    }
}

// add dependency
dependencies {
    implementation("com.thoughtworks.smart-assistant:assistant:0.3.3")
}
```

### groovy
```build.gradle
repositories {
    // ...
    maven {
        url "http://10.205.215.4:8081/repository/maven-releases/"
        allowInsecureProtocol(true)
        credentials {
            username = "admin"
            password = "IoT1234"
        }
    }
}

// add dependency
```
dependencies {
    //...
    implementation "com.thoughtworks.smart-assistant:assistant:0.3.3"
}

## 使用

### Tts

```kotlin
val smartAssistant = SmartAssistant(this)
val tts = smartAssistant.getTts(TtsType.Ali)

// play a text directly:
lifecycleScope.launch {
    tts.play("text")
}

// convert text into audio and save as file:
lifecycleScope.launch {
    val file = tts.createAudioFile("hello world", "saveFile.pcm")
    Log.d(TAG, file)
}
```

### Asr

```kotlin
val smartAssistant = SmartAssistant(this)
val asr = smartAssistant.getAsr(
    AsrType.Ali,
    mapOf(
        Pair("enable_voice_detection", true),
        Pair("max_start_silence", 10000),
        Pair("max_end_silence", 800)
    )
)

// start asr
lifecycleScope.launch {
    val text = asr.startListening()
    if (text.isEmpty()) {
        Log.d(TAG, "asr result is empty")
        tts.play("我没有听清")
    } else {
        Log.d(TAG, "asr result: $text")
        tts.play(text)
    }
}

// stop asr
lifecycleScope.launch {
    asr.stopListening()
}
```

### 唤醒
- 请访问以下网页:[百度AI唤醒](https://ai.baidu.com/tech/speech/wake)。 设置唤醒词并下载WakeUp.bin文件。 将 WakeUp.bin 文件放在 project/app/src/main/assets 目录下。
- 动态请求 android.Manifest.permission.RECORD_AUDIO 权限。
- 请前往 [百度AI控制台](https://console.bce.baidu.com/ai/?_=1684837854400#/ai/speech/app/list) 创建一个应用程序。 确保包名称与 applicationId 完全相同。 创建应用程序后，您将获得 AppID, API 密钥和 Secret 键。
- 确保minSdk <= 22,否则您会收到以下错误:
- com.baidu.speech.recognizerdemo I/WakeupEventAdapter: wakeup name:wp.error; params:{"error":11,"desc":"Wakeup engine model file invalid","sub_error":11005}

```kotlin
val smartAssistant = SmartAssistant(this)

wakeUp = smartAssistant.getWakeUp(
    WakeUpType.Baidu,
    mapOf(Pair("kws-file", "assets:///WakeUp.bin"))
)

findViewById<Button>(R.id.btn_start_wakeup).setOnClickListener {
    lifecycleScope.launch {
        // set callback listener
        wakeUp.setWakeUpListener(object : WakeUpListener {
            override fun onSuccess(word: String) {
                Log.d(TAG, "wakeUp success: $word")
            }

            override fun onError(errorCode: Int, errorMessage: String) {
                Log.e(TAG, "errorCode: $errorCode, errorMessage: $errorMessage")
            }

            override fun onStop() {
                Log.d(TAG, "wakeUp stopped")
            }
        })
        
        // start wakeup
        wakeUp.start()
    }
}

findViewById<Button>(R.id.btn_stop_wakeup).setOnClickListener {
    lifecycleScope.launch {
        // stop wakeup
        wakeUp.stop()
    }
}
```
