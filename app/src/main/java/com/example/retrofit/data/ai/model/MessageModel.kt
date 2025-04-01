package com.example.retrofit.data.ai.model

import com.example.retrofit.presentation.model.MessageUI
import com.example.retrofit.presentation.model.MessageUIType

enum class Role(val id: String) {
    USER("user"),
    MODEL("model"),
}

sealed class MessageModelType() {
    data class Image(val content: String, val base64ImageString: String) : MessageModelType()

    data class Text(val content: String) : MessageModelType()
}

data class MessageModel(val messageType: MessageModelType, val isUser: Role)

fun MessageModelType.toMessageUIType(): MessageUIType {
    return when (this) {
        is MessageModelType.Text -> MessageUIType.Text(this.content)
        is MessageModelType.Image -> MessageUIType.Image(this.content, this.base64ImageString)
    }
}

fun MessageModel.toMessageUI(): MessageUI {
    return MessageUI(
        messageUIType = this.messageType.toMessageUIType(),
        isUser = this.isUser == Role.USER,
    )
}

fun MessageModel.toContent(): Content {
    return when (this.messageType) {
        is MessageModelType.Text -> {
            Content(role = this.isUser.id, listOf(Part(text = this.messageType.content)))
        }
        is MessageModelType.Image -> {
            Content(
                role = this.isUser.id,
                listOf(
                    Part(
                        text = this.messageType.content,
                        inlineData = InlineData(data = this.messageType.base64ImageString),
                    )
                ),
            )
        }
    }
}

fun MessageModel.toRequestBody(): RequestBody {
    return when (this.messageType) {
        is MessageModelType.Text ->
            RequestBody(
                contents =
                    listOf(
                        Content(
                            parts = listOf(Part(text = this.messageType.content)),
                            role = this.isUser.id,
                        )
                    )
            )
        is MessageModelType.Image ->
            RequestBody(
                contents =
                    listOf(
                        Content(
                            parts =
                                listOf(
                                    Part(
                                        text = this.messageType.content,
                                        inlineData =
                                            InlineData(data = this.messageType.base64ImageString),
                                    )
                                ),
                            role = this.isUser.id,
                        )
                    )
            )
    }
}
