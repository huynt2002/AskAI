package com.example.retrofit.data.remote_database.model

import com.example.retrofit.data.ai.model.Role
import com.example.retrofit.presentation.model.MessageUI
import java.time.Instant

data class MessageResponse(
    val content: String,
    val imagePath: String,
    val createAt: Instant,
    val role: String,
)

data class MessageRequest(val content: String, val imagePath: String, val role: String)

fun MessageResponse.toMessageUI(): MessageUI {
    return MessageUI(
        text = content,
        imageUrl = if (imagePath.isBlank()) null else imagePath,
        user = if (role == "user") Role.USER else Role.MODEL,
    )
}
