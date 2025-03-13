package com.richzjc.shortvideo.fragment.autoVideo

import android.media.MediaMetadataRetriever
import android.os.Environment
import android.util.Log
import kotlinx.coroutines.delay
import java.io.File

fun responseToSelectAudioFile(): File? {
    val file = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        "audio"
    )
    if (!file.exists())
        return null

    val fileList = file.listFiles()
    val size = fileList.size
    if (size <= 0)
        return null

    val randomIndex = (0 until size).random()
    return fileList.get(randomIndex)
}

suspend fun responseToGetAudioFileDuration(file: File): Long {
    try {// 创建一个 MediaMetadataRetriever 对象
        Log.e("short", "audioFile = ${file.absolutePath}")
        delay(1000L)
        val retriever = MediaMetadataRetriever()
        // 设置数据源为视频文件的 URI
        retriever.setDataSource(file.absolutePath)

        // 获取视频总时长
        val time =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION) ?: "0"
        val second = time.toLong()
        Log.e("short", "audioDuration = ${second}")
        return second
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return 0L
}