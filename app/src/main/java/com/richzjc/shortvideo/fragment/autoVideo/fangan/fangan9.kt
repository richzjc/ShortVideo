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
suspend fun fangan9(
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

    val realWidth = 1080 + 108 - calculateCos(index - 60 + 1, 60, 108f).toInt()
    val realHeight = 1920 + 192 - calculateCos(index - 60 + 1, 60, 192f).toInt()
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
    val realWidth = calculateSin(index + 1, 60, 1080f).toInt()
    val realHeight = calculateSin(index + 1, 60, 1920f).toInt()
    if (realWidth > 0 && realHeight > 0) {
        var realBitmap = Bitmap.createScaledBitmap(curBitmap, realWidth, realHeight, true)
        var radius = 55 - calculatex2(index + 1, 60, 55f).toInt()
        if (radius % 2 == 0)
            radius -= 1

        if (radius <= 0)
            radius = 1


        val blurBitmap = blur(realBitmap, radius)
        val roundRadius =
            realWidth / 2 - calculatex2(index + 1, 60, realWidth.toFloat() / 2).toInt()
        val roundBitmap = getRoundedCornerBitmap(blurBitmap, roundRadius.toFloat())
        canvas.drawBitmap(roundBitmap, (1080 - realWidth) / 2f, (1920 - realHeight) / 2f, paint)
    }

    if (index < 45) {
        val bitmap0 =
            Bitmap.createBitmap(
                preBitmap,
                0,
                0,
                preBitmap.width / 2,
                preBitmap.height / 2,
                null,
                true
            )
        val bitmap1 = Bitmap.createBitmap(
            preBitmap,
            preBitmap.width / 2,
            0,
            preBitmap.width / 2,
            preBitmap.height / 2,
            null,
            true
        )
        val bitmap2 = Bitmap.createBitmap(
            preBitmap,
            0,
            preBitmap.height / 2,
            preBitmap.width / 2,
            preBitmap.height / 2,
            null,
            true
        )
        val bitmap3 = Bitmap.createBitmap(
            preBitmap,
            preBitmap.width / 2,
            preBitmap.height / 2,
            preBitmap.width / 2,
            preBitmap.height / 2,
            null,
            true
        )

        val realWidth = calculateCos((index + 1), 45, preBitmap.width / 2f).toInt()
        val realHeight = calculateCos((index + 1), 45, preBitmap.height / 2f).toInt()
        if (realWidth > 0 && realHeight > 0) {
            val roundRadius = calculatex2(index + 1, 45, ScreenUtils.dip2px(20f).toFloat()).toInt()
            var roundBitmap0 = getRoundedCornerBitmap(bitmap0, roundRadius.toFloat())
            var roundBitmap1 = getRoundedCornerBitmap(bitmap1, roundRadius.toFloat())
            var roundBitmap2 = getRoundedCornerBitmap(bitmap2, roundRadius.toFloat())
            var roundBitmap3 = getRoundedCornerBitmap(bitmap3, roundRadius.toFloat())

            roundBitmap0 =
                Bitmap.createScaledBitmap(roundBitmap0, realWidth, realHeight, true)
            roundBitmap1 =
                Bitmap.createScaledBitmap(roundBitmap1, realWidth, realHeight, true)
            roundBitmap2 =
                Bitmap.createScaledBitmap(roundBitmap2, realWidth, realHeight, true)
            roundBitmap3 =
                Bitmap.createScaledBitmap(roundBitmap3, realWidth, realHeight, true)

            canvas.drawBitmap(roundBitmap0, 0f, 0f, paint)
            canvas.drawBitmap(roundBitmap1, 1080f - realWidth, 0f, paint)
            canvas.drawBitmap(roundBitmap2, 0f, 1920f - realHeight, paint)
            canvas.drawBitmap(roundBitmap3, 1080f - realWidth, 1920f - realHeight, paint)
        }
    }

//    canvas.drawColor(Color.parseColor("#0a32cd32"))
    saveBitmapToFile(outputBitmap, handleFile, status)
}