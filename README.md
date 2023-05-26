# smart-assistant

smart-assistant 封装了 Baidu，Alibaba 等厂商提供的 ASR，TTS，WakeUp 等智能语音交互服务 SDK。给开发者提供简单便捷的使用接口，无需关注复杂的 SDK 集成和适配。

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
dependencies {
    implementation "com.thoughtworks.smart-assistant:assistant:0.3.3"
}
```

## Tts 使用

### Ali Tts

#### 后台配置
- 开通[阿里云智能语音交互服](https://nls-portal.console.aliyun.com/overview)，创建项目，在[项目列表](https://nls-portal.console.aliyun.com/applist)中打开创建的 App，得到 APP_KEY，在这里配置项目功能。
- 在 [RAM 访问控制](https://ram.console.aliyun.com/overview)中点击 AccessKey，进入[访问凭证管理](https://ram.console.aliyun.com/manage/ak)页面。在这里创建 Access Key 后得到 ACCESS_KEY 和 ACCESS_KEY_SECRET。

#### SDK/API Key 配置

在环境变量里配置：
```shell
export ALI_IVS_ACCESS_KEY={Your Access Key}
export ALI_IVS_ACCESS_KEY_SECRET={Your Access Key SECRET}
export ALI_IVS_APP_KEY={Your APP Key}
```

#### 示例代码

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

## Asr 使用

### Ali Tts

#### 后台配置
和 Ali Tts 完全相同，共用一套配置。

#### 注意事项
- 动态请求 android.Manifest.permission.RECORD_AUDIO 权限。

#### SDK/API Key 配置
和 Ali Tts 完全相同，共用一套配置。
```shell
export ALI_IVS_ACCESS_KEY={Your Access Key}
export ALI_IVS_ACCESS_KEY_SECRET={Your Access Key SECRET}
export ALI_IVS_APP_KEY={Your APP Key}
```

#### 示例代码

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

## 语音唤醒

### Baidu WakeUp

#### 后台配置

- 请前往[百度AI唤醒](https://ai.baidu.com/tech/speech/wake)。 设置唤醒词并下载 WakeUp.bin 文件。 将 WakeUp.bin 文件放在 _project/app/src/main/assets_ 目录下。
- 请前往[百度AI控制台](https://console.bce.baidu.com/ai/?_=1684837854400#/ai/speech/app/list)创建一个应用程序。确保包名称与 applicationId 完全相同。创建应用程序后，您将获得 APP_ID, API_KEY 和 SECRET_KEY。

#### SDK/API Key 配置
```shell
export BAIDU_IVS_APP_ID={Your APP ID}
export BAIDU_IVS_API_KEY={Your API Key}
export BAIDU_IVS_SECRET_KEY={Your Secret Key}
```

#### 注意事项
- 动态请求 android.Manifest.permission.RECORD_AUDIO 权限。
- 确保 minSdk <= 22，否则您会收到以下错误: com.baidu.speech.recognizerdemo I/WakeupEventAdapter: wakeup name:wp.error; params:{"error":11,"desc":"Wakeup engine model file invalid","sub_error":11005}
- 如果需要在程序一启动就调用 wakeup start，经常会收到 errorCode: 3, errorMessage: Open Recorder failed 错误。需要在启动前设置 delay 时间（3~5秒），并检查这错误。如果出现该错误，可以再次 delay 后再启动。

#### 示例代码
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
