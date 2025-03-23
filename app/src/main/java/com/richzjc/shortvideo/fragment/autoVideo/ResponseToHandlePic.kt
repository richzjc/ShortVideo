package com.richzjc.shortvideo.fragment.autoVideo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.Typeface
import android.util.Log
import android.widget.TextView
import com.richzjc.shortvideo.fragment.AutoFragment
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream
import kotlin.math.abs

suspend fun responseToHandlePic(
    context: Context,
    picList: List<File>,
    audioFileDuration: Long,
    fileName: String,
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

        val picTime = audioFileDuration
        val everyCount = (picTime / (33 * picList.size)).toInt()
        val paint = Paint()
        // 设置画笔去掉透明度
        paint.isAntiAlias = true
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
        paint.alpha = 255

        picList.forEachIndexed { index, file ->
            AutoFragment.updateStatusText("开始处理第${index + 1}张图片", status)
            var curBitmap = BitmapFactory.decodeFile(file.absolutePath)
            var picWidth = 1080 * 1.1f
            var picHeight = (curBitmap.height * picWidth) / curBitmap.width
            curBitmap =
                Bitmap.createScaledBitmap(curBitmap, picWidth.toInt(), picHeight.toInt(), true)

            var nextBitmap: Bitmap? = null
            if (index + 1 < picList.size) {
                nextBitmap = BitmapFactory.decodeFile(picList.get(index + 1).absolutePath)
                var picHeight = (nextBitmap.height * picWidth) / nextBitmap.width
                nextBitmap =
                    Bitmap.createScaledBitmap(nextBitmap, picWidth.toInt(), picHeight.toInt(), true)
            }

            (0 until everyCount)?.forEach {
                val outputBitmap = drawTextAnimBitmap(
                    curBitmap,
                    nextBitmap,
                    paint,
                    it,
                    everyCount,
                    index,
                    fileName
                )
                saveBitmapToFile(outputBitmap, index * everyCount + it + 1, file1, status)
            }
        }
    } catch (exception: Exception) {
        exception.printStackTrace()
        Log.e("short", "处理图片异常了： msg = ${exception.message}")
    }
}

private suspend fun drawTextAnimBitmap(
    curBitmap: Bitmap,
    nextBitmap: Bitmap?,
    paint: Paint,
    index: Int,
    everyCount: Int,
    outerIndex: Int,
    fileName: String
): Bitmap {
    delay(30)
    paint.color = Color.RED
    paint.textSize = 30f
    paint.alpha = 255
    var outputBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(outputBitmap)
    //TODO 第一步，绘制黑色背景
    canvas.drawColor(Color.BLACK)
    val scrollGap = (1080 * 0.1f) / (everyCount - 10)
    val alphaGap = 25.5f
    //TODO 判断是左滑还是右滑
    if (outerIndex % 2 == 0) {
        //向右滑动
        //TODO 判断有没有滚动到边
        if (index >= (everyCount - 10)) {
            //TODO 判断NextBitMap是否为空
            if (nextBitmap == null) {
                val startX = 0f
                paint.alpha = 255
                canvas.drawBitmap(curBitmap, startX, (1920 - curBitmap.height) / 2.toFloat(), paint)
            } else {
                var alphaValue = ((10 - (everyCount - index) + 1) * alphaGap).toInt()
                if (alphaValue > 255)
                    alphaValue = 255

                paint.alpha = alphaValue
                var startX = 0f
                canvas.drawBitmap(nextBitmap, startX, (1920 - nextBitmap.height) / 2.toFloat(), paint)

                var alpha1 = 255 - alphaValue
                if(alpha1 <= 0)
                    alpha1 = 0
                paint.alpha = alpha1
                canvas.drawBitmap(curBitmap, startX, (1920 - curBitmap.height) / 2.toFloat(), paint)
            }
        } else {
            val startX = 1080 - curBitmap.width + index * scrollGap
            paint.alpha = 255
            canvas.drawBitmap(curBitmap, startX, (1920 - curBitmap.height) / 2.toFloat(), paint)
        }
    } else {
        //向左滑动
        //TODO 判断有没有滚动到边
        if (index >= (everyCount - 10)) {
            //TODO 判断NextBitMap是否为空
            if (nextBitmap == null) {
                val startX = -0.1f * 1080
                paint.alpha = 255
                canvas.drawBitmap(curBitmap, startX, (1920 - curBitmap.height) / 2.toFloat(), paint)
            } else {
                var alphaValue = ((10 - (everyCount - index) + 1) * alphaGap).toInt()
                if (alphaValue > 255)
                    alphaValue = 255

                paint.alpha = alphaValue
                val startX = -0.1f * 1080
                canvas.drawBitmap(nextBitmap, startX, (1920 - nextBitmap.height) / 2.toFloat(), paint)

                var alpha1 = 255 - alphaValue
                if(alpha1 <= 0)
                    alpha1 = 0
                paint.alpha = alpha1
                canvas.drawBitmap(curBitmap, startX, (1920 - curBitmap.height) / 2.toFloat(), paint)
            }
        } else {
            val startX = -index * scrollGap
            paint.alpha = 255
            canvas.drawBitmap(curBitmap, startX, (1920 - curBitmap.height) / 2.toFloat(), paint)
        }
    }

    canvasDrawText(canvas, paint, fileName, index, everyCount, outerIndex)
    return outputBitmap
}

private fun canvasDrawText(
    canvas: Canvas,
    paint: Paint,
    fileName: String,
    index: Int,
    everyCount: Int,
    outerIndex: Int
) {
    //TODO 绘制阴影
    canvas.drawColor(Color.parseColor("#551478f0"))
    //TODO 绘制标题
    paint.setTypeface(Typeface.DEFAULT_BOLD)
    paint.alpha = 255
    paint.color = Color.WHITE
    val realText = "< ${fileName} >"
    paint.textSize = 70f
    val rect1 = Rect()
    paint.getTextBounds(realText, 0, realText.length, rect1)
    canvas.drawText(realText, (1080 - abs(rect1.right - rect1.left)) / 2f, 400f, paint)
    val realText1 = "经典歌曲 / 超级好听"
    paint.textSize = 40f
    val rect2 = Rect()
    paint.getTextBounds(realText1, 0, realText1.length, rect2)
    canvas.drawText(
        realText1,
        (1080 - abs(rect2.right - rect2.left)) / 2f,
        400f + abs(rect1.bottom - rect1.top) + 10f,
        paint
    )
}


private fun saveBitmapToFile(outputBitmap: Bitmap, index: Int, file: File, status: TextView?) {
    try {// 获取应用的内部存储路径
        Log.e("saveFile", "${index}")
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