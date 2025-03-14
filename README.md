# smart-assistant

This repo is deprecated, please move to [voice-assistant-android](https://github.com/TW-Smart-CoE/voice-assistant-android)

smart-assistant 封装了各云服务厂商提供的 ASR，TTS，WakeUp，ChatGpt 等智能语音交互服务 SDK。给开发者提供简单便捷的使用接口，无需关注复杂的 SDK 集成和适配。

目前支持的能力：
- ASR 语音识别：阿里，百度
- TTS 语音转文字：阿里（中文），Google（海外）
- WakeUp 语音唤醒：百度（中文），Picovoice（海外）
- Chat 智能聊天：ChatGpt

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
    implementation("com.thoughtworks.smart-assistant:assistant:0.7.4")
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
    implementation "com.thoughtworks.smart-assistant:assistant:0.7.4"
}
```

## Tts 语音转文字

### Ali Tts

#### 后台配置
- 开通[阿里云智能语音交互服务](https://nls-portal.console.aliyun.com/overview)，创建项目，在[项目列表](https://nls-portal.console.aliyun.com/applist)中打开创建的 App，得到 APP_KEY，在这里配置项目功能。
- 在 [RAM 访问控制](https://ram.console.aliyun.com/overview)中点击 AccessKey，进入[访问凭证管理](https://ram.console.aliyun.com/manage/ak)页面。在这里创建 Access Key 后得到 ACCESS_KEY 和 ACCESS_KEY_SECRET。

#### SDK/API Key 配置

AndroidManifest.xml 中 application 标签下配置（也可以在代码中配置）：
```xml
<meta-data
    android:name="ALI_IVS_ACCESS_KEY"
    android:value="${ALI_IVS_ACCESS_KEY}" />
<meta-data
    android:name="ALI_IVS_ACCESS_KEY_SECRET"
    android:value="${ALI_IVS_ACCESS_KEY_SECRET}" />
<meta-data
    android:name="ALI_IVS_APP_KEY"
    android:value="${ALI_IVS_APP_KEY}" />
```

#### 示例代码

```kotlin
val smartAssistant = SmartAssistant(this)
val tts = smartAssistant.createTts(TtsType.Ali,
    mapOf(
        Pair("font_name", "siqi"),  // 发音人，默认配置为 siqi，可以不用配置。如需配置不同人声，请参考官网帮助文档中的接口说明。
        Pair("enable_subtitle", "1"),  // 字级别音素边界功能开关，默认配置为 1，可以不用配置。
        Pair("sample_rate", 16000),  // 音频采样率，默认配置为 16000，可以不用配置。
        Pair("encode_type", "pcm"),  // 音频编码格式，默认配置为 pcm，可以不用配置。
        // Pair("access_key", ""),  // 优先使用这里的 access_key。如果没有，使用 AndroidManifest.xml 中的 ALI_IVS_ACCESS_KEY。
        // Pair("access_key_secret", ""),  // 优先使用这里的  access_key_secret。如果没有，使用 AndroidManifest.xml 中的 ALI_IVS_ACCESS_KEY_SECRET。
        // Pair("app_key", ""),  // 优先使用这里的 app_key，如果没有。使用 AndroidManifest.xml 中的 ALI_IVS_APP_KEY。
    )
)

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

### Google Speech Tts

#### 后台配置

构建 Google Speech TTS 需要 GoogleCredentials，而 GoogleCredentials 不能直接由 Google Cloud的API Key 创建。
GoogleCredentials 需要一个 service account key。下面是 service account key 的创建流程。

