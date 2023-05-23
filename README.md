# smart-assistant

## Usage

Add gradle dependency:

```gradle
// add meven config
maven {
    url = uri("http://10.205.215.4:8081/repository/maven-releases/")
    isAllowInsecureProtocol = true
    credentials {
        username = "admin"
        password = "IoT1234"
    }
}

// add dependency
implementation("com.thoughtworks.smart-assistant:assistant:0.1.1")
```

### Tts

```kotlin
val smartAssistant = SmartAssistant(this)
val tts = smartAssistant.getTts()

// play a text directly:
lifecycleScope.launch {
    tts.play("text")
}

// convert text into audio and save as file:
lifecycleScope.launch {
    val file = tts.createAudioFile("hello world", "saveFile.pcm")
    println(file)
}
```

### WakeUp

Please visit the following webpage: [Baidu AI WakeUp](https://ai.baidu.com/tech/speech/wake). Set the wake-up word and download the WakeUp.bin file.)
Place the WakeUp.bin file in the project/app/src/main/assets directory.

Dynamic request android.Manifest.permission.RECORD_AUDIO permission.

Please go to [Baidu AI Console](https://console.bce.baidu.com/ai/?_=1684837854400#/ai/speech/app/list) to create an application. Make sure the package name is exactly the same as the applicationId. After creating the application, you will obtain the AppID, API Key, and Secret Key.

Please add the following `meta-data` configuration to the `<application>` tag in the AndroidManifest.xml file:

Make sure minSdk <= 22, other wise you will get the following error:

```
com.baidu.speech.recognizerdemo I/WakeupEventAdapter: wakeup name:wp.error; params:{"error":11,"desc":"Wakeup engine model file invalid","sub_error":11005}
```
[Wakeup engine model file](https://ai.baidu.com/forum/topic/show/497055)

```xml
<meta-data
    android:name="com.baidu.speech.APP_ID"
    android:value="YOUR_APP_ID" />

<meta-data
    android:name="com.baidu.speech.API_KEY"
    android:value="YOUR_API_KEY" />

<meta-data
    android:name="com.baidu.speech.SECRET_KEY"
    android:value="YOUR_SECRET_KEY" />
```

Replace "YOUR_APP_ID", "YOUR_API_KEY", and "YOUR_SECRET_KEY" with the corresponding values you obtained from the Baidu AI Console.

```kotlin
findViewById<Button>(R.id.btn_start_wakeup).setOnClickListener {
    lifecycleScope.launch {
        // set callback listener
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

