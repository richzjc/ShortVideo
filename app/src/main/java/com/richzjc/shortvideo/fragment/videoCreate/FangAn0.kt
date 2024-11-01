package com.richzjc.shortvideo.fragment.videoCreate

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.set
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import com.richzjc.shortvideo.R
import com.richzjc.shortvideo.util.QDUtil
import com.richzjc.shortvideo.util.ScreenUtils
import com.richzjc.shortvideo.util.requestData
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.max

fun responseHeChengNBA(context: Context, originPath: List<String>?, statusTV: TextView?) {
    requestData {

        val imageFile = File(QDUtil.getShareImageCache(context).absolutePath, "imagePianTou")
        if(!imageFile.exists()){
            imageFile.mkdirs()
        }

        var bitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.fengmian)
        var outputBitmap = Bitmap.createScaledBitmap(bitmap, 1080, 1920, false)
        // 获取应用的内部存储路径
        val fengMianFile = File(imageFile, "fengmian.png")
        // 创建文件输出流
        val fos = FileOutputStream(imageFile)
        // 将 Bitmap 压缩为 JPEG 格式并写入文件流
        outputBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        // 关闭文件流
        fos.close()

        originPath?.forEachIndexed { index, s ->
            val file0 = File(QDUtil.getShareImageCache(context).absolutePath, "image${index}")
            if (file0.exists()) {
                val listFiles = file0.listFiles()
                listFiles?.forEach {
                    it.delete()
                }
            }

            if (!file0.exists())
                file0.mkdirs()

            val file1 = File(s)
            updateStatusText("提取第${index}个视频的每一帧图片", statusTV)
            delay(1000L)

            val command = "-i ${file1.absolutePath} ${file0.absolutePath}/%d.png"
            val returnCode = FFmpeg.execute(command)
            if (returnCode == 0) {
                // 命令执行成功
                updateStatusText("提取帧图片完成", statusTV)
            } else {
                // 命令执行失败
                updateStatusText("提取帧图片失败", statusTV)
                // 获取错误日志
                val output = Config.getLastCommandOutput()
                Log.e("FFmpeg Error", output)
            }
        }
    }
}

