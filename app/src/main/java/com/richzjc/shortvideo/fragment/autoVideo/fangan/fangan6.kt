package com.richzjc.shortvideo.fragment.autoVideo.fangan

import android.graphics.Bitmap
import android.graphics.Camera
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.widget.TextView
import kotlinx.coroutines.delay
import java.io.File
import kotlin.math.max

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
    paint.apply {
        isAntiAlias = true
        xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT) // 圆形裁剪模式
    }


    paint.alpha = 255
    var outputBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(outputBitmap)

    var blurBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
    val blurCanvas = Canvas(blurBitmap)
    blurCanvas.drawBitmap(curBitmap, 0f, 0f, paint)
    blurBitmap = blur(blurBitmap)
    canvas.drawBitmap(blurBitmap, 0f, 0f, paint)

    val progress = (index + 1) / 30f
    // 绘制背景图
    canvas.drawBitmap(preBitmap, 0f, 0f, paint)

    val maxRadius = 1920 / 2f
    val currentRadius = maxRadius * progress

    // 创建圆形遮罩层
    val maskBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
    val maskCanvas = Canvas(maskBitmap)
    maskCanvas.drawCircle(1080/2f, 1920/2f, currentRadius, Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    })

    // 应用遮罩绘制覆盖图
    canvas.drawBitmap(curBitmap, 0f, 0f, null)
    canvas.drawBitmap(maskBitmap, 0f, 0f, paint)

    saveBitmapToFile(outputBitmap, handleFile, status)
    paint.xfermode = null
}