package com.richzjc.shortvideo.fragment.autoVideo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.Log
import android.widget.TextView
import com.richzjc.shortvideo.fragment.AutoFragment
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max

suspend fun responseToHandlePic(
    context: Context,
    picList: List<File>,
    audioFileDuration: Long,
    pianTouFileDuration: Long,
    status: TextView?
) {
    try {
        delay(1000L)
        val file1 = File(context.externalCacheDir, "imageHandle")
        if (!file1.exists())
            file1.mkdirs()

        Log.d("short", "handlePath = ${file1.absolutePath}")

        if (file1 != null && file1.exists()) {
            val listFiles = file1.listFiles()
            listFiles?.forEach {
                if (it.exists()) {
                    val delResult = it.delete()
                    Log.d("short", "delResult = ${delResult}")
                }
            }
        }

        val picTime = audioFileDuration - pianTouFileDuration
        val guoDuTotalTime = (picList.size - 1) * 0.3
        val everyCount = ((picTime - (guoDuTotalTime * 1000)) / (20 * picList.size)).toInt()
        val guoDuCount = ((guoDuTotalTime * 1000) / (20 * (picList.size - 1))).toInt()
        val paint = Paint()
        // 设置画笔去掉透明度
        paint.isAntiAlias = true
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
        paint.alpha = 255


        picList.forEachIndexed { index, file ->
            Log.e("short", "生成处理的图片： index = ${index}")
            AutoFragment.updateStatusText("开始处理第${index + 1}张图片", status)
            var curBitmap = BitmapFactory.decodeFile(file.absolutePath)
            var picWidth = 1080
            var picHeight = (curBitmap.height * picWidth) / curBitmap.width
            curBitmap = Bitmap.createScaledBitmap(curBitmap, picWidth, picHeight, true)

            (0 until everyCount)?.forEach {
                val outputBitmap = drawTextAnimBitmap(curBitmap, paint, index)
                saveBitmapToFile(outputBitmap, index * everyCount + index * guoDuCount + it + 1, file1, status)
            }

            if (index < picList.size - 1) {
                var nextBitmap = BitmapFactory.decodeFile(picList.get(index + 1).absolutePath)
                var picWidth = 1080
                var picHeight = (nextBitmap.height * picWidth) / nextBitmap.width
                nextBitmap = Bitmap.createScaledBitmap(nextBitmap, picWidth, picHeight, true)
                (0 until guoDuCount)?.forEach {
                    val scaleRate = (guoDuCount - it - 1) / (guoDuCount * 1.0f)
                    val outputBitmap =
                        drawGuoDuBitmap(curBitmap, nextBitmap, paint, index, scaleRate)
                    saveBitmapToFile(
                        outputBitmap,
                        index * everyCount + index * guoDuCount + everyCount + it + 1,
                        file1,
                        status
                    )
                }
            }
        }
    }catch (exception : Exception){
        exception.printStackTrace()
        Log.e("short", "处理图片异常了： msg = ${exception.message}")
    }
}

private suspend fun drawTextAnimBitmap(curBitmap: Bitmap, paint: Paint, index: Int): Bitmap {
    delay(30)
    paint.color = Color.RED
    paint.textSize = 30f
    paint.alpha = 255
    var outputBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(outputBitmap)
    canvas.drawColor(Color.BLACK)
    canvas.drawBitmap(curBitmap, 0f, max(0, (1920 - curBitmap.height) / 2).toFloat(), paint)
    return outputBitmap
}

private suspend fun drawGuoDuBitmap(
    curBitmap: Bitmap,
    nextBitmap: Bitmap,
    paint: Paint,
    index: Int,
    scaleRate: Float
): Bitmap {
    delay(30)
    paint.color = Color.RED
    paint.textSize = 30f
    paint.alpha = 255
    var outputBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(outputBitmap)
    canvas.drawColor(Color.BLACK)
    //-------------
    var curOutBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
    val canvas1 = Canvas(curOutBitmap)
    canvas1.drawColor(Color.BLACK)
    canvas1.drawBitmap(curBitmap, 0f, max(0, (1920 - curBitmap.height) / 2).toFloat(), paint)
    paint.alpha = 255
    val scaleW =  (1080 * scaleRate).toInt()
    val scaleH = (1920 * scaleRate).toInt()
    if(scaleW > 0 && scaleH > 0) {
        curOutBitmap = Bitmap.createScaledBitmap(
            curOutBitmap,
            (1080 - scaleW)/2,
            scaleH,
            true
        )
    }

    canvas.drawBitmap(curOutBitmap, curOutBitmap.width / 2f, 0f, paint)
//-----------------------
    var nextOutBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
    val canvas2 = Canvas(nextOutBitmap)
    canvas2.drawColor(Color.BLACK)
    canvas2.drawBitmap(nextBitmap, 0f, max(0, (1920 - curBitmap.height) / 2).toFloat(), paint)

    canvas.drawBitmap(nextOutBitmap, 0f, (1920 * scaleRate), paint)
    return outputBitmap
}


private fun saveBitmapToFile(outputBitmap: Bitmap, index: Int, file: File, status: TextView?) {
    try {// 获取应用的内部存储路径
        val imageFile = File(file, "${index}.png")
        // 创建文件输出流
        val fos = FileOutputStream(imageFile)
        // 将 Bitmap 压缩为 JPEG 格式并写入文件流
        outputBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        // 关闭文件流
        fos.close()
    } catch (e: Exception) {
        e.printStackTrace()
        AutoFragment.updateStatusText("异常了： ${e.message}", status)
    }
}