package com.richzjc.shortvideo.fragment.autoVideo

import android.content.Context
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

suspend fun genHandleVideo(context: Context, statusTV: TextView?): Boolean {
    delay(1000L)
    val file1 = File(context.externalCacheDir, "imageHandle")
    if (!file1.exists())
        file1.mkdirs()

    var frameRate = 50
    val outputFile = File(context.externalCacheDir, "noAudio.mp4")
    if (outputFile.exists()) {
        outputFile.delete()
    }

    val cmd =
        "-framerate ${frameRate} -i ${file1.absolutePath}/%d.png -c:v h264_mediacodec -b:v 5000k -s 1080x1920 -pix_fmt yuv420p -r ${frameRate} ${outputFile.absolutePath}"

    val result = suspendCoroutine { continuation ->
        // 执行FFmpeg命令
        FFmpegKit.executeAsync(
            cmd
        ) { session ->
            if (session != null && ReturnCode.isSuccess(session.returnCode)) {
                AutoFragment.updateStatusText("生成无音频视频成功", statusTV)
                continuation.resume(true)
            } else {
                AutoFragment.updateStatusText("生成无音频视频失败:${FFmpegKitConfig.getLastSession()}", statusTV)
                continuation.resume(false)
            }
        }
    }
    return result
}