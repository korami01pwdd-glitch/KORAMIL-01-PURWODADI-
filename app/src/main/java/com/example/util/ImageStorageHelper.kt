package com.example.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ImageStorageHelper {
    /**
     * Copies selected image from photo picker to app's internal storage
     * so it persists permanently and can be viewed anytime offline.
     */
    fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val photosDir = File(context.filesDir, "komsos_photos")
            if (!photosDir.exists()) {
                photosDir.mkdirs()
            }
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.getDefault()).format(Date())
            val targetFile = File(photosDir, "IMG_KOMSOS_$timeStamp.jpg")
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(targetFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            targetFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
