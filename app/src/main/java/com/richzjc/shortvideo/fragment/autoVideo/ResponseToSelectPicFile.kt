package com.richzjc.shortvideo.fragment.autoVideo

import android.os.Environment
import java.io.File
import java.util.Arrays


fun responseToSelectPicFile(totalDuration: Float, pianTouDuration: Float): List<File> {
    val picDuration = totalDuration - pianTouDuration
    val picCount = Math.floor(picDuration / 2.0).toInt()
    val fileList = ArrayList<File>()
    if (picCount <= 0)
        return fileList
    val picDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    val list = picDirectory.listFiles()
    val allPicList = ArrayList<File>()
    list.forEach {
        if (it.isFile && it.name.contains("dreamina"))
            allPicList.add(it)
    }

    if (allPicList.size <= 0)
        return fileList

// 使用Lambda表达式简化Comparator实现
    Arrays.sort(
        allPicList.toTypedArray()
    ) { f1: File, f2: File ->
        java.lang.Long.compare(
            f2.lastModified(),
            f1.lastModified()
        )
    }

    if (allPicList.size <= 0)
        return fileList


    fileList.add(allPicList.get(0))

    while (fileList.size < picCount) {
        val randomIndex = (0 until allPicList.size).random()
        val file = allPicList.get(randomIndex)
        if (!fileList.contains(file))
            fileList.add(file)
    }

    return fileList
}