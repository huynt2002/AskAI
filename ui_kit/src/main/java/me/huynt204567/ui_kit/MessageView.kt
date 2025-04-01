package me.huynt204567.ui_kit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.huynt204567.ui_kit.R

sealed interface MessageType {
    @Stable data class Image(val painter: Painter) : MessageType

    data class Text(val content: String) : MessageType
}

class MessageAvatar(val avatar: @Composable () -> Unit)

object MessageDefault {
    fun messageAvatar(): MessageAvatar {
        return MessageAvatar({
            Icon(imageVector = Icons.Default.Person, contentDescription = null)
        })
    }
}

data class MessageConfig(
    val trailing: Boolean = false,
    val userAvatar: MessageAvatar? = MessageDefault.messageAvatar(),
    val userName: String? = null,
)

@Composable
fun MessageView(
    modifier: Modifier = Modifier,
    messageType: MessageType,
    messageConfig: MessageConfig = MessageConfig(),
) {
    val speakerAvatar: @Composable () -> Unit = {
        if (messageConfig.userAvatar != null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier =
                    Modifier.size(48.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = CircleShape,
                        )
                        .clip(CircleShape)
                        .border(
                            BorderStroke(
                                width = 1.5.dp,
                                color = MaterialTheme.colorScheme.onSurface,
                            ),
                            shape = CircleShape,
                        ),
            ) {
                messageConfig.userAvatar.avatar()
            }
        }
    }
    val speakerName: @Composable () -> Unit = {
        if (messageConfig.userName != null) Text(messageConfig.userName)
    }
    val messageContent: @Composable () -> Unit = {
        when (messageType) {
            is MessageType.Text -> {
                val textColor =
                    if (!messageConfig.trailing) MaterialTheme.colorScheme.onSecondary
                    else MaterialTheme.colorScheme.onPrimary
                val backgroundColor =
                    if (!messageConfig.trailing) MaterialTheme.colorScheme.secondary
                    else MaterialTheme.colorScheme.primary
                val backgroundShape =
                    if (!messageConfig.trailing)
                        RoundedCornerShape(
                            bottomStart = 8.dp,
                            bottomEnd = 8.dp,
                            topStart = 0.dp,
                            topEnd = 8.dp,
                        )
                    else
                        RoundedCornerShape(
                            bottomStart = 8.dp,
                            bottomEnd = 8.dp,
                            topStart = 8.dp,
                            topEnd = 0.dp,
                        )
                Box(
                    modifier =
                        Modifier.background(color = backgroundColor, shape = backgroundShape)
                            .padding(12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = messageType.content, color = textColor)
                }
            }
            is MessageType.Image -> {
                Image(
                    painter = messageType.painter,
                    contentDescription = null,
                    modifier = Modifier.sizeIn(maxWidth = 200.dp, maxHeight = 300.dp),
                )
            }
        }
    }
    val messageBody: @Composable () -> Unit = {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment =
                if (!messageConfig.trailing) {
                    Alignment.Start
                } else {
                    Alignment.End
                },
        ) {
            speakerName()
            Spacer(Modifier.height(4.dp))
            messageContent()
        }
    }

    Box(modifier = modifier) {
        val messagePadding: PaddingValues =
            if (!messageConfig.trailing) {
                PaddingValues(start = 0.dp, end = 32.dp, top = 4.dp, bottom = 4.dp)
            } else {
                PaddingValues(start = 32.dp, end = 0.dp, top = 4.dp, bottom = 4.dp)
            }
        Row(
            modifier = Modifier.fillMaxWidth().padding(messagePadding),
            horizontalArrangement =
                if (!messageConfig.trailing) {
                    Arrangement.Start
                } else {
                    Arrangement.End
                },
        ) {
            if (!messageConfig.trailing) {
                speakerAvatar()
                Spacer(Modifier.width(8.dp))
                Box(modifier = Modifier.weight(1f)) { messageBody() }
            } else {
                Box(modifier = Modifier.weight(1f)) { messageBody() }
                Spacer(Modifier.width(8.dp))
                speakerAvatar()
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun MessageView_Preview() {
    Column {
        MessageView(messageType = MessageType.Text("Hello"))

        MessageView(
            messageType = MessageType.Text("Hi"),
            messageConfig = MessageConfig(trailing = true),
        )
        MessageView(
            messageType = MessageType.Image(painter = painterResource(R.drawable.image_test))
        )
        MessageView(
            messageType = MessageType.Text("This is my image"),
            messageConfig =
                MessageConfig(
                    userAvatar =
                        MessageAvatar {
                            Image(
                                painter = painterResource(R.drawable.image_test),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                            )
                        }
                ),
        )
        MessageView(
            messageType = MessageType.Text("My name is Set"),
            messageConfig = MessageConfig(userName = "Set"),
        )
    }
}
