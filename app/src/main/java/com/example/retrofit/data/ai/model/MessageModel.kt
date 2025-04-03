package com.example.retrofit.data.ai.model

import com.example.retrofit.presentation.model.MessageUI

enum class Role(val id: String) {
    USER("user"),
    MODEL("model"),
}

data class MessageModel(
    val text: String = "",
    val imageBase64String: String? = null,
    val user: Role,
)

fun MessageModel.toContent(): Content {
    val parts = mutableListOf(Part(text = this.text))
    if (this.imageBase64String != null) {
        parts.add(Part(inlineData = InlineData(data = this.imageBase64String)))
    }

    return Content(role = this.user.id, parts = parts)
}

fun MessageModel.toRequestBody(): RequestBody {
    return RequestBody(contents = listOf(this.toContent()))
}

fun MessageModel.toMessageUI():MessageUI{
    return MessageUI(text=this.text, user = this.user)
}