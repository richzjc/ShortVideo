package com.richzjc.shortvideo.fragment.autoVideo.fangan

import android.graphics.Bitmap
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
suspend fun fangan6(
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
                fangan1Small30(originSize, preBitmap, curBitmap, paint, handleFile, status, it)
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
    originSize : Int,
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

    if(originSize > 0){
        // 绘制背景
        canvas.drawBitmap(preBitmap, 0f, 0f, paint)

    }

    val progress = (index + 1) / 30f

    val centerX = 1080 / 2f
    val centerY = 1920 / 2f
    val maxRadius = 1920 / 2f

    // 计算当前半径
    val currentRadius = maxRadius * progress


    // 创建BitmapShader并设置缩放矩阵
    val shader = BitmapShader(curBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    val matrix = Matrix().apply {
        // 将前景图居中缩放
        val scale = maxRadius * 2f / min(curBitmap.width, curBitmap.height)
        setScale(scale, scale)
        postTranslate(
            centerX - curBitmap.width * scale / 2,
            centerY - curBitmap.height * scale / 2
        )
    }
    shader.setLocalMatrix(matrix)

    // 绘制圆形前景
    val newPaint = Paint().apply {
        this.shader = shader
        isAntiAlias = true
    }
    canvas.drawCircle(centerX, centerY, currentRadius, newPaint)
    saveBitmapToFile(outputBitmap, handleFile, status)
}