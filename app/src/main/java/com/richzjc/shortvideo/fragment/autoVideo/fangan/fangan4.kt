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
suspend fun fangan4(
    handleFile: File,
    preBitmap: Bitmap,
    curBitmap: Bitmap,
    status: TextView?,
    totalCount: Int,
    paint: Paint
) {
    delay(30)
    var blurBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
    val blurCanvas = Canvas(blurBitmap)
    blurCanvas.drawBitmap(curBitmap, 0f, 0f, paint)
    blurBitmap = blur(blurBitmap)

    (0 until 60)?.forEach {
        if (handleFile.listFiles().size < totalCount) {
            if (it < 30) {
                fangan1Small30(preBitmap, curBitmap, blurBitmap, paint, handleFile, status, it)
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
    val widthGap = (pBitmap.width * 0.05f) / 30f
    val heightGap = (pBitmap.height * 0.05f) / 30f
    if(index - 30 < 5){
        val realWidth = 1080 + widthGap * (index - 30 + 1)
        val realHeight = 1920 + heightGap * (index - 30 + 1)
        val preBitmap = Bitmap.createScaledBitmap(pBitmap, realWidth.toInt(), realHeight.toInt(), true)
        var outputBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outputBitmap)
        canvas.drawBitmap(preBitmap, (1080 - realWidth) / 2f, (1920 - realHeight) / 2f, paint)
        saveBitmapToFile(outputBitmap, file1, status)
    }else if(index - 30 < 15){
        val realWidth = 1080 + widthGap * 5 - widthGap * (index - 35 + 1)
        val realHeight = 1920 + heightGap * 5 - heightGap * (index - 35 + 1)
        val preBitmap = Bitmap.createScaledBitmap(pBitmap, realWidth.toInt(), realHeight.toInt(), true)
        var outputBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outputBitmap)
        canvas.drawBitmap(preBitmap, (1080 - realWidth) / 2f, (1920 - realHeight) / 2f, paint)
        saveBitmapToFile(outputBitmap, file1, status)
    }else if(index - 30 < 25){
        val realWidth = 1080 - widthGap * 5 + widthGap * (index - 45 + 1)
        val realHeight = 1920 - heightGap * 5 + heightGap * (index - 45 + 1)
        val preBitmap = Bitmap.createScaledBitmap(pBitmap, realWidth.toInt(), realHeight.toInt(), true)
        var outputBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outputBitmap)
        canvas.drawBitmap(preBitmap, (1080 - realWidth) / 2f, (1920 - realHeight) / 2f, paint)
        saveBitmapToFile(outputBitmap, file1, status)
    }else{
        val realWidth = 1080 + widthGap * 5 - widthGap * (index - 55 + 1)
        val realHeight = 1920 + heightGap * 5 - heightGap * (index - 55 + 1)
        val preBitmap = Bitmap.createScaledBitmap(pBitmap, realWidth.toInt(), realHeight.toInt(), true)
        var outputBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outputBitmap)
        canvas.drawBitmap(preBitmap, (1080 - realWidth) / 2f, (1920 - realHeight) / 2f, paint)
        saveBitmapToFile(outputBitmap, file1, status)
    }
}


private suspend fun fangan1Small30(
    preBitmap: Bitmap,
    curBitmap: Bitmap,
    blurBitmap: Bitmap,
    paint: Paint,
    handleFile: File,
    status: TextView?,
    index: Int
) {
    delay(30)
    paint.alpha = 255
    var outputBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(outputBitmap)

    canvas.drawBitmap(blurBitmap, 0f, 0f, paint)

    val widthGap = (1080 / 30f)
    val heightGap = (1920 / 30f)

    var alpha = 255 - (255 / 15f) * (index + 1)
    if (alpha >= 0) {
        paint.alpha = alpha.toInt()
        val realWidth = 1080 - (index + 1) * widthGap
        val realHeight = 1920 - (index + 1) * heightGap
        val realBitmap =
            Bitmap.createScaledBitmap(preBitmap, realWidth.toInt(), realHeight.toInt(), true)
        canvas.drawBitmap(
            realBitmap,
            (1080 - realWidth) / 2f,
            (1080 - realHeight - (index + 1) * heightGap),
            paint
        )
    }

    paint.alpha = 255
    canvas.drawBitmap(curBitmap, 0f, (1920 - (index + 1) * heightGap), paint)




    saveBitmapToFile(outputBitmap, handleFile, status)
}