fun processImage(
    context: Context,
    statusTV: TextView?,
    selectPicPath: String?,
    handlePic: ImageView?,
    originPic: ImageView?
) {
    var index = 0
    updateStatusText("开始处理第${index}个图片", statusTV)
    // 加载图片
    val file1 = File(QDUtil.getShareImageCache(context).absolutePath, "image_handle${index}")
    if (file1 != null) {
        val listFiles = file1.listFiles()
        listFiles?.forEach {
            if (it.exists())
                it.delete()
        }
    }

    if (!file1.exists())
        file1.mkdirs()

    val file0 = File(QDUtil.getShareImageCache(context).absolutePath, "image${index}")
    if (file0 != null) {
        val listFiles = file0.listFiles()
        val size = listFiles.size
        var bgBmp = BitmapFactory.decodeResource(context.resources, R.mipmap.imgnew)
        bgBmp = Bitmap.createScaledBitmap(bgBmp, 1920, 1920, true)

        val paint = Paint()
        // 设置画笔去掉透明度
        paint.isAntiAlias = true
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
        paint.alpha = 255


        var commentBg = BitmapFactory.decodeFile(selectPicPath)
        var commentBgWidth = 1080 - ScreenUtils.dip2px(40f)
        var commentBgHeight = (commentBg.height * commentBgWidth) / (commentBg.width * 1.0f)
        commentBg =
            Bitmap.createScaledBitmap(commentBg, commentBgWidth, commentBgHeight.toInt(), false)
        var commentBgGap = ((commentBgHeight + ScreenUtils.dip2px(10f)) / (30 * 1.0f)).toInt()
        commentBg = getRoundedCornerBitmap(commentBg, ScreenUtils.dip2px(20f).toFloat())

        var startX = 0
        var gap = (1920 - 1080) / 150f
        var isIncrease = true
        var startIndex = max(listFiles.size - 90, 0)
        listFiles?.forEachIndexed { index, it ->
            try {
                updateStatusText(
                    "开始处理第${index + 1}张图片； totalCount = ${size}",
                    statusTV
                )
                var outputBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(outputBitmap)

                var resultBitmap: Bitmap? = null
                if (isIncrease) {
                    resultBitmap = Bitmap.createBitmap(bgBmp, startX, 0, 1080, 1920, null, false)
                    startX += gap.toInt()
                    if (startX + 1080 > 1920) {
                        isIncrease = false
                        startX = 1920 - 1080
                    }
                } else {
                    resultBitmap = Bitmap.createBitmap(bgBmp, startX, 0, 1080, 1920, null, false)
                    startX -= gap.toInt()
                    if (startX < 0) {
                        isIncrease = true
                        startX = 0
                    }
                }

                canvas.drawBitmap(resultBitmap, 0f, 0f, paint)

                //绘制图片
                var inputBitmap = BitmapFactory.decodeFile(it.absolutePath)
                if (index <= startIndex) {
                    val inputWidth = 1080 * 0.98f
                    val inputHeight = 1920 * 0.98f
                    inputBitmap = Bitmap.createScaledBitmap(
                        inputBitmap,
                        inputWidth.toInt(),
                        inputHeight.toInt(),
                        false
                    )
                    val startX = (1080 - inputWidth)/2f
                    val startY = (1920 - inputHeight)/2
                    canvas.drawBitmap(
                        inputBitmap,
                        startX,
                        startY,
                        paint
                    )
                } else if(index < startIndex + 30) {
                    val inputWidth = 1080 * 0.98f
                    val inputHeight = 1920 * 0.98f
                    var value = (index - startIndex)%30
                    var realInputHeight = inputHeight - value * commentBgGap
                    var realInputWidth = (inputWidth * realInputHeight)/inputHeight
                    inputBitmap = Bitmap.createScaledBitmap(
                        inputBitmap,
                        realInputWidth.toInt(),
                        realInputHeight.toInt(),
                        false
                    )
                    val startX = (1080 - realInputWidth)/2f
                    val startY = 1920 * 0.01f
                    canvas.drawBitmap(
                        inputBitmap,
                        startX,
                        startY,
                        paint
                    )
                }else{
                    val inputWidth = 1080 * 0.98f
                    val inputHeight = 1920 * 0.98f
                    var value = 30
                    var realInputHeight = inputHeight - value * commentBgGap
                    var realInputWidth = (inputWidth * realInputHeight)/inputHeight
                    inputBitmap = Bitmap.createScaledBitmap(
                        inputBitmap,
                        realInputWidth.toInt(),
                        realInputHeight.toInt(),
                        false
                    )
                    val startX = (1080 - realInputWidth)/2f
                    val startY = 1920 * 0.01f
                    canvas.drawBitmap(
                        inputBitmap,
                        startX,
                        startY,
                        paint
                    )
                }


                if (index > startIndex && index < startIndex + 30) {
                    var value = (index - startIndex)%30
                    canvas.drawBitmap(
                        commentBg,
                        (1080 - commentBg.width) / 2f,
                        (1920 - value * commentBgGap).toFloat(),
                        paint
                    )
                }else if(index >= startIndex + 30){
                    canvas.drawBitmap(
                        commentBg,
                        (1080 - commentBg.width) / 2f,
                        (1920 - 30 * commentBgGap).toFloat(),
                        paint
                    )
                }


                if (index == 0) {
                    setHandlePic(originPic, inputBitmap)
                    setHandlePic(handlePic, outputBitmap)
                }

                // 获取应用的内部存储路径
                val imageFile = File(file1, it.name)
                // 创建文件输出流
                val fos = FileOutputStream(imageFile)
                // 将 Bitmap 压缩为 JPEG 格式并写入文件流
                outputBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                // 关闭文件流
                fos.close()
            } catch (e: Exception) {
                e.printStackTrace()
                updateStatusText("图片处理失败", statusTV)
            }
        }
    }
    updateStatusText("图片处理完成", statusTV)
}


private fun isBlack(color: Int): Boolean {
    // 可以根据实际需求调整黑色阈值
    val threshold = 10
    val r = Color.red(color)
    val g = Color.green(color)
    val b = Color.blue(color)
    return r < threshold && g < threshold && b < threshold
}

fun setHandlePic(handlePic: ImageView?, resultBitmap: Bitmap) {
    Handler(Looper.getMainLooper()).post {
        handlePic?.setImageBitmap(resultBitmap)
    }
}

fun getRoundedCornerBitmap(bitmap: Bitmap, cornerRadius: Float): Bitmap {
    val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)

    val color = 0xff424242.toInt()
    val paint = Paint()
    val rectF = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())

    paint.isAntiAlias = true
    canvas.drawARGB(0, 0, 0, 0)
    paint.color = color
    canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint)

    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(bitmap!!, 0f, 0f, paint!!)
    return output
}

fun updateStatusText(statusText: String?, statusTV: TextView?) {
    Handler(Looper.getMainLooper()).post {
        statusTV?.text = statusText ?: ""
    }
}