package com.richzjc.shortvideo.fragment.autoVideo

import android.os.Environment
import android.text.TextUtils
import android.util.Log
import com.richzjc.shortvideo.UtilsContextManager
import com.richzjc.shortvideo.util.SharedPrefsUtil
import kotlinx.coroutines.delay
import java.io.File
import java.util.Arrays


suspend fun responseToSelectPicFile(picDuration: Long): List<File> {
    delay(1000L)
    val picCount = 30
    val fileList = ArrayList<File>()
    if (picCount <= 0)
        return fileList
    val file = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
        "pic"
    )
    val allPicList = file.listFiles()
    if (allPicList.isEmpty())
        return fileList

    while (fileList.size < picCount) {
        val randomIndex = (0 until allPicList.size).random()
        val file = allPicList[randomIndex]
        if (!fileList.contains(file))
            fileList.add(file)
    }

    return fileList
}