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

    val cmd = "-i ${inputFile.absolutePath} -i ${pianTouFile.absolutePath} -b:v 5000k -filter_complex [1:v]scale=1080:1920:flags=lanczos,colorkey=0x000000:0.3:0.2[ckout];[0:v][ckout]overlay=shortest=1:x=0:y=0,unsharp=5:5:1.0:5:5:0.5[out] -map [out] -c:v libx264 -crf 18 -preset slower -profile:v high -movflags +faststart ${outputFile.absolutePath}"
    val result = suspendCoroutine { continuation ->
        // 执行FFmpeg命令
        FFmpegKit.executeAsync(
            cmd
        ) { session ->
            if (session != null && ReturnCode.isSuccess(session.returnCode)) {
                AutoFragment.updateStatusText("添加歌词成功", statusTV)
                continuation.resume(true)
            } else {
                Log.e("short", "添加歌词失败:${FFmpegKitConfig.getLastSession()}")
                AutoFragment.updateStatusText("添加歌词失败:${FFmpegKitConfig.getLastSession()}", statusTV)
                continuation.resume(false)
            }
        }
    }
    return result
}