package com.example.retrofit.presentation.model

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import com.example.retrofit.data.ai.model.MessageModel
import com.example.retrofit.data.ai.model.MessageModelType
import com.example.retrofit.data.ai.model.Role
import java.util.UUID
import me.huynt204567.android_ui_kit.MessageType

sealed class MessageUIType {
    data class Text(val content: String) : MessageUIType()

    data class Image(val content: String, val base64ImageString: String) : MessageUIType()
}

fun MessageUIType.toMessageType(): MessageModelType {
    return when (this) {
        is MessageUIType.Text -> MessageModelType.Text(this.content)
        is MessageUIType.Image -> MessageModelType.Image(this.content, this.base64ImageString)
    }
}

@Composable
fun MessageUIType.toMessageViewType(): MessageType {
    return when (this) {
        is MessageUIType.Text -> MessageType.Text(this.content)
        is MessageUIType.Image -> {
            val decodedBytes =
                Base64.decode(this.base64ImageString, Base64.DEFAULT) // Decode Base64 to bytes
            val bitmap =
                BitmapFactory.decodeByteArray(
                    decodedBytes,
                    0,
                    decodedBytes.size,
                ) // Convert to Bitmap
            val painter = BitmapPainter(bitmap.asImageBitmap())
            MessageType.Image(this.content, painter)
        }
    }
}

@Immutable
data class MessageUI(
    val id: String = UUID.randomUUID().toString(),
    val messageUIType: MessageUIType,
    val isUser: Boolean,
)

fun MessageUI.toMessageModel(): MessageModel {
    val role = if (this.isUser) Role.USER else Role.MODEL

    return MessageModel(this.messageUIType.toMessageType(), role)
}
