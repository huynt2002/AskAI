package com.example.retrofit.util

import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ImageFileSaver {
    suspend fun getSavedImageUriStoragePath(context: Context, uri: Uri?): String? {
        if (uri == null) {
            return null
        }
        return withContext(Dispatchers.IO) {
            val inputStream =
                context.contentResolver.openInputStream(uri) ?: return@withContext null

            val directory =
                File(
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "images",
                )
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val fileName = "${System.currentTimeMillis()}.jpeg"
            val file = File(directory, fileName)

            FileOutputStream(file).use { outputStream -> inputStream.copyTo(outputStream) }

            file.absolutePath.trim() // Return the saved file path
        }
    }
}
