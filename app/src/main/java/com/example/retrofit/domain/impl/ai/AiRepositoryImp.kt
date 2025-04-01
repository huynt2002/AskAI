package com.example.retrofit.domain.impl.ai

import com.example.retrofit.BuildConfig
import com.example.retrofit.data.ai.AiAPI
import com.example.retrofit.data.ai.AiRepository
import com.example.retrofit.data.ai.model.MessageModel
import com.example.retrofit.data.ai.model.MessageModelType
import com.example.retrofit.data.ai.model.RequestBody
import com.example.retrofit.data.ai.model.ResponseBody
import com.example.retrofit.data.ai.model.Role
import com.example.retrofit.data.ai.model.toContent
import com.example.retrofit.data.ai.model.toMessageModel
import com.example.retrofit.data.ai.model.toRequestBody
import com.google.gson.Gson
import kotlinx.coroutines.CancellationException
import java.io.IOException
import javax.inject.Inject
import kotlin.text.removePrefix
import kotlin.text.startsWith
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

    override suspend fun getReply(messageModel: MessageModel): MessageModel {
        return try {
            api.generateContentOnSingleLine(requestBody = messageModel.toRequestBody()).toMessageModel()
        } catch (e: Exception) {
            if(e is CancellationException) throw e
            MessageModel(MessageModelType.Text("error: ${e.localizedMessage}"), Role.MODEL)
        }
    }

    override suspend fun getReply(listMessageModel: List<MessageModel>): MessageModel {
        val requestBody = RequestBody(listMessageModel.map { it.toContent() })
        return try {
            api.generateContentOnSingleLine(requestBody = requestBody).toMessageModel()
        } catch (e: Exception) {
            if(e is CancellationException) throw e
            MessageModel(MessageModelType.Text("error: ${e.localizedMessage}"), Role.MODEL)
        }
    }

    override fun getStreamReply(messageModel: MessageModel): Flow<MessageModel> = flow {
        try {
            val url =
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:streamGenerateContent?alt=sse&key=${BuildConfig.API_KEY}"

            val body = gson.toJson(messageModel.toRequestBody())

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
                        val messageModel = responseBody.toMessageModel() // Convert to MessageModel
                        emit(messageModel) // Emit the MessageModel object
                    }
                }
            }
        } catch (e: Exception) {
            if(e is CancellationException) throw e
            emit(MessageModel(MessageModelType.Text("Error: ${e.localizedMessage}"), Role.MODEL))
        }
    }

    override suspend fun getConversationTitle(content: String): String {
        val messageModel =
            MessageModel(
                MessageModelType.Text(
                    "naming the title of this conversation in one line: \"${content}\""
                ),
                Role.USER,
            )
        return try {
            val title =
                api.generateContentOnSingleLine(requestBody = messageModel.toRequestBody())
                    .toMessageModel()
                    .messageType as MessageModelType.Text
            title.content.trim()
        } catch (e: Exception) {
            if(e is CancellationException) throw e
            print(e.localizedMessage)
            "New Conversation"
        }
    }
}

class FakeRepository : AiRepository {
    override suspend fun getReply(messageModel: MessageModel): MessageModel {
        return MessageModel(MessageModelType.Text(""), Role.MODEL)
    }

    override suspend fun getReply(listMessageModel: List<MessageModel>): MessageModel {
        return MessageModel(MessageModelType.Text(""), Role.MODEL)
    }

    override fun getStreamReply(messageModel: MessageModel): Flow<MessageModel> = flow {
        emit(MessageModel(MessageModelType.Text("1.."), Role.MODEL))
    }

    override suspend fun getConversationTitle(content: String): String {
        return "Title"
    }
}
