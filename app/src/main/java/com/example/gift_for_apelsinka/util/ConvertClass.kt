package com.example.gift_for_apelsinka.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object ConvertClass {
    suspend fun convertStringToBitmap(encodedString: String?): Bitmap? {
        if (encodedString == null) return null
        return suspendCoroutine {
            val decodeString = Base64.decode(encodedString, Base64.DEFAULT)
            it.resume(BitmapFactory.decodeByteArray(decodeString, 0, decodeString.size))
        }
    }

    suspend fun convertBitmapToString(bitmap: Bitmap?): String? {
        if (bitmap == null) return null
        return suspendCoroutine {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val byteArray = stream.toByteArray()
            try {
                stream.flush()
                stream.close()
            } catch (ignored: IOException) {
            }
            it.resume(Base64.encodeToString(byteArray, Base64.DEFAULT))
        }
    }
}