package com.example.retrofit.data.remote_database.model

import com.example.retrofit.presentation.model.ConversationUI
import com.google.gson.annotations.SerializedName
import java.time.Instant

data class ConversationRequest(val id: String?, val title: String)

data class ConversationResponse(
    val id: String,
    val title: String,
    val createAt: Instant,
    val updateAt: Instant,
)

fun ConversationResponse.toConversationUI(): ConversationUI{
    return ConversationUI(id = id,title=title,emptyList())
}

