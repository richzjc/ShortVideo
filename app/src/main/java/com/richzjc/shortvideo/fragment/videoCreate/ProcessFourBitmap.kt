package com.richzjc.shortvideo.fragment.videoCreate

import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Environment
import android.util.Log
import android.widget.TextView
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import com.richzjc.shortvideo.R
import com.richzjc.shortvideo.util.QDUtil
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.io.InputStream
import java.util.Arrays

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

        val listFiles = file1.listFiles()
        if (listFiles == null || listFiles.size <= 0)
            return

        Arrays.sort<File>(listFiles) { f1: File, f2: File ->
            val num1: Int = extractNumber(f1.getName())
            val num2: Int = extractNumber(f2.getName())
            Integer.compare(num1, num2)
        }


        val file2 = File(QDUtil.getShareImageCache(context).absolutePath, "piantou_handle")
        if (!file2.exists())
            file2.mkdirs()

        val listFiles2 = file2.listFiles()
        if (listFiles2 == null || listFiles2.size <= 0)
            return

        Arrays.sort<File>(listFiles2) { f1: File, f2: File ->
            val num1: Int = extractNumber(f1.getName())
            val num2: Int = extractNumber(f2.getName())
            Integer.compare(num1, num2)
        }

        val itemOffset = (listFiles2.size * 1f)/fps

        // 创建一个临时文本文件来存储图片路径
        val imageListPathFile = File(context?.getExternalFilesDir(null), "my_directory");
        if (imageListPathFile.exists())
            imageListPathFile.delete()

        val imageListPath: String = imageListPathFile.absolutePath
        createImageListFile(listFiles2, listFiles, imageListPath, fps)

        // 设置帧率，假设每秒24帧
        val frameRate = fps
        // 构建FFmpeg命令
        val cmd =
            "-y -f concat -safe 0 -i $imageListPath -vsync vfr -pix_fmt yuv420p -r $frameRate -b:v 10M -s 1080x1920 -preset slow -crf 18 ${outputFile.absolutePath}"

        // 执行FFmpeg命令
        val returnCode = FFmpeg.execute(cmd)
        if (returnCode == Config.RETURN_CODE_SUCCESS) {
            updateStatusText("生成${index}个视频成功", statusTV)
            heChengVideo(statusTV, context, originPath, index, itemOffset)
        } else if (returnCode == Config.RETURN_CODE_CANCEL) {
            updateStatusText("生成${index}个视频取消", statusTV)
        } else {
            updateStatusText("生成${index}个视频失败", statusTV)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        updateStatusText("生成${index}个视频失败", statusTV)
    }
}

fun heChengVideo(statusTV: TextView?, context: Context, originPath: String?, index: Int, timeOffset : Double) {
    updateStatusText("合成第${index}个最终视频", statusTV)

    val file = File(QDUtil.getShareImageCache(context), "video")
    if (!file.exists())
        file.mkdirs()

    var audioFile = File(file, "audio.mp4")
    val command1 = "-i ${originPath} -q:a 0 -map a ${audioFile.absolutePath}"
    var returnCode1 = FFmpeg.execute(command1)
    if (returnCode1 == 0) {
        updateStatusText("提取音频成功", statusTV)


    val inputVideoPath1 = File(file, "hecheng_noaudio${index}.mp4").absolutePath


        var fileName = ""
        if (index == 0)
            fileName = "2"
        else if (index == 1)
            fileName = "4";
        else if (index == 2)
            fileName = "6"
        else
            fileName = "8"


        // 输出音频文件路径
        val outputVideoPath = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "${fileName}.mp4").absolutePath

//        val command = "-itsoffset ${timeOffset} -i ${audioFile.absolutePath} -i ${inputVideoPath1} -c:v copy -c:a aac ${outputVideoPath}"

        val command = arrayOf<String>(
            "-i", audioFile.absolutePath,  // 第一个视频，音频来源
            "-i", inputVideoPath1,  // 第二个视频，视频来源
            "-c:v", "copy",  // 复制第二个视频的视频流
            "-c:a", "aac",  // 使用 AAC 编码器来处理音频
            "-map", "0:a:0",  // 从第一个视频中选择第一条音频流
            "-map", "1:v:0",  // 从第二个视频中选择第一条视频流
            outputVideoPath // 输出合成后的视频文件路径
        )

//        val command = arrayOf<String>(
//            "-i", inputVideoPath,  // 第一个视频，音频来源
//            "-i", inputVideoPath1,  // 第二个视频，视频来源
//            "-c:v", "copy",  // 复制第二个视频的视频流
//            "-c:a", "aac",  // 使用 AAC 编码器来处理音频
//            "-map", "0:a:0",  // 从第一个视频中选择第一条音频流
//            "-map", "1:v:0",  // 从第二个视频中选择第一条视频流
//            "-shortest",  // 以较短的视频长度为基准
//            outputVideoPath // 输出合成后的视频文件路径
//        )


        var returnCode = FFmpeg.execute(command)
        if (returnCode == 0) {
            updateStatusText("合成第${index}个视频成功", statusTV)
        } else {
            Log.e("ffmpeg", Config.getLastCommandOutput())
            updateStatusText("合成第${index}个视频失败", statusTV)
        }
    } else {
        Log.e("ffmpeg", Config.getLastCommandOutput())
        updateStatusText("提取音频失败", statusTV)
    }
}

fun createImageListFile(
    imagePaths2: Array<File>,
    imagePaths: Array<File>,
    imageListPath: String,
    frameCount: Double
) {
    try {
        val time = 1 / frameCount
        BufferedWriter(FileWriter(imageListPath)).use { writer ->
            for (imagePath in imagePaths2) {
                writer.write("file '${imagePath.absolutePath}'\n")
                // 设置每张图片显示的持续时间
                writer.write("duration ${time}\n") // 每张图片显示时间（秒），24帧每秒 -> 1/24 ≈ 0.04
            }

            for (imagePath in imagePaths) {
                writer.write("file '${imagePath.absolutePath}'\n")
                // 设置每张图片显示的持续时间
                writer.write("duration ${time}\n") // 每张图片显示时间（秒），24帧每秒 -> 1/24 ≈ 0.04
            }
            // 需要最后一张图片的持续时间
            writer.write("file '" + imagePaths[imagePaths.size - 1].absolutePath + "'\n")
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun extractNumber(fileName: String): Int {
    val parts = fileName.split(".")
    return parts[0].toInt() // Assuming file names are like "1.jpg", "2.jpg", etc.
}


