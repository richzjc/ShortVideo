package com.richzjc.shortvideo.fragment.videoCreate

import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Environment
import android.util.Log
import android.widget.TextView
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegKitConfig
import com.arthenica.ffmpegkit.ReturnCode
import com.richzjc.shortvideo.util.QDUtil
import java.io.File

fun gennerateVideoNoAudio(originPath: String, context: Context, statusTV: TextView?, index: Int) {
    updateStatusText("开始生成第${index}个视频", statusTV)
    try {
        // 创建一个 MediaMetadataRetriever 对象
        val retriever = MediaMetadataRetriever()


        // 设置数据源为视频文件的 URI
        retriever.setDataSource(originPath)

        // 获取视频总时长
        val time =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION) ?: "0"
        val frameCountStr =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT)
        val frameCount = frameCountStr?.toLongOrNull() ?: 0


        // Define the output video size and frame rate
        val second = time.toLong() / 1000L
        val fps = frameCount * 1.0 / second

        var f = File(QDUtil.getShareImageCache(context).absolutePath, "video")
        if (f.exists())
            f.delete()

        f.mkdirs()

        val outputFile = File(
            f,
            "hecheng_noaudio${index}.mp4"
        )


        val file1 = File(QDUtil.getShareImageCache(context).absolutePath, "image_handle${index}")
        if (!file1.exists())
            file1.mkdirs()


        // 设置帧率，假设每秒24帧
        val frameRate = fps
        // 构建FFmpeg命令


        val cmd = "-framerate ${frameRate} -i ${file1.absolutePath}/%d.png -c:v h264_mediacodec -b:v 5000k -s 1080x1920 -pix_fmt yuv420p -r ${frameRate} ${outputFile.absolutePath}"

        // 执行FFmpeg命令
        FFmpegKit.executeAsync(
            cmd
        ) { session ->
            if (session != null && ReturnCode.isSuccess(session.returnCode)) {
                Log.d("FFmpeg", "视频生成成功！路径: /sdcard/Movies/output.mp4");
                updateStatusText("生成${index}个视频成功", statusTV)
                heChengVideo(statusTV, context, originPath, index)
            } else {
                Log.e("FFmpeg", "失败原因: " + FFmpegKitConfig.getLastSession() + "；sessionCode = ${session?.returnCode}");
                updateStatusText("生成${index}个视频失败", statusTV)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        updateStatusText("生成${index}个视频失败", statusTV)
    }
}

fun heChengVideo(
    statusTV: TextView?,
    context: Context,
    originPath: String?,
    index: Int
) {
    updateStatusText("合成第${index}个最终视频", statusTV)

    val file = File(QDUtil.getShareImageCache(context), "video")
    if (!file.exists())
        file.mkdirs()

    val inputVideoPath1 = File(file, "hecheng_noaudio${index}.mp4").absolutePath
    // 输出音频文件路径
    val outputVideoPath = File(QDUtil.getShareImageCache(context), "hasAudio.mp4").absolutePath
    val cmd = "-i ${originPath} -i ${inputVideoPath1} -c:v copy -c:a aac -map 0:a:0 -map 1:v:0 -shortest ${outputVideoPath}"

    FFmpegKit.executeAsync(
        cmd
    ) { session ->
        if (session != null && ReturnCode.isSuccess(session.returnCode)) {
            updateStatusText("合成成功", statusTV)
            pinJiePianTou(statusTV, context, outputVideoPath)
        } else {
            Log.e("FFmpeg", "失败原因: " + session?.failStackTrace);
            updateStatusText("合成第${index}个视频失败", statusTV)
        }
    }
}

fun pinJiePianTou(statusTV: TextView?,  context: Context, inputPath1 : String) {
    updateStatusText("开始拼接片头", statusTV)

    val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "piantou")
    if (!file.exists())
        file.mkdirs()

    val inputVideoPath1 = File(file, "piantou.mp4").absolutePath
    // 输出音频文件路径
    val outputVideoPath = File(QDUtil.getShareImageCache(context), "hasPianTou.mp4").absolutePath
    val cmd = "-i ${inputVideoPath1} -i ${inputPath1} -filter_complex [0:v][0:a][1:v][1:a]concat=n=2:v=1:a=1[v][a] -map [v] -map [a] ${outputVideoPath}"

    FFmpegKit.executeAsync(
        cmd
    ) { session ->
        if (session != null && ReturnCode.isSuccess(session.returnCode)) {
            updateStatusText("接拼片头成功", statusTV)
        } else {
            Log.e("FFmpeg", "失败原因: " + session?.failStackTrace);
            updateStatusText("接拼片头失败", statusTV)
        }
    }
}

