package com.example.retrofit.data.ai.model

import com.google.gson.annotations.SerializedName

data class Part(
    @SerializedName("text") val text: String? = null,
    @SerializedName("inline_data") val inlineData: InlineData? = null,
)
