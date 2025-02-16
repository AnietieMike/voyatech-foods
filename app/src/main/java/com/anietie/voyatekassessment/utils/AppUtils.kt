package com.anietie.voyatekassessment.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns

object AppUtils {
    // Utility to create a URI for camera images
    fun createImageUri(context: Context): Uri? {
        val contentValues =
            ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            }
        return context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues,
        )
    }

    // Utility to get file name from URI
    fun getFileNameFromUri(
        uri: Uri,
        contentResolver: ContentResolver,
    ): String? {
        val cursor = contentResolver.query(uri, null, null, null, null) ?: return null
        return cursor.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) it.getString(index) else null
            } else {
                null
            }
        }
    }
}
