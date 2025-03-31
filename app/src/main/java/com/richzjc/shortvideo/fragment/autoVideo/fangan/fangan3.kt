package com.richzjc.shortvideo.fragment.autoVideo.fangan

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.widget.TextView
import com.richzjc.shortvideo.fragment.autoVideo.fangan.interpreter.calculateSin
import com.richzjc.shortvideo.fragment.autoVideo.fangan.interpreter.calculatex2
import kotlinx.coroutines.delay
import java.io.File

/**
 * 透明度变换
 */
suspend fun fangan3(
    handleFile: File,
    preBitmap: Bitmap,
    curBitmap: Bitmap,
    status: TextView?,
    totalCount: Int,
    paint: Paint
) {
    delay(30)
    val blurBg :Bitmap = blur(curBitmap)
    val preBitmapList = ArrayList<Bitmap>()
    val height = preBitmap.height / 4
    val bitmap1 = Bitmap.createBitmap(preBitmap, 0, 0, preBitmap.width, height, null, false)
    val bitmap2 = Bitmap.createBitmap(preBitmap, 0, height, preBitmap.width, height, null, false)
    val bitmap3 =
        Bitmap.createBitmap(preBitmap, 0, height * 2, preBitmap.width, height, null, false)
    val bitmap4 =
        Bitmap.createBitmap(preBitmap, 0, height * 3, preBitmap.width, height, null, false)

    preBitmapList.add(bitmap1)
    preBitmapList.add(bitmap2)
    preBitmapList.add(bitmap3)
    preBitmapList.add(bitmap4)

    (0 until 60)?.forEach {
        if (handleFile.listFiles().size < totalCount) {
            if (it < 30) {
                fangan1Small30(blurBg,preBitmapList, curBitmap, paint, handleFile, status, it)
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

    val realWidth = pBitmap.width * 1.05f - widthGap * (index - 30 + 1)
    val realHeight = pBitmap.height * 1.05f - heightGap * (index - 30 + 1)
    val preBitmap = Bitmap.createScaledBitmap(pBitmap, realWidth.toInt(), realHeight.toInt(), true)

    var outputBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(outputBitmap)
    canvas.drawBitmap(preBitmap, (1080 - realWidth) / 2f, (1920 - realHeight) / 2f, paint)
    canvas.drawColor(Color.parseColor("#1132cd32"))
    saveBitmapToFile(outputBitmap, file1, status)
}


private suspend fun fangan1Small30(
    blurBg : Bitmap,
    preBitmapList: List<Bitmap>,
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


    if (index < 15) {
        var alpha = (index + 1) * (255 / 15f)
        if (alpha > 255)
            alpha = 255f
        paint.alpha = alpha.toInt()
    } else {
        paint.alpha = 255
    }

    val widthGap1 = (1080 * 0.05f) / 30f
    val heightGap1 = (1920 * 0.05f) / 30f

    val realWidth = 1080 + widthGap1 * (index + 1)
    val realHeight = 1920 + heightGap1 * (index + 1)
    val preBitmap =
        Bitmap.createScaledBitmap(curBitmap, realWidth.toInt(), realHeight.toInt(), true)
    canvas.drawBitmap(preBitmap, (1080 - realWidth) / 2f, (1920 - realHeight) / 2f, paint)

    val widthGap = 1080 / 30f
    val heightGap = 1920 / 4f
    paint.alpha = 255
    var blurValue: Int
    if (index < 5) {
        blurValue = calculatex2(index + 1, 5, 99f).toInt()
    } else {
        blurValue = 99
    }
    if (blurValue % 2 == 0)
        blurValue += 1

    preBitmapList.forEachIndexed { innerIndex, bitmap ->
        val bmp = blur(bitmap, blurValue)
        if (innerIndex % 2 == 0) {
            val startX = (index + 1) * widthGap
            canvas.drawBitmap(bmp, startX, innerIndex * heightGap, paint)
        } else {
            val startX = -(index + 1) * widthGap
            canvas.drawBitmap(bmp, startX, innerIndex * heightGap, paint)
        }
    }
    canvas.drawColor(Color.parseColor("#1132cd32"))
    saveBitmapToFile(outputBitmap, handleFile, status)
}