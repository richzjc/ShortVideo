package com.richzjc.shortvideo.fragment.autoVideo.fangan

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.widget.TextView
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
    (0 until 60)?.forEach {
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
    val widthGap = (pBitmap.width * 0.1f) / 30f
    val heightGap = (pBitmap.height * 0.1f) / 30f

    val realWidth = pBitmap.width * 1.1f - widthGap * (index - 30 + 1)
    val realHeight = pBitmap.height * 1.1f - heightGap * (index - 30 + 1)
    val preBitmap = Bitmap.createScaledBitmap(pBitmap, realWidth.toInt(), realHeight.toInt(), true)


    var outputBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(outputBitmap)
    canvas.drawBitmap(preBitmap, (1080 - realWidth) / 2f, (1920 - realHeight) / 2f, paint)
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
    val progress = (index + 1) / 30f
    val matrix = Matrix()
    val degrees = 360f * progress
    val scale = 0.2f + progress * 0.9f
    matrix.apply {
        reset()
        postScale(scale, scale, 1080 / 2f, 1920 / 2f)
        postRotate(degrees, 1080 / 2f, 1920 / 2f)
    }

    // 绘制动态图
    canvas.drawBitmap(curBitmap, matrix, null)
    saveBitmapToFile(outputBitmap, handleFile, status)
}