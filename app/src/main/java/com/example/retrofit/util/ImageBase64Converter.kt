package com.example.retrofit.util

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream

object ImageBase64Converter {
    fun encodeBitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}
