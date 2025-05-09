package com.richzjc.shortvideo.fragment.autoVideo.fangan

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.widget.TextView
import com.richzjc.shortvideo.fragment.autoVideo.fangan.interpreter.calculateCos
import com.richzjc.shortvideo.fragment.autoVideo.fangan.interpreter.calculateSin
import com.richzjc.shortvideo.fragment.autoVideo.fangan.interpreter.calculatex2
import com.richzjc.shortvideo.fragment.videoCreate.getRoundedCornerBitmap
import com.richzjc.shortvideo.util.ScreenUtils
import kotlinx.coroutines.delay
import java.io.File

/**
 * 透明度变换
 */
suspend fun fangan2(
    handleFile: File,
    preBitmap: Bitmap,
    curBitmap: Bitmap,
    status: TextView?,
    totalCount: Int,
    paint: Paint
) {
    delay(30)
    val blurBg :Bitmap = blur(curBitmap)
    (0 until 120)?.forEach {
        if (handleFile.listFiles().size < totalCount) {
            if (it < 60) {
                fangan1Small30(blurBg,  preBitmap, curBitmap, paint, handleFile, status, it)
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
    val widthGap = (pBitmap.width * 0.1f) / 60f
    val heightGap = (pBitmap.height * 0.1f) / 60f

    val realWidth = pBitmap.width * 1.1f - widthGap * (index - 60 + 1)
    val realHeight = pBitmap.height * 1.1f - heightGap * (index - 60 + 1)
    val preBitmap = Bitmap.createScaledBitmap(pBitmap, realWidth.toInt(), realHeight.toInt(), true)


    var outputBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(outputBitmap)
    canvas.drawBitmap(preBitmap, (1080 - realWidth) / 2f, (1920 - realHeight)/2, paint)
//    canvas.drawColor(Color.parseColor("#0a32cd32"))
    saveBitmapToFile(outputBitmap, file1, status)
}


private suspend fun fangan1Small30(
    blurBg : Bitmap,
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

    var blurBitmap = Bitmap.createScaledBitmap(blurBg, 1080, 1920, true)
    canvas.drawBitmap(blurBitmap!!, 0f, 0f, paint)

    if (index < 5) {
        val preAlpha = calculateCos(index + 1, 5, 255f)
        if (preAlpha > 0) {
            paint.alpha = preAlpha.toInt()
            canvas.drawBitmap(preBitmap, 0f, 0f, paint)
        }
    }

    paint.alpha = 255
    val matrix = Matrix()
    val degrees = calculateSin(index + 1, 60, 360f)
    val scale = 0.2f + calculateSin(index + 1, 60, 0.9f)
    matrix.apply {
        reset()
        postScale(scale, scale, 1080 / 2f, 1920 / 2f)
        postRotate(degrees, 1080 / 2f, 1920 / 2f)
    }

    // 绘制动态图
    var radius = 55 - calculatex2(index + 1, 60, 55f).toInt()
    if (radius % 2 == 0)
        radius -= 1

    if (radius <= 0)
        radius = 1

    val rbitmap = blur(curBitmap, radius)
    val maxRadius = ScreenUtils.dip2px(20f)
    val roundRadius = maxRadius - calculatex2(index + 1, 60, maxRadius.toFloat()).toInt()
    val roundBitmap = getRoundedCornerBitmap(rbitmap, roundRadius.toFloat())
    canvas.drawBitmap(roundBitmap, matrix, null)
//    canvas.drawColor(Color.parseColor("#0a32cd32"))
    saveBitmapToFile(outputBitmap, handleFile, status)
}