# smart-assistant

## Usage

### Tts

1. add gradle dependency:

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
implementation("com.thoughtworks.smart-assistant:assistant:0.1.0")
```

2. create tts object:

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