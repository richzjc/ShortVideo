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
import kotlinx.coroutines.delay
import java.io.File
import kotlin.math.max
import kotlin.math.min

/**
 * 透明度变换
 */
suspend fun fangan6(
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
                fangan1Small30(
                    blurBg,
                    preBitmap,
                    curBitmap,
                    paint,
                    handleFile,
                    status,
                    it
                )
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
        Bitmap.createScaledBitmap(preBitmap, realWidth.toInt(), realHeight.toInt(), true)
    var outputBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(outputBitmap)
    canvas.drawBitmap(preBitmap, (1080 - realWidth) / 2f, (1920 - realHeight) / 2f, paint)
    canvas.drawColor(Color.parseColor("#1132cd32"))
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

    val realWidth = 1080 + calculateSin(index + 1, 60, 108f).toInt()
    val realHeight = 1920 + calculateSin(index + 1, 60, 192f).toInt()
    var realBitmap = Bitmap.createScaledBitmap(preBitmap, realWidth, realHeight, true)
    // 绘制背景
    canvas.drawBitmap(realBitmap, 0f, 0f, paint)


    val centerX = 1080 / 2f
    val centerY = 1920 / 2f
    val maxRadius = 1920 / 2f

    // 计算当前半径
    val currentRadius = calculateSin(index + 1, 60, maxRadius)

    val degrees = calculateSin(index + 1, 60, 360f)

    // 绘制动态图
    var radius = 99 - calculatex2(index + 1, 60, 99f).toInt()
    if (radius % 2 == 0)
        radius -= 1

    if (radius <= 0)
        radius = 1

    val rbitmap = blur(curBitmap, radius)

    // 创建BitmapShader并设置缩放矩阵
    val shader = BitmapShader(rbitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    val matrix = Matrix().apply {
        // 将前景图居中缩放
        val scale = maxRadius * 2f / curBitmap.height
        setScale(scale, scale)
        postTranslate(
            centerX - curBitmap.width * scale / 2,
            centerY - curBitmap.height * scale / 2
        )

        postRotate(degrees, curBitmap.width * scale / 2, curBitmap.height * scale / 2)
    }
    shader.setLocalMatrix(matrix)

    // 绘制圆形前景
    val newPaint = Paint().apply {
        this.shader = shader
        isAntiAlias = true
    }
    canvas.drawCircle(centerX, centerY, currentRadius, newPaint)
    canvas.drawColor(Color.parseColor("#1132cd32"))
    saveBitmapToFile(outputBitmap, handleFile, status)
}