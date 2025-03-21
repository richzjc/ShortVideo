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

//    ffmpeg -i /Users/zhangjianchuan/Desktop/marry.mp4 -i /Users/zhangjianchuan/Desktop/1.mp4 -filter_complex "[1:v]colorkey=0x000000:0.1:0.1[ckout];[0:v][ckout]overlay=shortest=1[out]" -map "[out]" output.mp4
    val cmd = "-i ${inputFile.absolutePath} -i ${pianTouFile.absolutePath} -filter_complex [1:v]colorkey=0x000000:0.1:0.1[ckout];[0:v][ckout]overlay=shortest=1[out] -map [out] ${outputFile.absolutePath}"
//    val cmd = "-i ${pianTouFile.absolutePath} -i ${inputFile.absolutePath}  -b:v 5000k -s 1080x1920 -filter_complex [0:v][1:v]concat=n=2:v=1:a=0[v] -map [v] ${outputFile.absolutePath}"
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