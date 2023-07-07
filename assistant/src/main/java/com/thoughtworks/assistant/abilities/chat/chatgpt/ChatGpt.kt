package com.thoughtworks.assistant.abilities.chat.chatgpt

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.thoughtworks.assistant.abilities.chat.Chat
import com.thoughtworks.assistant.abilities.chat.chatgpt.model.ChatGptRequest
import com.thoughtworks.assistant.abilities.chat.chatgpt.model.ChatGptResponse
import com.thoughtworks.assistant.abilities.chat.chatgpt.model.GptMessage
import com.thoughtworks.assistant.utils.Utils.getManifestMetaData
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.io.IOException
import java.util.concurrent.TimeUnit

class ChatGpt(
    private val context: Context,
    params: Map<String, Any> = emptyMap(),
) : Chat {
    interface ChatGptService {
        @POST("v1/chat/completions")
        suspend fun chat(@Body request: RequestBody): ChatGptResponse
    }

    private var baseUrl: String = (params["base_url"] ?: "https://api.openai.com/") as String
    private var maxHistoryLen: Int = (params["max_history_len"] ?: 50) as Int
    private var temperature: Float = (params["temperature"] ?: 1f) as Float
    private var model: String = (params["model"] ?: DEFAULT_MODEL) as String
    private var maxTokens: Int = (params["max_tokens"] ?: 2048) as Int
    private var readTimeout: Long = (params["read_timeout"] ?: 20_000L) as Long
    private var writeTimeout: Long = (params["write_timeout"] ?: 5_000L) as Long
    private var systemPromptList = mutableListOf<GptMessage>()
    private var conversionList = mutableListOf<GptMessage>()

    private val gson: Gson = GsonBuilder().create()

    private val logging = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BASIC)
    }

    private val okHttpClient = okhttp3.OkHttpClient.Builder()
        .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
        .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
        .addInterceptor { chain ->
            val builder = chain.request().newBuilder()
            builder.header(
                "Authorization",
                "Bearer ${params["api_key"]?.toString() ?: context.getManifestMetaData(META_OPENAI_API_KEY)}"
            )
            builder.header("content-type", "application/json")
            return@addInterceptor chain.proceed(builder.build())
        }
        .addInterceptor(logging)
        .addInterceptor { chain ->
            val response = chain.proceed(chain.request())

            if (!response.isSuccessful) {
                // Throw non-exception-typed error manually based on response code
                Log.e(TAG, "ERROR - code: ${response.code} with message: ${response.message}")
                when (response.code) {
                    INTERNAL_SERVER_ERROR -> throw InternalServerException(response.message)
                }
            }
            return@addInterceptor response
        }
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val chatGptService = retrofit.create(ChatGptService::class.java)

    init {
        params["system_prompt"]?.let {
            if (it is List<*>) {
                systemPromptList = it.map { prompt ->
                    GptMessage(ROLE_SYSTEM, prompt as String)
                }.toMutableList()
            } else {
                systemPromptList.add(GptMessage(ROLE_SYSTEM, it as String))
            }
        }
    }

    override suspend fun chat(content: String): String {
        val reqMessage = GptMessage(ROLE_USER, content)

        val messages = systemPromptList + conversionList + listOf(reqMessage)
        Log.d(TAG, "model: $model, temperature: $temperature, maxTokens: $maxTokens")
        Log.d(TAG, "messages: $messages")
        val chatRequest = ChatGptRequest(
            model = model,
            messages = messages,
            temperature = temperature,
            maxTokens = maxTokens,
        )
        val requestBody = gson.toJson(chatRequest).toRequestBody()

        val resMessage: GptMessage
        try {
            resMessage = chatGptService.chat(requestBody).choices[0].message
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            return ""
        }

        if (conversionList.size > maxHistoryLen) {
            conversionList.removeFirst()
            conversionList.removeFirst()
        }

        conversionList.add(reqMessage)
        conversionList.add(resMessage)

        return resMessage.content
    }

    override fun configure(params: Map<String, Any>) {
        params["base_url"]?.let {
            baseUrl = it as String
        }

        params["max_history_len"]?.let {
            maxHistoryLen = it as Int
        }

        params["temperature"]?.let {
            temperature = it as Float
        }

        params["model"]?.let {
            model = it as String
        }

        params["max_tokens"]?.let {
            maxTokens = it as Int
        }

        params["read_timeout"]?.let {
            readTimeout = it as Long
        }

        params["write_timeout"]?.let {
            writeTimeout = it as Long
        }

        params["system_prompt"]?.let {
            if (it is List<*>) {
                systemPromptList = it.map { prompt ->
                    GptMessage(ROLE_SYSTEM, prompt as String)
                }.toMutableList()
            } else {
                systemPromptList.clear()
                systemPromptList.add(GptMessage(ROLE_SYSTEM, it as String))
            }
        }
    }

    override fun clearConversationHistory() {
        conversionList.clear()
    }

    override fun release() {
        Log.d(TAG, "release")
    }

    internal class InternalServerException(message: String) : IOException(message)

    companion object {
        private const val TAG = "SA.ChatGpt"
        private const val ROLE_SYSTEM = "system"
        private const val ROLE_USER = "user"
        private const val ROLE_ASSISTANT = "assistant"
        private const val DEFAULT_MODEL = "gpt-3.5-turbo-0613"
        private const val META_OPENAI_API_KEY = "OPENAI_API_KEY"

        const val INTERNAL_SERVER_ERROR = 500
    }
}