Create a service account key:
- Go to the [Google Cloud Console](https://console.cloud.google.com/)
- Select your project.
- Go to IAM & Admin > Service Accounts.
- Click on CREATE SERVICE ACCOUNT.
- Give your service account a name and click CREATE.
- Under Service account permissions (optional), add roles that you need for your project. For example, if you're using Text-to-Speech, you might add the role roles/cloudtexttospeech.editor.
- Click CONTINUE and then DONE.
- Click on the service account that you just created, then on the Keys tab, and then on ADD KEY > Create new key.
- Select JSON and click CREATE. The JSON key will be downloaded.

将下载到 json 文件中的内容用作创建 Google TTS 时的 credentials。

#### 注意事项

如果使用 Google Tts，需要在 app 模块的 build.gradle 文件中添加以下配置：

```kotlin
android {
    //...
    packagingOptions {
        pickFirst("META-INF/io.netty.versions.properties")
        pickFirst("META-INF/DEPENDENCIES")
        pickFirst("META-INF/INDEX.LIST")
    }
}
```

#### SDK/API Key 配置

创建 TTS 时配置 credentials

#### 示例代码

```kotlin
 tts = smartAssistant.createTts(
        TtsType.Google,
        mapOf(
            Pair(
                "credentials", ByteArrayInputStream(
                    """
                        // your google credentials in json format
                    """.toByteArray()
                )
            ),
            Pair("language_code", "en-US"),
            Pair("name", "en-US-Wavenet-F"),
            Pair("speaking_rate", 1.0), // Speech speed. Default is 1.0. Range is 0.25 to 4.0.
            Pair("volume_gain_db", 0.0), //Volume gain (in dB) of the normal native volume, supported by the specific voice, in the range [-96.0, 16.0]
        )
    )

// play a text directly:
lifecycleScope.launch {
    tts.play("text")
}

// convert text into audio and save as file:
lifecycleScope.launch {
    val file = tts.createAudioFile("hello world", "saveFile.mp3")
    Log.d(TAG, file)
}
```

## Asr 语音识别

### Ali Asr

#### 后台配置
和 Ali Tts 完全相同，共用一套配置。

#### 注意事项
- 动态请求 android.Manifest.permission.RECORD_AUDIO 权限。

#### SDK/API Key 配置
和 Ali Tts 完全相同，共用一套配置。
AndroidManifest.xml 中 application 标签下配置（也可以在代码中配置）：

```xml
<meta-data
    android:name="ALI_IVS_ACCESS_KEY"
    android:value="${ALI_IVS_ACCESS_KEY}" />
<meta-data
    android:name="ALI_IVS_ACCESS_KEY_SECRET"
    android:value="${ALI_IVS_ACCESS_KEY_SECRET}" />
<meta-data
    android:name="ALI_IVS_APP_KEY"
    android:value="${ALI_IVS_APP_KEY}" />
```

#### 示例代码

```kotlin
val smartAssistant = SmartAssistant(this)
val asr = smartAssistant.createAsr(
    AsrType.Ali,
    mapOf(
        Pair("enable_voice_detection", true),
        Pair("max_start_silence", 10000),
        Pair("max_end_silence", 800),
        // Pair("access_key", ""),  // 优先使用这里的 access_key。如果没有，使用 AndroidManifest.xml 中的 ALI_IVS_ACCESS_KEY。
        // Pair("access_key_secret", ""),  // 优先使用这里的  access_key_secret。如果没有，使用 AndroidManifest.xml 中的 ALI_IVS_ACCESS_KEY_SECRET。
        // Pair("app_key", ""),  // 优先使用这里的 app_key。如果没有，使用 AndroidManifest.xml 中的 ALI_IVS_APP_KEY。
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
### Baidu Asr

#### 后台配置 
和 Baidu Wakeup 完全相同，共用一套配置。

#### 注意事项
- 动态请求 android.Manifest.permission.RECORD_AUDIO 权限。

#### SDK/API Key 配置
百度语音识别库同时支持WakeUp和Asr，配置和百度WakeUp 完全相同，共用一套配置。

```xml
<meta-data
    android:name="BAIDU_IVS_APP_ID"
    android:value="\${BAIDU_IVS_APP_ID}" />
<meta-data
android:name="BAIDU_IVS_API_KEY"
android:value="${BAIDU_IVS_API_KEY}" />
<meta-data
android:name="BAIDU_IVS_SECRET_KEY"
android:value="${BAIDU_IVS_SECRET_KEY}" />
```
这里注意，因为 BAIDU_IVS_APP_ID 是个 Int 类型的数字，所以需要在前面加一个 "\\" 符号，否则在代码中取出 value 时会因数据类型不对而报错。

#### 示例代码

```kotlin
val smartAssistant = SmartAssistant(this)
val asr = smartAssistant.createAsr(
    AsrType.Baidu,
    mapOf(
        // Pair("app_id", ""),  // 优先使用这里的 app_id。如果没有，使用 AndroidManifest.xml 中的 BAIDU_IVS_APP_ID。
        // Pair("api_key", ""),  // 优先使用这里的 api_key。如果没有，使用 AndroidManifest.xml 中的 BAIDU_IVS_API_KEY。
        // Pair("secret_key", ""),  // 优先使用这里的 secret_key。如果没有，使用 AndroidManifest.xml 中的 BAIDU_IVS_SECRET_KEY。
    )
)

// start asr
lifecycleScope.launch {
    // text 为百度Asr识别出来的最终/最佳文本
    val text = asr.startListening()
}

// stop asr
lifecycleScope.launch {
    asr.stopListening()
}
```
## WakeUp 语音唤醒

### Baidu WakeUp

#### 后台配置

- 请前往[百度AI唤醒](https://ai.baidu.com/tech/speech/wake)。 设置唤醒词并下载 WakeUp.bin 文件。 将 WakeUp.bin 文件放在 _project/app/src/main/assets_ 目录下（只能放到 assets 根目录下，不能创建子目录，否则 SDK 会找不到这个文件）。
- 请前往[百度AI控制台](https://console.bce.baidu.com/ai/?_=1684837854400#/ai/speech/app/list)创建一个应用程序。确保包名称与 applicationId 完全相同。创建应用程序后，您将获得 APP_ID, API_KEY 和 SECRET_KEY。

#### SDK/API Key 配置
AndroidManifest.xml 中 application 标签下配置（也可以在代码中配置）：
```xml
<meta-data
    android:name="BAIDU_IVS_APP_ID"
    android:value="\${BAIDU_IVS_APP_ID}" />
<meta-data
    android:name="BAIDU_IVS_API_KEY"
    android:value="${BAIDU_IVS_API_KEY}" />
<meta-data
    android:name="BAIDU_IVS_SECRET_KEY"
    android:value="${BAIDU_IVS_SECRET_KEY}" />
```
这里注意，因为 BAIDU_IVS_APP_ID 是个 Int 类型的数字，所以需要在前面加一个 "\\" 符号，否则在代码中取出 value 时会因数据类型不对而报错。

#### 注意事项
- 动态请求 android.Manifest.permission.RECORD_AUDIO 权限。
- 确保 minSdk <= 22，否则您会收到以下错误: com.baidu.speech.recognizerdemo I/WakeupEventAdapter: wakeup name:wp.error; params:{"error":11,"desc":"Wakeup engine model file invalid","sub_error":11005}
- 如果需要在程序一启动就调用 wakeup start，经常会收到 errorCode: 3, errorMessage: Open Recorder failed 错误。需要在启动前设置 delay 时间（3~5秒），并检查这错误。如果出现该错误，可以再次 delay 后再启动。

#### 示例代码
```kotlin
val smartAssistant = SmartAssistant(this)
val wakeUp = smartAssistant.createWakeUp(
    WakeUpType.Baidu,
    mapOf(
        Pair("kws-file", "assets:///WakeUp.bin"),
        // Pair("app_id", ""),  // 优先使用这里的 app_id。如果没有，使用 AndroidManifest.xml 中的 BAIDU_IVS_APP_ID。
        // Pair("api_key", ""),  // 优先使用这里的 api_key。如果没有，使用 AndroidManifest.xml 中的 BAIDU_IVS_API_KEY。
        // Pair("secret_key", ""),  // 优先使用这里的 secret_key。如果没有，使用 AndroidManifest.xml 中的 BAIDU_IVS_SECRET_KEY。
    )
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

### Picovoice WakeUp

#### 后台配置

- 请前往[picovoice 控制台](https://console.picovoice.ai/)注册登录并拿到 AccessKey。
- 请前往[picovoice 唤醒词](https://console.picovoice.ai/ppn)。设置唤醒词并下载 ppn 文件。 将 ppn 文件放在 _project/app/src/main/assets_ 目录下，可以存放到子目录下。如果需要多个唤醒词，可下载多个文件。

#### SDK/API Key 配置
AndroidManifest.xml 中 application 标签下配置（也可以在代码中配置）：
```xml
<meta-data
    android:name="PICOVOICE_ACCESS_KEY"
    android:value="${PICOVOICE_ACCESS_KEY}" />
```

#### 示例代码
```kotlin
val smartAssistant = SmartAssistant(this)
wakeUp = smartAssistant.createWakeUp(
    WakeUpType.Picovoice,
    mapOf(
        Pair(
            "keyword_paths", listOf(
                "wakeup/picovoice/Hi-Joey_en_android_v2_2_0.ppn", // 注意这里的文件路径不要加 assets:/// 前缀
                "wakeup/picovoice/Hello-Joey_en_android_v2_2_0.ppn"
            )
        ),
        // Pair("access_key", ""),  // 优先使用这里的 access_key。如果没有，使用 AndroidManifest.xml 中的 PICOVOICE_ACCESS_KEY。
    ),
    wakeUpListener
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

## Chat 智能聊天

### OpenAI ChatGpt

#### 后台配置

- 请前往[OpenAI API keys](https://platform.openai.com/account/api-keys)。 创建一个 API Key。

#### SDK/API Key 配置
AndroidManifest.xml 中 application 标签下配置（也可以在代码中配置）：
```xml
<meta-data
    android:name="OPENAI_API_KEY"
    android:value="${OPENAI_API_KEY}" />
```

#### 示例代码
```kotlin
val smartAssistant = SmartAssistant(this)

val chat = smartAssistant.createChat(
    ChatType.ChatGpt,
    mapOf(
        Pair("base_url", "https://api.openai.com"),  // OpenAI API 地址。默认值为 https://api.openai.com。
        Pair("model", "gpt-3.5-turbo-0613"),  // 模型名称。默认为 gpt-3.5-turbo-0613。
        Pair("temperature", 1.0f),  // 生成文本的多样性。值越大，生成的文本越多样化。默认为 1.0f。
        Pair("max_history_len", 50), // 最大聊天历史记录长度。默认为 50。
        // Pair("api_key", ""),  // 优先使用这里的 api_key。如果没有，使用 AndroidManifest.xml 中的 OPENAI_API_KEY。
        Pair(
            "system_prompt", listOf(
                "你是一个资深的游戏玩家，你所回答的问题，都必须限定在这个知识领域之内",
                "如果被问你是谁，你要说自己是一名资深的游戏玩家",
                "回答都要限定到50字以内"
            )
        ),
    )
)

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
```
