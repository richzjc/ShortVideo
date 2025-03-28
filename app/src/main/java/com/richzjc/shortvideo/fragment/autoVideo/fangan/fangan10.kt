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
suspend fun fangan10(
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
                fang1Large30(paint, handleFile, status, it)
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
): Bitmap {
    delay(30)
    paint.alpha = 255

    var outputBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(outputBitmap)
    val gap = 0.05f / 30f
    val realWidth = preBitmap.width * (1 + gap)
    val realHeight = preBitmap.height * (1 + gap)

    var preBitmap1 = Bitmap.createScaledBitmap(preBitmap, realWidth.toInt(), realHeight.toInt(), true)

    canvas.drawBitmap(preBitmap1, (1080 - realWidth) / 2f, (1920 - realHeight), paint)

    var resultBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
    val resultCanvas = Canvas(resultBitmap)
    resultCanvas.drawBitmap(outputBitmap, 0f, 0f, paint)

    saveBitmapToFile(outputBitmap, file1, status)
    return resultBitmap!!
}

private suspend fun fangan1Small30(
    preBitmap: Bitmap,
    curBitmap: Bitmap,
    paint: Paint,
    handleFile: File,
    status: TextView?,
    index: Int
){
    delay(30)
    paint.alpha = 255
    var outputBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(outputBitmap)

    var blurBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
    val blurCanvas = Canvas(blurBitmap)
    blurCanvas.drawBitmap(preBitmap, 0f, 0f, paint)
    blurBitmap = blur(blurBitmap)
    canvas.drawBitmap(blurBitmap, 0f, 0f, paint)

    var progress = (index + 1) / 30f


    var realWidth = (1080 / 2f) * progress
    var realHeight = (1920 / 2f) * progress
    if (realWidth > 0 && realHeight > 0) {
        var w = (1080 / 2f)
        var h = (1920 / 2f)
        val realBitmap =
            Bitmap.createScaledBitmap(curBitmap, realWidth.toInt(), realHeight.toInt(), true)
        canvas.drawBitmap(realBitmap, (w - realWidth) / 2, (h - realHeight) / 2, paint)
        canvas.drawBitmap(realBitmap, w + (w - realWidth) / 2, (h - realHeight) / 2, paint)
        canvas.drawBitmap(realBitmap, (w - realWidth) / 2, h + (h - realHeight) / 2, paint)
        canvas.drawBitmap(realBitmap, w + (w - realWidth) / 2, h + (h - realHeight) / 2, paint)
    }

    realWidth = (1080) * progress
    realHeight = (1920) * progress
    if (realWidth > 0 && realHeight > 0) {
        var w = 1080
        var h = 1920
        val realBitmap =
            Bitmap.createScaledBitmap(curBitmap, realWidth.toInt(), realHeight.toInt(), true)
        canvas.drawBitmap(realBitmap, (w - realWidth) / 2, (h - realHeight) / 2, paint)
    }

    if (index < 10) {
        val alpha = 255 - 25.5 * (index + 1)
        if (alpha >= 0) {
            paint.alpha = alpha.toInt()
            canvas.drawBitmap(preBitmap, 0f, 0f, paint)
        }
    }
    saveBitmapToFile(outputBitmap, handleFile, status)
}