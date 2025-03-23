package com.example.retrofit.data.ai.model

import com.example.retrofit.presentation.model.MessageUI
import com.google.gson.annotations.SerializedName

data class RequestBody(val contents: List<Content>)

data class Content(
    @SerializedName("role") val role: String,
    @SerializedName("parts") val parts: List<Part>,
)

data class Part(@SerializedName("text") val text: String)

fun Content.toMessage(): Message {
    return Message(
        content = this.parts.firstOrNull()?.text ?: "...",
        isUser =
            when (this.role) {
                "user" -> Role.USER
                "model" -> Role.MODEL
                else -> Role.MODEL
            },
    )
}

data class ResponseBody(@SerializedName("candidates") val candidates: List<Candidate>)

fun ResponseBody.toMessage(): Message {
    return this.candidates.firstOrNull()?.content?.toMessage() ?: Message("error!", Role.MODEL)
}

data class Candidate(@SerializedName("content") val content: Content)

enum class Role(val id: String) {
    USER("user"),
    MODEL("model"),
}

data class Message(val content: String, val isUser: Role)

fun Message.toMessageUI(): MessageUI {
    return MessageUI(content = this.content.trim(), isUser = this.isUser == Role.USER)
}

fun Message.toContent(): Content {
    return Content(role = this.isUser.id, listOf(Part(text = this.content)))
}

fun Message.toRequestBody(): RequestBody {
    return RequestBody(
        contents = listOf(Content(parts = listOf(Part(text = this.content)), role = this.isUser.id))
    )
}
