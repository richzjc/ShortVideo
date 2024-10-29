package com.richzjc.shortvideo.fragment.picVideo

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

fun genPic(
    context: Context,
    pathList: List<String>,
    statusTv: TextView?,
    handlePic: ImageView?
): List<Pair<String, Int>> {
    updateStatusText("开始生成图片", statusTv)
    //用来存储视频的配置文件
    val pairList = ArrayList<Pair<String, Int>>()
    //每一轮显示2秒
    val size = pathList.size
    var index = 0
    val height = 1080

    val outputFile = File(QDUtil.getShareImageCache(context).absolutePath, "picVideo")

    if (!outputFile.exists())
        outputFile.mkdirs()

    (0 until size)?.forEach {
        var curPath = pathList.get(it)
        var nextPath = ""
        if (it + 1 >= size)
            nextPath = pathList.get(0)
        else
            nextPath = pathList.get(it + 1)

        var curBitmap = BitmapFactory.decodeFile(curPath)
        var picHeight = height - ScreenUtils.dip2px(20f) * 2
        var picWidth = (curBitmap.width * picHeight)/curBitmap.height
        curBitmap = Bitmap.createScaledBitmap(curBitmap, picWidth, picHeight, true)
        val curShuMiaoBitmap = responseToShuMiao(curBitmap)

        var nextBitmap = BitmapFactory.decodeFile(nextPath)
       picHeight = height - ScreenUtils.dip2px(20f) * 2
       picWidth = (nextBitmap.width * picHeight)/nextBitmap.height
        nextBitmap = Bitmap.createScaledBitmap(nextBitmap, picWidth, picHeight, true)
        val nextShuMiaoBitmap = responseToShuMiao(nextBitmap)


        var  bmp0 = makeEdgesOpaque(curShuMiaoBitmap)
        var  bmp1 = makeEdgesOpaque(curBitmap)
        var  bmp2 = null
        //生成1.0
        var bitmap = realGenPic(curShuMiaoBitmap, curBitmap, null, 1.0f, true)
        if (index == 0) {
            Handler(Looper.getMainLooper()).post {
                handlePic?.setImageBitmap(bitmap)
            }
        }

        // 获取应用的内部存储路径
        val imageFile = File(outputFile, "${index}.png")
        index++
        // 创建文件输出流
        val fos = FileOutputStream(imageFile)
        // 将 Bitmap 压缩为 JPEG 格式并写入文件流
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        // 关闭文件流
        fos.close()
        var pair = Pair<String, Int>(imageFile.absolutePath, 90)
        pairList.add(pair)


        // 生成right
        (0 until 30)?.forEach {
            var scale = ((-1 * (it + 1) * (it + 1)) / 900.0f) + 1
            var bitmap =
                realGenPic(curShuMiaoBitmap, nextBitmap, curBitmap, scale, false)

            // 获取应用的内部存储路径
            val imageFile = File(outputFile, "${index}.png")
            index++
            // 创建文件输出流
            val fos = FileOutputStream(imageFile)
            // 将 Bitmap 压缩为 JPEG 格式并写入文件流
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            // 关闭文件流
            fos.close()
            var pair = Pair<String, Int>(imageFile.absolutePath, 1)
            pairList.add(pair)
        }

        //生成left
        (0 until 30)?.forEach {
            var scale = ((-1 * ((30 - it - 1)) * (30 - it - 1)) / 900.0f) + 1
            var bitmap =
                realGenPic(curShuMiaoBitmap, nextBitmap, nextShuMiaoBitmap, scale, true)

            // 获取应用的内部存储路径
            val imageFile = File(outputFile, "${index}.png")
            index++
            // 创建文件输出流
            val fos = FileOutputStream(imageFile)
            // 将 Bitmap 压缩为 JPEG 格式并写入文件流
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            // 关闭文件流
            fos.close()
            var pair = Pair<String, Int>(imageFile.absolutePath, 1)
            pairList.add(pair)
        }
    }

    updateStatusText("生成图片结束", statusTv)
    return pairList
}

