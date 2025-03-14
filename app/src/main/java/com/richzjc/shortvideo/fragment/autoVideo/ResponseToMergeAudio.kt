package com.richzjc.shortvideo.fragment.autoVideo

import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.TextView
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegKitConfig
import com.arthenica.ffmpegkit.ReturnCode
import com.richzjc.shortvideo.fragment.AutoFragment
import kotlinx.coroutines.delay
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun responseToMergeAudio(context: Context, audioFile: File, statusTV: TextView?): Boolean {
    delay(1000L)
    val inputFile = File(context.externalCacheDir, "pinjie.mp4")
    val outputVideoPath = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "${System.currentTimeMillis()}.mp4").absolutePath
    val cmd = "-i ${audioFile.absolutePath} -i ${inputFile.absolutePath} -c:v copy -c:a aac -map 0:a:0 -map 1:v:0 -b:v 5000k -shortest ${outputVideoPath}"
    val result = suspendCoroutine { continuation ->
        // 执行FFmpeg命令
        FFmpegKit.executeAsync(
            cmd
        ) { session ->
            if (session != null && ReturnCode.isSuccess(session.returnCode)) {
                AutoFragment.updateStatusText("拼接音频成功", statusTV)
                continuation.resume(true)
            } else {
                Log.e("short", "拼接音频失败:${FFmpegKitConfig.getLastSession()}")
                AutoFragment.updateStatusText("拼接音频失败:${FFmpegKitConfig.getLastSession()}", statusTV)
                continuation.resume(false)
            }
        }
    }
    return result
}