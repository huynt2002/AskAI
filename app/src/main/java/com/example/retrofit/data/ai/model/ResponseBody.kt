package com.example.retrofit.data.ai.model

import com.google.gson.annotations.SerializedName

data class ResponseBody(@SerializedName("candidates") val candidates: List<Candidate>)

fun ResponseBody.toMessageModel(): MessageModel {
    return this.candidates.firstOrNull()?.content?.toMessageModel()
        ?: MessageModel(text = "error!", user = Role.MODEL)
}
