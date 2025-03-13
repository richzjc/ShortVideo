package com.richzjc.shortvideo.fragment.autoVideo

import android.os.Environment
import java.io.File

fun responseToHandlePic(picList: List<File>) {
    val file1 = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "imageHandle")
    if(!file1.exists())
        file1.mkdirs()

    if (file1 != null) {
        val listFiles = file1.listFiles()
        listFiles?.forEach {
            if (it.exists())
                it.delete()
        }
    }
}