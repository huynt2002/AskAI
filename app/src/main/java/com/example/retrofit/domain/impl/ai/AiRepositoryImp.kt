package com.example.retrofit.domain.impl.ai

import com.example.retrofit.BuildConfig
import com.example.retrofit.data.ai.AiAPI
import com.example.retrofit.data.ai.AiRepository
import com.example.retrofit.data.ai.model.Message
import com.example.retrofit.data.ai.model.RequestBody
import com.example.retrofit.data.ai.model.ResponseBody
import com.example.retrofit.data.ai.model.Role
import com.example.retrofit.data.ai.model.toContent
import com.example.retrofit.data.ai.model.toMessage
import com.example.retrofit.data.ai.model.toRequestBody
import com.google.gson.Gson
import java.io.IOException
import javax.inject.Inject
import kotlin.text.removePrefix
import kotlin.text.startsWith
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSource

class AiRepositoryImp
@Inject
constructor(private val api: AiAPI, private val okHttpClient: OkHttpClient) : AiRepository {
    private val gson = Gson()

    override suspend fun getReply(message: Message): Message {
        return try {
            api.generateContentOnSingleLine(requestBody = message.toRequestBody()).toMessage()
        } catch (e: Exception) {
            Message("error: ${e.localizedMessage}", Role.MODEL)
        }
    }

    override suspend fun getReply(listMessage: List<Message>): Message {
        val requestBody = RequestBody(listMessage.map { it.toContent() })
        return try {
            api.generateContentOnSingleLine(requestBody = requestBody).toMessage()
        } catch (e: Exception) {
            Message("error: ${e.localizedMessage}", Role.MODEL)
        }
    }

    override fun getStreamReply(message: Message): Flow<Message> = flow {
        try {
            val url =
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:streamGenerateContent?alt=sse&key=${BuildConfig.API_KEY}"

            val body = gson.toJson(message.toRequestBody())

            val request =
                Request.Builder()
                    .url(url)
                    .addHeader("Content-Type", "application/json")
                    .post(body.toRequestBody("application/json".toMediaType()))
                    .build()

            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("$response")
                }

                val source: BufferedSource = response.body?.source() ?: return@use

                while (!source.exhausted()) {
                    val line = source.readUtf8Line()?.trim() ?: continue

                    if (line.startsWith("data:")) {
                        val jsonString = line.removePrefix("data: ").trim()
                        val responseBody = gson.fromJson(jsonString, ResponseBody::class.java)
                        val message = responseBody.toMessage() // Convert to Message
                        emit(message) // Emit the Message object
                    }
                }
            }
        } catch (e: Exception) {
            emit(Message("Error: ${e.localizedMessage}", Role.MODEL))
        }
    }

    override suspend fun getConversationTitle(content: String): String {
        val message =
            Message("naming the title of this conversation in one line: \"${content}\"", Role.USER)
        return try {
            api.generateContentOnSingleLine(requestBody = message.toRequestBody())
                .toMessage()
                .content
                .trim()
        } catch (e: Exception) {
            print(e.localizedMessage)
            "New Conversation"
        }
    }
}

class FakeRepository : AiRepository {
    override suspend fun getReply(message: Message): Message {
        return Message("", Role.MODEL)
    }

    override suspend fun getReply(listMessage: List<Message>): Message {
        return Message("", Role.MODEL)
    }

    override fun getStreamReply(message: Message): Flow<Message> = flow {
        emit(Message("1..", Role.MODEL))
        delay(1000)
        emit(Message("..2..", Role.MODEL))
        delay(1000)
        emit(Message("..3", Role.MODEL))
    }

    override suspend fun getConversationTitle(content: String): String {
        return "Title"
    }
}
