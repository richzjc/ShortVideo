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
import com.richzjc.shortvideo.fragment.autoVideo.fangan.interpreter.calculatex2
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
    val blurBg :Bitmap = blur(curBitmap)
    (0 until 60)?.forEach {
        if (handleFile.listFiles().size < totalCount) {
            if (it < 30) {
                fangan1Small30(blurBg,preBitmap, curBitmap, paint, handleFile, status, it)
            } else {
                fang1Large30(curBitmap, paint, handleFile, status, it)
            }
        }
    }
}

private suspend fun fang1Large30(
    curBitmap: Bitmap,
    paint: Paint,
    file1: File,
    status: TextView?,
    index: Int
) {
    delay(30)
    paint.alpha = 255

    var outputBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(outputBitmap)
    val gap = 0.08f/30
    val realWidth = 1080 + (1080 * gap * (index - 30 + 1))
    val realHeight = 1920 + (1920 * gap * (index - 30 + 1))
    val realBitmap = Bitmap.createScaledBitmap(curBitmap, realWidth.toInt(), realHeight.toInt(), true)
    canvas.drawBitmap(realBitmap, (1080 - realWidth)/2f, (1920 - realHeight), paint)
    canvas.drawColor(Color.parseColor("#0a32cd32"))
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

    var progress = (index + 1) / 30f
    var alpha = (255 * progress)
    if (alpha > 255)
        alpha = 255f
    paint.alpha = alpha.toInt()
    canvas.drawBitmap(curBitmap, 0f, 0f, paint)

    alpha = 255 - (255 * progress)
    if (alpha < 0)
        alpha = 0f
    paint.alpha = alpha.toInt()
    val realWidth = 1080 - 1080 * progress
    val realHeight = 1920 - 1920 * progress
    if (realWidth > 0 && realHeight > 0) {
        var blurValue = calculatex2(index + 1, 30, 199f).toInt() + 30
        if (blurValue % 2 == 0)
            blurValue += 1
        val pmp = blur(preBitmap, blurValue)
        canvas.drawBitmap(pmp, (1080 - realWidth) / 2, (1920 - realHeight) / 2, paint)
    }
    canvas.drawColor(Color.parseColor("#0a32cd32"))
    saveBitmapToFile(outputBitmap, handleFile, status)
}