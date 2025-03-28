package com.richzjc.shortvideo.fragment.autoVideo.fangan

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Camera
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Shader
import android.widget.TextView
import kotlinx.coroutines.delay
import java.io.File
import kotlin.math.max
import kotlin.math.min

/**
 * 透明度变换
 */
suspend fun fangan7(
    handleFile: File,
    preBitmap: Bitmap,
    curBitmap: Bitmap,
    status: TextView?,
    totalCount: Int,
    paint: Paint
) {
    delay(30)
    var lastBitMap: Bitmap? = null
    (0 until 60)?.forEach {
        if (handleFile.listFiles().size < totalCount) {
            if (it < 30) {
                lastBitMap = fangan1Small30(preBitmap, curBitmap, paint, handleFile, status, it)
            } else {
                lastBitMap = fang1Large30(lastBitMap!!, paint, handleFile, status, it)
            }
        }
    }
}

private suspend fun fang1Large30(
    preBitmap : Bitmap,
    paint: Paint,
    file1: File,
    status: TextView?,
    index: Int
) : Bitmap{
    delay(30)
    paint.alpha = 255


    var outputBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(outputBitmap)
    val gap = 0.05f / 30f
    val realWidth = preBitmap.width * (1 + gap)
    val realHeight = preBitmap.height * (1 + gap)

    val preBitmap1 = Bitmap.createScaledBitmap(preBitmap, realWidth.toInt(), realHeight.toInt(), true)

    canvas.drawBitmap(preBitmap1, (1080 - realWidth) / 2f, (1920 - realHeight), paint)

    var lastBitmap: Bitmap? = null
    var resultBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
    val resultCanvas = Canvas(resultBitmap)
    resultCanvas.drawBitmap(outputBitmap, 0f, 0f, paint)

    canvasDrawText(canvas, paint, file1)
    saveBitmapToFile(outputBitmap, file1, status)

    return lastBitmap!!
}

private suspend fun fangan1Small30(
    preBitmap: Bitmap,
    curBitmap: Bitmap,
    paint: Paint,
    handleFile: File,
    status: TextView?,
    index: Int
) : Bitmap?{
    delay(30)
    paint.alpha = 255
    var outputBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(outputBitmap)

    var blurBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
    val blurCanvas = Canvas(blurBitmap)
    blurCanvas.drawBitmap(curBitmap, 0f, 0f, paint)
    blurBitmap = blur(blurBitmap)
    canvas.drawBitmap(blurBitmap, 0f, 0f, paint)

    paint.alpha = 255
    val widthGap = 1080 / 30f
    val heightGap = 1920 / 30f
    val realWidth = (widthGap * index).toInt()
    val realHeight = (heightGap * index).toInt()
    if (realWidth > 0 && realHeight > 0) {
        val realBitmap = Bitmap.createScaledBitmap(curBitmap, realWidth, realHeight, true)
        canvas.drawBitmap(realBitmap, (1080 - realWidth) / 2f, (1920 - realHeight) / 2f, paint)
    }

    if (index < 20) {
        val progress = (index + 1) / 20f
        val realWidth = 1080 - (1080 * 0.1f) * progress
        val realHeight = 1920 - (1920 * 0.1f) * progress
        val realBitmap = Bitmap.createScaledBitmap(preBitmap, realWidth.toInt(), realHeight.toInt(), true)
        canvas.drawBitmap(realBitmap, -progress * realWidth, (1920 - realHeight)/2, paint)
        canvas.drawBitmap(realBitmap, progress * realWidth, (1920 - realHeight)/2, paint)
    }
    var lastBitmap: Bitmap? = null
    if (index >= 29) {
        var resultBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
        val resultCanvas = Canvas(resultBitmap)
        resultCanvas.drawBitmap(outputBitmap, 0f, 0f, paint)
    }

    canvasDrawText(canvas, paint, handleFile)
    saveBitmapToFile(outputBitmap, handleFile, status)
    return lastBitmap
}