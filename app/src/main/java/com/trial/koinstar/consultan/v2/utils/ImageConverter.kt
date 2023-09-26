package com.trial.koinstar.consultan.v2.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream

class ImageConverter {
    fun uriToBase64(context: Context, uri: Uri): String? {
        try {
            // Ambil InputStream dari URI
            val inputStream = context.contentResolver.openInputStream(uri)

            // Baca gambar ke dalam Bitmap
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            // Resize gambar jika terlalu besar
            val resizedBitmap = resizeBitmap(bitmap)

            // Konversi Bitmap ke Base64
            return bitmapToBase64(resizedBitmap)
        } catch (e: Exception) {
            Log.e("ConvertToBase64", "Error: ${e.message}")
        }
        return null
    }

    fun resizeBitmap(bitmap: Bitmap): Bitmap {
        // Tentukan ukuran maksimal gambar yang diinginkan
        val maxWidth = 800
        val maxHeight = 600

        val width = bitmap.width
        val height = bitmap.height

        // Hitung ulang ukuran sesuai proporsi asli
        val ratio: Float = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int
        if (width > height) {
            newWidth = maxWidth
            newHeight = (maxWidth / ratio).toInt()
        } else {
            newHeight = maxHeight
            newWidth = (maxHeight * ratio).toInt()
        }

        // Resize gambar ke ukuran yang diinginkan
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun getConversionImage(encodedImage: String?): Bitmap? {
        val bytes = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}