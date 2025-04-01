package com.example.retrofit.data.ai.model

import com.google.gson.annotations.SerializedName


data class InlineData(
    @SerializedName("mime_type") private val mimeType: String = "image/jpeg",
    @SerializedName("data") val data: String,
)
