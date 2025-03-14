package com.richzjc.shortvideo.fragment.autoVideo

import java.io.File
import android.content.Context
import android.util.Log
import android.widget.TextView
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegKitConfig
import com.arthenica.ffmpegkit.ReturnCode
import com.richzjc.shortvideo.fragment.AutoFragment
import kotlinx.coroutines.delay
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun responseToPinJieVideo(context: Context, pianTouFile: File, statusTV : TextView?): Boolean {
    delay(1000L)
    val inputFile = File(context.externalCacheDir, "noAudio.mp4")
    val outputFile = File(context.externalCacheDir, "pinjie.mp4")
    if (outputFile.exists()) {
        outputFile.delete()
    }
    val cmd = "-i ${pianTouFile.absolutePath} -i ${inputFile.absolutePath} -b:v 5000k -s 1080x1920 -filter_complex [0:v][0:a][1:v][1:a]concat=n=2:v=1:a=1[v][a] -map [v] -map [a] ${outputFile.absolutePath}"
    val result = suspendCoroutine { continuation ->
        // 执行FFmpeg命令
        FFmpegKit.executeAsync(
            cmd
        ) { session ->
            if (session != null && ReturnCode.isSuccess(session.returnCode)) {
                AutoFragment.updateStatusText("拼接片头成功", statusTV)
                continuation.resume(true)
            } else {
                Log.e("short", "拼接片头失败:${FFmpegKitConfig.getLastSession()}")
                AutoFragment.updateStatusText("拼接片头失败:${FFmpegKitConfig.getLastSession()}", statusTV)
                continuation.resume(false)
            }
        }
    }
    return result
    return false
}