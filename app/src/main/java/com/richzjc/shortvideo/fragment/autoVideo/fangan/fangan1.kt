package com.richzjc.shortvideo.fragment.autoVideo.fangan

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.widget.TextView
import com.richzjc.shortvideo.fragment.AutoFragment
import com.richzjc.shortvideo.fragment.AutoFragment.Companion.audioFile
import kotlinx.coroutines.delay
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import java.io.File
import java.io.FileOutputStream
import kotlin.math.abs


/**
 * 透明度变换
 */
suspend fun fangan1(
    handleFile: File,
    preBitmap: Bitmap,
    curBitmap: Bitmap,
    status: TextView?,
    totalCount: Int,
    paint: Paint
) {
    delay(30)

    val originSize = handleFile.listFiles().size

    (0 until 60)?.forEach {
        if (handleFile.listFiles().size < totalCount) {
            if (it < 30) {
                fangan1Small30(preBitmap, curBitmap, paint, handleFile, status, it)
            } else {
                val bfile = File(handleFile, "${originSize + 30}.png")
                var pbitmap = BitmapFactory.decodeFile(bfile.absolutePath)
                fang1Large30(pbitmap, paint, handleFile, status, it)
            }
        }
    }
}

private suspend fun fang1Large30(
    pBitmap: Bitmap,
    paint: Paint,
    file1: File,
    status: TextView?,
    index: Int
) {
    delay(30)
    paint.alpha = 255

    val realWidth = pBitmap.width * (1 + 0.01f * (index - 30))
    val realHeight = pBitmap.height * (1 + 0.01f * (index - 30))
    val preBitmap = Bitmap.createScaledBitmap(pBitmap, realWidth.toInt(), realHeight.toInt(), true)
    var outputBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(outputBitmap)
    canvas.drawBitmap(preBitmap, (1080 - realWidth) / 2f, (1920 - realHeight) / 2f, paint)
    canvasDrawText(canvas, paint, file1)
    saveBitmapToFile(outputBitmap, file1, status)
}

private suspend fun fangan1Small30(
    preBitmap: Bitmap,
    curBitmap: Bitmap,
    paint: Paint,
    handleFile: File,
    status: TextView?,
    index: Int
) {
    delay(30)
    paint.alpha = 255
    var outputBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(outputBitmap)

    var blurBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
    val blurCanvas = Canvas(blurBitmap)
    blurCanvas.drawBitmap(curBitmap, 0f, 0f, paint)
    blurBitmap = blur(blurBitmap)
    canvas.drawBitmap(blurBitmap, 0f, 0f, paint)

    val preAlpha = (255 - (255 / 15f) * index)
    if (preAlpha > 0) {
        paint.alpha = preAlpha.toInt()
        canvas.drawBitmap(preBitmap, 0f, 0f, paint)
    }

    paint.alpha = 255
    val widthGap = 1080 / 30f
    val heightGap = 1920 / 30f
    val realWidth = (widthGap * index).toInt()
    val realHeight = (heightGap * index).toInt()
    if (realWidth > 0 && realHeight > 0) {
        val realBitmap = Bitmap.createScaledBitmap(curBitmap, realWidth, realHeight, true)
        canvas.drawBitmap(realBitmap, (1080 - realWidth) / 2f, (1920 - realHeight) / 2f, paint)
    }
    canvasDrawText(canvas, paint, handleFile)
    saveBitmapToFile(outputBitmap, handleFile, status)
}

fun canvasDrawText(
    canvas: Canvas,
    paint: Paint,
    handleFile : File
) {
    val lastIndex = audioFile!!.name.lastIndexOf(".")
    val fileName = audioFile!!.name.substring(0, lastIndex)

    //TODO 绘制阴影
    canvas.drawColor(Color.parseColor("#22000000"))
    //TODO 绘制标题
    paint.setTypeface(Typeface.DEFAULT_BOLD)
    paint.alpha = 255
    paint.color = Color.WHITE
    val realText = "<<${fileName}>>"
    paint.textSize = 70f
    val rect1 = Rect()
    paint.getTextBounds(realText, 0, realText.length, rect1)
    canvas.drawText(realText, (1080 - abs(rect1.right - rect1.left)) / 2f, 200f, paint)
    val realText1 = "经典歌曲 / 超级好听"
    paint.textSize = 40f
    val rect2 = Rect()
    paint.getTextBounds(realText1, 0, realText1.length, rect2)
    canvas.drawText(
        realText1,
        (1080 - abs(rect2.right - rect2.left)) / 2f,
        200f + abs(rect1.bottom - rect1.top) + 10f,
        paint
    )
}

fun saveBitmapToFile(outputBitmap: Bitmap, file: File, status: TextView?) {
    try {// 获取应用的内部存储路径
        var index = 0
        val arr = file.listFiles()
        if (arr != null)
            index = arr.size + 1
        else
            index = 1
        val imageFile = File(file, "${index}.png")
        // 创建文件输出流
        val fos = FileOutputStream(imageFile)
        // 将 Bitmap 压缩为 JPEG 格式并写入文件流
        outputBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        // 关闭文件流
        fos.close()
        outputBitmap.recycle()
    } catch (e: Exception) {
        e.printStackTrace()
        AutoFragment.updateStatusText("异常了： ${e.message}", status)
    }
}

// 高斯模糊工具类
fun blur(bitmap: Bitmap, radius: Int = 87): Bitmap {
    val srcMat = Mat(bitmap.height, bitmap.width, CvType.CV_8UC4)
    Utils.bitmapToMat(bitmap, srcMat)

    Imgproc.GaussianBlur(srcMat, srcMat, Size(radius.toDouble(), radius.toDouble()), 0.0)

    val resultBitmap = Bitmap.createBitmap(srcMat.cols(), srcMat.rows(), Bitmap.Config.ARGB_8888)
    Utils.matToBitmap(srcMat, resultBitmap)
    srcMat.release()
    return resultBitmap
}