fun makeEdgesOpaque(originalBitmap: Bitmap): Bitmap {
    val width = originalBitmap.width
    val height = originalBitmap.height
    val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(outputBitmap)
    val paint = Paint()

    // 设置画笔去掉透明度
    paint.isAntiAlias = true
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
    paint.alpha = 255

    // 绘制原始 Bitmap 到新的 Bitmap 上
    canvas.drawBitmap(originalBitmap, 0f, 0f, paint)

    // 处理边缘
    for (x in 0 until width) {
        for (y in 0 until height) {
            if (x < 88 || y < 88) {
                val alpha = (((min(x, y) + 1) / 88.0) * 255.0).toInt()
                val pixel = outputBitmap.getPixel(x, y)
                val newPixel = Color.argb(
                    alpha, // 设置新的透明度为不透明
                    Color.red(pixel),
                    Color.green(pixel),
                    Color.blue(pixel)
                )
                outputBitmap.setPixel(x, y, newPixel)
            }else if(x > width - 88 || y > height - 88){
                val alpha = (((min(width - x, height - y) + 1) / 88.0) * 255.0).toInt()
                val pixel = outputBitmap.getPixel(x, y)
                val newPixel = Color.argb(
                    alpha, // 设置新的透明度为不透明
                    Color.red(pixel),
                    Color.green(pixel),
                    Color.blue(pixel)
                )
                outputBitmap.setPixel(x, y, newPixel)
            }
        }
    }

    return outputBitmap
}

private fun getChildBitMap(isLeft: Boolean, scaleX: Float, picBitMap: Bitmap): Bitmap {
    val width = 960
    val height = 1080

    val combinedBitmap =
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(combinedBitmap)
    val paint = Paint()
    paint.isAntiAlias = true
    var bgBmp = BitmapFactory.decodeResource(
        UtilsContextManager.getInstance().application.resources,
        R.mipmap.imgnew
    )
    bgBmp = Bitmap.createScaledBitmap(bgBmp, width, height, true)
    canvas.drawBitmap(bgBmp, 0f, 0f, paint)

    if (isLeft) {
        canvas.drawBitmap(
            picBitMap,
            (width / 2 - picBitMap.width / 2).toFloat(),
            ScreenUtils.dip2px(10f).toFloat(),
            paint
        )

        // 初始化渐变效果
        val shadowWidth = ScreenUtils.dip2px(50f)
        val gradient = LinearGradient(
            (width - shadowWidth).toFloat(), 0f, width.toFloat(), 0f,
            0x00000000, 0xFF88000000.toInt(),
            Shader.TileMode.CLAMP
        )
        paint.shader = gradient
        canvas.drawRect(
            (width - shadowWidth).toFloat(),
            0f,
            width.toFloat(),
            height.toFloat(),
            paint
        )
    } else {
        canvas.drawBitmap(
            picBitMap,
            (width / 2 - picBitMap.width / 2).toFloat(),
            ScreenUtils.dip2px(10f).toFloat(),
            paint
        )

        // 初始化渐变效果
        val shadowWidth = ScreenUtils.dip2px(50f)
        val gradient = LinearGradient(
            0f, 0f, shadowWidth.toFloat(), 0f,
            0xFF88000000.toInt(), 0x00000000,
            Shader.TileMode.CLAMP
        )
        paint.shader = gradient


        canvas.drawRect(0f, 0f, shadowWidth.toFloat(), height.toFloat(), paint)
    }

    if (scaleX != 1.0f) {
        var realWidth = combinedBitmap.width * scaleX
        if (realWidth <= 0)
            realWidth = 1f
        return Bitmap.createScaledBitmap(
            combinedBitmap,
            realWidth.toInt(),
            combinedBitmap.height,
            true
        )
    } else {
        return combinedBitmap
    }
}

