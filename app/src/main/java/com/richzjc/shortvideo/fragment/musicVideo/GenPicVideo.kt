package com.richzjc.shortvideo.fragment.musicVideo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Shader
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import com.richzjc.shortvideo.R
import com.richzjc.shortvideo.UtilsContextManager
import com.richzjc.shortvideo.fragment.videoCreate.updateStatusText
import com.richzjc.shortvideo.util.QDUtil
import com.richzjc.shortvideo.util.ScreenUtils
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import kotlin.math.min

fun musicVideoGenPic(
    context: Context,
    originPath: String?,
    handlePath: String?,
    statusTv: TextView?,
    handlePic: ImageView?,
    duration: Int,
    isHorizontal: Boolean
): List<Pair<String, Int>> {
    updateStatusText("开始生成图片", statusTv)
    //用来存储视频的配置文件
    val pairList = ArrayList<Pair<String, Int>>()
    //每一轮显示2秒
    var index = 0
    val height = 1080

    val outputFile = File(QDUtil.getShareImageCache(context).absolutePath, "musicVideo")

    if (!outputFile.exists())
        outputFile.mkdirs()

    val originBitmap = BitmapFactory.decodeFile(originPath)


//    var curBitmap = BitmapFactory.decodeFile(curPath)
//    var picHeight = height - ScreenUtils.dip2px(20f) * 2
//    var picWidth = (curBitmap.width * picHeight) / curBitmap.height
//    curBitmap = Bitmap.createScaledBitmap(curBitmap, picWidth, picHeight, true)
//    val curShuMiaoBitmap = responseToShuMiao(curBitmap)
//
//    var nextBitmap = BitmapFactory.decodeFile(nextPath)
//    picHeight = height - ScreenUtils.dip2px(20f) * 2
//    picWidth = (nextBitmap.width * picHeight) / nextBitmap.height
//    nextBitmap = Bitmap.createScaledBitmap(nextBitmap, picWidth, picHeight, true)
//    val nextShuMiaoBitmap = responseToShuMiao(nextBitmap)
//
//    //生成1.0
//    var bitmap = realGenPic(curShuMiaoBitmap, curBitmap, null, 1.0f, true)
//    if (index == 0) {
//        Handler(Looper.getMainLooper()).post {
//            handlePic?.setImageBitmap(bitmap)
//        }
//    }
//
//    // 获取应用的内部存储路径
//    val imageFile = File(outputFile, "${index}.png")
//    index++
//    // 创建文件输出流
//    val fos = FileOutputStream(imageFile)
//    // 将 Bitmap 压缩为 JPEG 格式并写入文件流
//    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
//    // 关闭文件流
//    fos.close()
//    var pair = Pair<String, Int>(imageFile.absolutePath, 90)
//    pairList.add(pair)
//
//
//    // 生成right
//    (0 until 30)?.forEach {
//        var scale = ((-1 * (it + 1) * (it + 1)) / 900.0f) + 1
//        var bitmap =
//            realGenPic(curShuMiaoBitmap, nextBitmap, curBitmap, scale, false)
//
//        // 获取应用的内部存储路径
//        val imageFile = File(outputFile, "${index}.png")
//        index++
//        // 创建文件输出流
//        val fos = FileOutputStream(imageFile)
//        // 将 Bitmap 压缩为 JPEG 格式并写入文件流
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
//        // 关闭文件流
//        fos.close()
//        var pair = Pair<String, Int>(imageFile.absolutePath, 1)
//        pairList.add(pair)
//    }
//
//    //生成left
//    (0 until 30)?.forEach {
//        var scale = ((-1 * ((30 - it - 1)) * (30 - it - 1)) / 900.0f) + 1
//        var bitmap =
//            realGenPic(curShuMiaoBitmap, nextBitmap, nextShuMiaoBitmap, scale, true)
//
//        // 获取应用的内部存储路径
//        val imageFile = File(outputFile, "${index}.png")
//        index++
//        // 创建文件输出流
//        val fos = FileOutputStream(imageFile)
//        // 将 Bitmap 压缩为 JPEG 格式并写入文件流
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
//        // 关闭文件流
//        fos.close()
//        var pair = Pair<String, Int>(imageFile.absolutePath, 1)
//        pairList.add(pair)
//    }

    updateStatusText("生成图片结束", statusTv)
    return pairList
}

