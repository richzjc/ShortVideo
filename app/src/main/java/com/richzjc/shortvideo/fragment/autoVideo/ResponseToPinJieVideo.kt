package com.richzjc.shortvideo.fragment.autoVideo

import java.io.File
import android.content.Context

suspend fun responseToPinJieVideo(context: Context, pianTouFile: File): Boolean {
    val outputFile = File(context.externalCacheDir, "noAudio.mp4")
    return false
}