private fun realGenPic(
    leftBitmap: Bitmap,
    rightBitmap: Bitmap,
    fanzhuanBitmap: Bitmap?,
    scale: Float,
    isLeft: Boolean
): Bitmap {
    val width = 1920
    val height = 1080
    val combinedBitmap =
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(combinedBitmap)

    val paint = Paint()
    //绘制背景
    paint.isAntiAlias = true

    //绘制左边
    var childPic = getChildBitMap(true, 1.0f, leftBitmap)
    canvas.drawBitmap(childPic, 0f, 0f, paint)

    //绘制右边的图片
    childPic = getChildBitMap(false, 1.0f, rightBitmap)
    canvas.drawBitmap(childPic, (width / 2f), 0f, paint)
    //绘制左右的素描图片
    if (fanzhuanBitmap != null) {
        if (isLeft) {
            childPic = getChildBitMap(true, scale, fanzhuanBitmap)
            canvas.drawBitmap(childPic, (width / 2f) - childPic.width, 0f, paint)
        } else {
            childPic = getChildBitMap(false, scale, fanzhuanBitmap)
            canvas.drawBitmap(childPic, (width / 2f), 0f, paint)
        }
    }
    return combinedBitmap
}

private fun responseToShuMiao(bitmap: Bitmap): Bitmap {
    // 将Bitmap转换为Mat对象
    val originalMat = Mat()
    Utils.bitmapToMat(bitmap, originalMat)

    // 转换为灰度图像
    val grayImage = Mat()
    Imgproc.cvtColor(originalMat, grayImage, Imgproc.COLOR_BGR2GRAY)

    // 应用高斯模糊
    val blurred = Mat()
    Imgproc.GaussianBlur(grayImage, blurred, Size(21.0, 21.0), 0.0)

    // 反转图像
    val inverted = Mat()
    Core.bitwise_not(blurred, inverted)

    // 创建空白图像用于计算
    val blankImage = Mat(inverted.size(), CvType.CV_8UC1, Scalar(255.0))

    // 计算反转后的图像
    val invertedImage = Mat()
    Core.subtract(blankImage, inverted, invertedImage)

    // 创建铅笔素描效果
    val sketch = Mat()
    Core.divide(grayImage, invertedImage, sketch, 256.0)


    // 转换为彩色图像
    val colorImage = Mat()
    Imgproc.cvtColor(sketch, colorImage, Imgproc.COLOR_GRAY2BGR)

    // 转换回 Bitmap
    val resultBitmap =
        Bitmap.createBitmap(colorImage.cols(), colorImage.rows(), Bitmap.Config.ARGB_8888)
    Utils.matToBitmap(colorImage, resultBitmap)

    return resultBitmap
}

fun genPicVideo(context: Context, statusTv: TextView?, pairList: List<Pair<String, Int>>) {
    updateStatusText("开始生成视频", statusTv)

    // 创建一个临时文本文件来存储图片路径
    val imageListPathFile = File(context?.getExternalFilesDir(null), "picVideo");
    if (imageListPathFile.exists())
        imageListPathFile.delete()

    val imageListPath: String = imageListPathFile.absolutePath
    val fps = 60.0
    createImageListFileGenPic(pairList, imageListPath, fps)

    val realOutputFile = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
        "picVideo.mp4"
    )

    // 构建FFmpeg命令
    val frameRate = fps
    val cmd =
        "-y -f concat -safe 0 -i $imageListPath -vsync vfr -pix_fmt yuv420p -r $frameRate -b:v 8000k ${realOutputFile.absolutePath}"

    // 执行FFmpeg命令
    val returnCode = FFmpeg.execute(cmd)
    if (returnCode == Config.RETURN_CODE_SUCCESS) {
        updateStatusText("生成视频成功", statusTv)
    } else if (returnCode == Config.RETURN_CODE_CANCEL) {
        updateStatusText("生成视频取消", statusTv)
    } else {
        updateStatusText("生成失败", statusTv)
    }
}


fun createImageListFileGenPic(
    pairList: List<Pair<String, Int>>,
    imageListPath: String,
    frameCount: Double
) {
    try {
        val time = 1 / frameCount
        BufferedWriter(FileWriter(imageListPath)).use { writer ->
            for (imagePath in pairList) {
                val path = imagePath.first
                val count = imagePath.second

                (0 until count)?.forEach {
                    writer.write("file '${path}'\n")
                    // 设置每张图片显示的持续时间
                    writer.write("duration ${time}\n") // 每张图片显示时间（秒），24帧每秒 -> 1/24 ≈ 0.04
                }
            }
            val lastPair = pairList.get(pairList.size - 1)
            val lastPath = lastPair.first
            // 需要最后一张图片的持续时间
            writer.write("file '" + lastPath + "'\n")
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}