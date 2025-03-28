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

    val lastTotal =
        SharedPrefsUtil.getString(UtilsContextManager.getInstance().application, "lastTotalPic")
    val selectTotal =
        SharedPrefsUtil.getString(UtilsContextManager.getInstance().application, "selectTotalPic")
    val fileTotalsList = ArrayList<String>()
    val excludeList = ArrayList<String>()
    val lastTotalList = ArrayList<String>()
    lastTotalList.addAll(lastTotal.split(","))
    var selectTotalList = ArrayList<String>()
    selectTotalList.addAll(selectTotal.split(","))
    fileList.forEach {
        fileTotalsList.add(it.name)
    }
    fileTotalsList.forEach {
        if (!lastTotalList.contains(it))
            excludeList.add(it)
    }



    while (fileList.size < picCount) {
        var returnFile: File? = null
        while (returnFile == null || !returnFile.exists()) {
            if (excludeList.size > 0) {
                val randomIndex = (0 until excludeList.size).random()
                returnFile = File(file, excludeList[randomIndex])
            } else {
                if (selectTotalList.isEmpty()) {
                    selectTotalList = ArrayList()
                    selectTotalList.addAll(fileTotalsList)
                }
                val randomIndex = (0 until selectTotalList.size).random()
                returnFile = File(file, selectTotalList[randomIndex])
            }
        }
        selectTotalList?.remove(returnFile.name)
        excludeList?.remove(returnFile.name)
        lastTotalList?.add(returnFile.name)
        fileList.add(returnFile)
    }

    SharedPrefsUtil.saveString(
        UtilsContextManager.getInstance().application,
        "lastTotalPic",
        lastTotalList.joinToString(",")
    )

    SharedPrefsUtil.saveString(
        UtilsContextManager.getInstance().application,
        "selectTotalPic",
        selectTotalList.joinToString(",")
    )
    return fileList
}