//
//private fun getChildBitMap(isLeft: Boolean, scaleX: Float, picBitMap: Bitmap): Bitmap {
//    val width = 960
//    val height = 1080
//
//    val combinedBitmap =
//        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//    val canvas = Canvas(combinedBitmap)
//    val paint = Paint()
//    paint.isAntiAlias = true
//    var bgBmp = BitmapFactory.decodeResource(
//        UtilsContextManager.getInstance().application.resources,
//        R.mipmap.img
//    )
//    bgBmp = Bitmap.createScaledBitmap(bgBmp, width, height, true)
//    canvas.drawBitmap(bgBmp, 0f, 0f, paint)
//
//    if (isLeft) {
//        canvas.drawBitmap(
//            picBitMap,
//            (width / 2 - picBitMap.width / 2).toFloat(),
//            ScreenUtils.dip2px(10f).toFloat(),
//            paint
//        )
//
//        // 初始化渐变效果
//        val shadowWidth = ScreenUtils.dip2px(50f)
//        val gradient = LinearGradient(
//            (width - shadowWidth).toFloat(), 0f, width.toFloat(), 0f,
//            0x00000000, 0xFF88000000.toInt(),
//            Shader.TileMode.CLAMP
//        )
//        paint.shader = gradient
//        canvas.drawRect(
//            (width - shadowWidth).toFloat(),
//            0f,
//            width.toFloat(),
//            height.toFloat(),
//            paint
//        )
//    } else {
//        canvas.drawBitmap(
//            picBitMap,
//            (width / 2 - picBitMap.width / 2).toFloat(),
//            ScreenUtils.dip2px(10f).toFloat(),
//            paint
//        )
//
//        // 初始化渐变效果
//        val shadowWidth = ScreenUtils.dip2px(50f)
//        val gradient = LinearGradient(
//            0f, 0f, shadowWidth.toFloat(), 0f,
//            0xFF88000000.toInt(), 0x00000000,
//            Shader.TileMode.CLAMP
//        )
//        paint.shader = gradient
//
//
//        canvas.drawRect(0f, 0f, shadowWidth.toFloat(), height.toFloat(), paint)
//    }
//
//    if (scaleX != 1.0f) {
//        var realWidth = combinedBitmap.width * scaleX
//        if (realWidth <= 0)
//            realWidth = 1f
//        return Bitmap.createScaledBitmap(
//            combinedBitmap,
//            realWidth.toInt(),
//            combinedBitmap.height,
//            true
//        )
//    } else {
//        return combinedBitmap
//    }
//}

//private fun realGenPic(
//    bitmap: Bitmap
//): Bitmap {
//    val width = 1920
//    val height = 1080
//    val combinedBitmap =
//        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//    val canvas = Canvas(combinedBitmap)
//
//    val paint = Paint()
//    //绘制背景
//    paint.isAntiAlias = true
//
//    canvas.drawBitmap(bitmap, 0f, 0f, paint)
//
//    paint.color = Color.WHITE
//    paint.alpha = 40
//    canvas.drawRoundRect()
//
//    return combinedBitmap
//}

//fun createImageListFileGenPic(
//    pairList: List<Pair<String, Int>>,
//    imageListPath: String,
//    frameCount: Double
//) {
//    try {
//        val time = 1 / frameCount
//        BufferedWriter(FileWriter(imageListPath)).use { writer ->
//            for (imagePath in pairList) {
//                val path = imagePath.first
//                val count = imagePath.second
//
//                (0 until count)?.forEach {
//                    writer.write("file '${path}'\n")
//                    // 设置每张图片显示的持续时间
//                    writer.write("duration ${time}\n") // 每张图片显示时间（秒），24帧每秒 -> 1/24 ≈ 0.04
//                }
//            }
//            val lastPair = pairList.get(pairList.size - 1)
//            val lastPath = lastPair.first
//            // 需要最后一张图片的持续时间
//            writer.write("file '" + lastPath + "'\n")
//        }
//    } catch (e: IOException) {
//        e.printStackTrace()
//    }
//}