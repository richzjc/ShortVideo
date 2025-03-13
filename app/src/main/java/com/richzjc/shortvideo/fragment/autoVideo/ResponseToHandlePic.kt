package com.richzjc.shortvideo.fragment.autoVideo

import android.os.Build
import android.os.Environment
import android.util.Log
import kotlinx.coroutines.delay
import java.io.File

suspend fun responseToHandlePic(picList: List<File>) {
    delay(1000L)
    val file1 = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "imageHandle")
    if(!file1.exists())
        file1.mkdirs()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Log.d("TAG", "Storage Manager Permission: ${Environment.isExternalStorageManager()}")
    }
    if (file1 != null) {
        val listFiles = file1.listFiles()
        listFiles?.forEach {
            if (it.exists())
                it.delete()
        }
    }
}