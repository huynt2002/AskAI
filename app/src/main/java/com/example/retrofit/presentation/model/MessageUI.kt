package com.example.retrofit.presentation.model

import android.net.Uri
import androidx.compose.runtime.Immutable
import com.example.retrofit.data.ai.model.Role
import java.util.UUID

@Immutable
data class MessageUI(
    val id: String = UUID.randomUUID().toString(),
    val text: String = "",
    val imageUri: Uri? = null,
    val imageUrl: String? = null,
    val user: Role,
)
