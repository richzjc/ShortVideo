package com.richzjc.shortvideo.fragment.autoVideo.fangan

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.widget.TextView
import com.richzjc.shortvideo.R
import com.richzjc.shortvideo.UtilsContextManager
import com.richzjc.shortvideo.fragment.AutoFragment
import com.richzjc.shortvideo.fragment.autoVideo.fangan.interpreter.calculateCos
import com.richzjc.shortvideo.fragment.autoVideo.fangan.interpreter.calculateSin
import com.richzjc.shortvideo.fragment.autoVideo.fangan.interpreter.calculatex2
import kotlinx.coroutines.delay
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Rect
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import java.io.File
import java.io.FileOutputStream
import kotlin.math.cos


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
    (0 until 90)?.forEach {
        if (handleFile.listFiles().size < totalCount) {
            if (it < 30) {
                fangan1Small30(preBitmap, curBitmap, paint, handleFile, status, it)
            } else {
                fang1Large30(curBitmap, paint, handleFile, status, it)
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

    val realWidth = pBitmap.width * (1 + 0.01f * (index - 60 + 1))
    val realHeight = pBitmap.height * (1 + 0.01f * (index - 60 + 1))
    val preBitmap = Bitmap.createScaledBitmap(pBitmap, realWidth.toInt(), realHeight.toInt(), true)
    var outputBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(outputBitmap)
    canvas.drawBitmap(preBitmap, (1080 - realWidth) / 2f, (1920 - realHeight) / 2f, paint)
    canvas.drawColor(Color.parseColor("#1132cd32"))
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

    var bgBitmap = BitmapFactory.decodeResource(
        UtilsContextManager.getInstance().application.resources,
        R.mipmap.imgnew1
    )
    bgBitmap = Bitmap.createScaledBitmap(bgBitmap, 1080, 1920, true)
    canvas.drawBitmap(bgBitmap!!, 0f, 0f, paint)

    val preAlpha = 255 - calculateCos(index + 1, 5, 255f)
    if (preAlpha > 0) {
        paint.alpha = preAlpha.toInt()
        canvas.drawBitmap(preBitmap, 0f, 0f, paint)
    }

    paint.alpha = 255
    val realWidth = calculateSin(index + 1, 30, 1080f).toInt()
    val realHeight = calculateSin(index + 1, 30, 1920f).toInt()
    if (realWidth > 0 && realHeight > 0) {
        var realBitmap = Bitmap.createScaledBitmap(curBitmap, realWidth, realHeight, true)
        var radius = 99 - calculatex2(index + 1, 30, 99f).toInt()
        if (radius % 2 == 0)
            radius -= 1

        if (radius <= 0)
            radius = 1


        val blurBitmap = blur(realBitmap, radius)
        canvas.drawBitmap(blurBitmap, (1080 - realWidth) / 2f, (1920 - realHeight) / 2f, paint)
    }
    canvas.drawColor(Color.parseColor("#1132cd32"))
    saveBitmapToFile(outputBitmap, handleFile, status)
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
fun blur(bitmap: Bitmap, radius: Int = 199): Bitmap {
    val srcMat = Mat(bitmap.height, bitmap.width, CvType.CV_8UC4)
    Utils.bitmapToMat(bitmap, srcMat)

    Imgproc.GaussianBlur(srcMat, srcMat, Size(radius.toDouble(), radius.toDouble()), 0.0)

    val resultBitmap = Bitmap.createBitmap(srcMat.cols(), srcMat.rows(), Bitmap.Config.ARGB_8888)
    Utils.matToBitmap(srcMat, resultBitmap)
    srcMat.release()
    return resultBitmap
}


fun blurPartial(bitmap: Bitmap, rect: Rect, radius: Int = 87): Bitmap {
    val srcMat = Mat(bitmap.height, bitmap.width, CvType.CV_8UC4)
    Utils.bitmapToMat(bitmap, srcMat)

    // 局部模糊处理
    val roiMat = srcMat.submat(rect)
    Imgproc.GaussianBlur(roiMat, roiMat, Size(radius.toDouble(), radius.toDouble()), 0.0)

    // 混合处理（可选羽化边缘）
    val resultBitmap = Bitmap.createBitmap(srcMat.cols(), srcMat.rows(), Bitmap.Config.ARGB_8888)
    Utils.matToBitmap(srcMat, resultBitmap)
    srcMat.release()
    return resultBitmap
}