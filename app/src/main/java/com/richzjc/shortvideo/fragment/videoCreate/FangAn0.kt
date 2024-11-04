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

private fun copyRawToFile(context: Context, rawResId: Int, outputFile: File) {
    val inputStream: InputStream = context.resources.openRawResource(rawResId)
    val outputStream = FileOutputStream(outputFile)

    inputStream.use { input ->
        outputStream.use { output ->
            input.copyTo(output)
        }
    }
}

fun responseHePianTou(context: Context) {
    requestData {
        val file0 = File(QDUtil.getShareImageCache(context), "piantou.mp4")
        if (!file0.exists()) {
           copyRawToFile(context, R.raw.piantou, file0)
        }

        val file1 = File(QDUtil.getShareImageCache(context), "piantou")
        if(file1.exists())
            return@requestData

        file1.mkdirs()
        delay(1000L)

        val command = "-i ${file0.absolutePath} ${file1.absolutePath}/%d.png"
        FFmpeg.execute(command)
    }
}

fun responseHeChengNBA(context: Context, originPath: List<String>?, statusTV: TextView?) {
    requestData {
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

fun handlePianTou(context : Context){
    // 加载图片
    val file1 = File(QDUtil.getShareImageCache(context).absolutePath, "piantou_handle")
    if (file1 != null) {
        val listFiles = file1.listFiles()
        listFiles?.forEach {
            if (it.exists())
                it.delete()
        }
    }

    if (!file1.exists())
        file1.mkdirs()


    val file2 = File(QDUtil.getShareImageCache(context), "piantou")
    val listFiles = file2.listFiles()
    val paint = Paint()
    // 设置画笔去掉透明度
    paint.isAntiAlias = true
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
    paint.alpha = 255
    listFiles?.forEachIndexed { index, it ->
        if(index < 40) {
            var outputBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(outputBitmap)
            canvas.drawColor(Color.BLACK)
            var inputBitmap = BitmapFactory.decodeFile(it.absolutePath)
            inputBitmap = Bitmap.createScaledBitmap(inputBitmap, 1080, 1920, false)
            canvas.drawBitmap(
                inputBitmap,
                (1080f - inputBitmap.width) / 2f,
                (1920f - inputBitmap.height) / 2f,
                paint
            )
            // 获取应用的内部存储路径
            val imageFile = File(file1, it.name)
            // 创建文件输出流
            val fos = FileOutputStream(imageFile)
            // 将 Bitmap 压缩为 JPEG 格式并写入文件流
            outputBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            // 关闭文件流
            fos.close()
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

        var followHintBitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.follow_hint)
        var followHintBitmapWidth = commentBgWidth
        var followHintBitmapHeight =
            (followHintBitmap.height * commentBgWidth) / (followHintBitmap.width * 1.0f)
        followHintBitmap =
            Bitmap.createScaledBitmap(
                followHintBitmap,
                followHintBitmapWidth,
                followHintBitmapHeight.toInt(),
                false
            )
        followHintBitmap =
            getRoundedCornerBitmap(followHintBitmap, ScreenUtils.dip2px(20f).toFloat())

        var startX = 0
        var gap = (1920 - 1080) / 150f
        var isIncrease = true
        var startIndex = listFiles.size / 2
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


//                paint.alpha = 40
//                var startX = 1080 - (index % 100) * 10.8f
//                canvas.drawBitmap(followHintBitmap, startX, 1920f / 2, paint)
//                paint.alpha = 255


                //绘制图片
                var inputBitmap = BitmapFactory.decodeFile(it.absolutePath)
                inputBitmap = getRoundedCornerBitmap(inputBitmap, ScreenUtils.dip2px(15f).toFloat())
                if (index <= startIndex) {
                    val inputWidth = 1080 * 0.98f
                    val inputHeight = 1920 * 0.98f
                    inputBitmap = Bitmap.createScaledBitmap(
                        inputBitmap,
                        inputWidth.toInt(),
                        inputHeight.toInt(),
                        false
                    )
                    val startX = (1080 - inputWidth) / 2f
                    val startY = (1920 - inputHeight) / 2
                    canvas.drawBitmap(
                        inputBitmap,
                        startX,
                        startY,
                        paint
                    )
                } else if (index < startIndex + 30) {
                    val inputWidth = 1080 * 0.98f
                    val inputHeight = 1920 * 0.98f
                    var value = (index - startIndex) % 30
                    var realInputHeight = inputHeight - value * commentBgGap
                    var realInputWidth = (inputWidth * realInputHeight) / inputHeight
                    inputBitmap = Bitmap.createScaledBitmap(
                        inputBitmap,
                        realInputWidth.toInt(),
                        realInputHeight.toInt(),
                        false
                    )
                    val startX = (1080 - realInputWidth) / 2f
                    val startY = 1920 * 0.01f + value * commentBgGap
                    canvas.drawBitmap(
                        inputBitmap,
                        startX,
                        startY,
                        paint
                    )
                } else {
                    val inputWidth = 1080 * 0.98f
                    val inputHeight = 1920 * 0.98f
                    var value = 30
                    var realInputHeight = inputHeight - value * commentBgGap
                    var realInputWidth = (inputWidth * realInputHeight) / inputHeight
                    inputBitmap = Bitmap.createScaledBitmap(
                        inputBitmap,
                        realInputWidth.toInt(),
                        realInputHeight.toInt(),
                        false
                    )
                    val startX = (1080 - realInputWidth) / 2f
                    val startY = 1920 * 0.01f + value * commentBgGap
                    canvas.drawBitmap(
                        inputBitmap,
                        startX,
                        startY,
                        paint
                    )
                }


                if (index > startIndex && index < startIndex + 30) {
                    var value = (index - startIndex) % 30
                    canvas.drawBitmap(
                        commentBg,
                        (1080 - commentBg.width) / 2f,
                        1920 * 0.01f + (-30 * commentBgGap + value * commentBgGap).toFloat(),
                        paint
                    )
                } else if (index >= startIndex + 30) {
                    canvas.drawBitmap(
                        commentBg,
                        (1080 - commentBg.width) / 2f,
                        1920 * 0.01f + (-30 * commentBgGap + 30 * commentBgGap).toFloat(),
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