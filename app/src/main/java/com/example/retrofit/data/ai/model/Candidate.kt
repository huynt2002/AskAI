package com.example.retrofit.data.ai.model
import com.google.gson.annotations.SerializedName

data class Candidate(@SerializedName("content") val content: Content)
