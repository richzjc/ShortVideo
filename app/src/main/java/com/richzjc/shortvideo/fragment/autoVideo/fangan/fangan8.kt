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
import com.richzjc.shortvideo.fragment.autoVideo.fangan.interpreter.calculateCos
import com.richzjc.shortvideo.fragment.autoVideo.fangan.interpreter.calculateSin
import com.richzjc.shortvideo.fragment.autoVideo.fangan.interpreter.calculatex2
import com.richzjc.shortvideo.fragment.videoCreate.getRoundedCornerBitmap
import com.richzjc.shortvideo.util.ScreenUtils
import kotlinx.coroutines.delay
import java.io.File
import kotlin.math.max
import kotlin.math.min

/**
 * 透明度变换
 */
suspend fun fangan8(
    handleFile: File,
    preBitmap: Bitmap,
    curBitmap: Bitmap,
    status: TextView?,
    totalCount: Int,
    paint: Paint
) {
    delay(30)
    val blurBg: Bitmap = blur(curBitmap)
    (0 until 120)?.forEach {
        if (handleFile.listFiles().size < totalCount) {
            if (it < 60) {
                fangan1Small30(blurBg, preBitmap, curBitmap, paint, handleFile, status, it)
            } else {
                fang1Large30(curBitmap, paint, handleFile, status, it)
            }
        }
    }
}

private suspend fun fang1Large30(
    preBitmap: Bitmap,
    paint: Paint,
    file1: File,
    status: TextView?,
    index: Int
) {
    delay(30)
    paint.alpha = 255

    val realWidth = 1080 + 108 - calculateCos(index -60 + 1, 60, 108f).toInt()
    val realHeight = 1920 + 192 - calculateCos(index -60 + 1, 60, 192f).toInt()
    val preBitmap =
        Bitmap.createScaledBitmap(preBitmap, realWidth, realHeight, true)
    var outputBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(outputBitmap)
    canvas.drawBitmap(preBitmap, (1080 - realWidth) / 2f, (1920 - realHeight) / 2f, paint)
//    canvas.drawColor(Color.parseColor("#0a32cd32"))
    saveBitmapToFile(outputBitmap, file1, status)
}


private suspend fun fangan1Small30(
    blurBg: Bitmap,
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

    paint.alpha = 255
    val rate = 1 - calculateSin((index + 1), 90, 0.1f)


    var progress = (index + 1) / 90f
    var realWidth = 1080 * rate
    var realHeight = 1920 * rate
    var bitmap  = Bitmap.createScaledBitmap(preBitmap, realWidth.toInt(), realHeight.toInt(), true)
    val maxRadius = ScreenUtils.dip2px(20f)
    val roundRadius = maxRadius - calculatex2(index + 1, 60, maxRadius.toFloat()).toInt()
    var roundBitmap = getRoundedCornerBitmap(bitmap, roundRadius.toFloat())
    canvas.drawBitmap(roundBitmap, -realWidth *progress, (1920 - realHeight) / 2f, paint)

    realWidth = 1080 - 108 + 1080 * calculateSin((index + 1), 60, 0.1f)
    realHeight = 1920 - 192 + 1920 * calculateSin((index + 1), 60, 0.1f)

    if (realWidth > 0 && realHeight > 0) {
        var realBitmap = Bitmap.createScaledBitmap(curBitmap, realWidth.toInt(), realHeight.toInt(), true)
        var radius = 55 - calculatex2(index + 1, 60, 55f).toInt()
        if (radius % 2 == 0)
            radius -= 1

        if (radius <= 0)
            radius = 1

        var xLocate = calculateCos(index + 1, 60, 1080f).toInt()
        val blurBitmap = blur(realBitmap, radius)
        val maxRadius = ScreenUtils.dip2px(20f)
        val roundRadius = maxRadius - calculatex2(index + 1, 60, maxRadius.toFloat()).toInt()
        val roundBitmap = getRoundedCornerBitmap(blurBitmap, roundRadius.toFloat())
        canvas.drawBitmap(roundBitmap, xLocate.toFloat(), (1920 - realHeight) / 2f, paint)
    }
//    canvas.drawColor(Color.parseColor("#0a32cd32"))
    saveBitmapToFile(outputBitmap, handleFile, status)

}