package com.richzjc.shortvideo.fragment.autoVideo

import android.content.Context
import android.util.Log
import kotlinx.coroutines.delay
import java.io.File

suspend fun responseToHandlePic(context : Context, picList: List<File>) {
    delay(1000L)
    val file1 = File(context.externalCacheDir, "imageHandle")
    if(!file1.exists())
        file1.mkdirs()

    if (file1 != null && file1.exists()) {
        val listFiles = file1.listFiles()
        listFiles?.forEach {
            if (it.exists()) {
                val delResult =  it.delete()
                Log.d("short", "delResult = ${delResult}")
            }
        }
    }



}