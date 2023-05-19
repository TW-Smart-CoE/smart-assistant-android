# smart-assistant

## TTS Usage

add gradle dependency:

```gradle
implementation("com.thoughtworks.smart-assistant:assistant:0.1.0")
```

### AliTTS Usage

1. Call `AliTtsInitializer.init()` in your Application's onCreate method:

```kotlin
// accessKey和accessKeySecret请参考阿里云文档进行获取：https://help.aliyun.com/document_detail/72138.htm?spm=a2c4g.72153.0.0.7aab596b5MUAHo
AliTtsInitializer.init(
    this,
    AliTtsConfig(
        accessKey = "",
        accessKeySecret = "",
        appKey = ""
    )
)
```

2. Create AliTts object and use:

```kotlin
val aliTts = AliTts(this)

// play a text directly:
lifecycleScope.launch {
    aliTts.play("text")
}

// convert text into audio and save as file:
lifecycleScope.launch {
    val file = aliTts.createAudioFile("hello world")
    println(file)
}
```