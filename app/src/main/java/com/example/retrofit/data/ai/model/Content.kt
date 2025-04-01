package com.example.retrofit.data.ai.model

import com.google.gson.annotations.SerializedName

data class Content(
    @SerializedName("role") val role: String,
    @SerializedName("parts") val parts: List<Part>,
)

fun Content.toMessageModel(): MessageModel {
    val user =
        when (this.role) {
            "user" -> Role.USER
            "model" -> Role.MODEL
            else -> Role.MODEL
        }

    return MessageModel(
        messageType = MessageModelType.Text(this.parts.firstOrNull()?.text ?: "..."),
        isUser = user,
    )
}

