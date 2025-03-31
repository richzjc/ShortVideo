package com.richzjc.shortvideo.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.huantansheng.easyphotos.EasyPhotos
import com.huantansheng.easyphotos.models.album.entity.Photo
import com.richzjc.shortvideo.R
import com.richzjc.shortvideo.TestActivity
import com.richzjc.shortvideo.dialog.selectPicFromCameraOrPic
import com.richzjc.shortvideo.fragment.autoVideo.fangan.saveBitmapToFile
import com.richzjc.shortvideo.util.ResourceUtils
import com.richzjc.shortvideo.util.ScreenUtils
import com.richzjc.shortvideo.util.ShapeDrawable
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import java.io.File
import java.io.FileOutputStream


class EditPicFragment : Fragment() {
    private var originPath: String? = ""
    private var handlePic: ImageView? = null

    private val btnDrawable by lazy {
        ShapeDrawable.getDrawable(
            ScreenUtils.dip2px(1f),
            ScreenUtils.dip2px(5f),
            ResourceUtils.getColor(R.color.day_mode_theme_color_1478f0),
            ResourceUtils.getColor(R.color.day_mode_theme_color_1478f0)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.video_fragment_edit_pic, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val selectPicView = view.findViewById<View>(R.id.select_pic)
        val shumiao = view.findViewById<View>(R.id.shumiao)
        val editPic = view.findViewById<View>(R.id.editPic)
        val removeBackground = view.findViewById<View>(R.id.removeBackground)
        handlePic = view.findViewById(R.id.handle_pic)
        selectPicView.background = btnDrawable
        shumiao.background = btnDrawable
        editPic.background = btnDrawable
        removeBackground.background = btnDrawable
        selectPicView.setOnClickListener {
            responseToSelectPic()
        }

        shumiao.setOnClickListener {
            responseToShuMiao()
        }

        editPic.setOnClickListener {
            val intent = Intent(context, TestActivity::class.java)
            startActivity(intent)
        }

        removeBackground.setOnClickListener {

        }
    }

    private fun responseToShuMiao() {
        if (!TextUtils.isEmpty(originPath)) {
            val bitmap = BitmapFactory.decodeFile(originPath)
//
//            // 将Bitmap转换为Mat对象
//            val originalMat = Mat()
//            Utils.bitmapToMat(bitmap, originalMat)
//
//            // 转换为灰度图像
//            val grayImage = Mat()
//            Imgproc.cvtColor(originalMat, grayImage, Imgproc.COLOR_BGR2GRAY)
//
//            // 应用高斯模糊
//            val blurred = Mat()
//            Imgproc.GaussianBlur(grayImage, blurred, Size(21.0, 21.0), 0.0)
//
//            // 反转图像
//            val inverted = Mat()
//            Core.bitwise_not(blurred, inverted)
//
//            // 创建空白图像用于计算
//            val blankImage = Mat(inverted.size(), CvType.CV_8UC1, Scalar(255.0))
//
//            // 计算反转后的图像
//            val invertedImage = Mat()
//            Core.subtract(blankImage, inverted, invertedImage)
//
//            // 创建铅笔素描效果
//            val sketch = Mat()
//            Core.divide(grayImage, invertedImage, sketch, 256.0)
//
//
//            // 转换为彩色图像
//            val colorImage = Mat()
//            Imgproc.cvtColor(sketch, colorImage, Imgproc.COLOR_GRAY2BGR)
//
//            // 转换回 Bitmap
//            val resultBitmap =
//                Bitmap.createBitmap(colorImage.cols(), colorImage.rows(), Bitmap.Config.ARGB_8888)
//            Utils.matToBitmap(colorImage, resultBitmap)
            var resultBitmap = adjustShewen(bitmap)
            resultBitmap = adjustSheDiao(resultBitmap)
            resultBitmap = adjustBaohedu(resultBitmap)
            resultBitmap = adjustImageHierarchy(resultBitmap)
            resultBitmap = applyFilmEffect(resultBitmap)
            handlePic?.setImageBitmap(resultBitmap)
            saveBitmap(resultBitmap)
        }
    }


    fun applyFilmEffect(originalBitmap: Bitmap): Bitmap {
        // Step 1: 初始化矩阵
        val srcMat = Mat()
        Utils.bitmapToMat(originalBitmap, srcMat)
        Imgproc.cvtColor(srcMat, srcMat, Imgproc.COLOR_RGBA2BGR)

        // Step 2: 电影感滤镜（C1暖色系）
        val hslMat = Mat()
        Imgproc.cvtColor(srcMat, hslMat, Imgproc.COLOR_BGR2HLS)

        // 调整饱和度和明度
        val channels = ArrayList<Mat>().apply { Core.split(hslMat, this) }
        Core.multiply(channels[2], Scalar(1.2), channels[2]) // 明度+20%
        Core.multiply(channels[1], Scalar(0.9), channels[1]) // 饱和度-10%
        Core.merge(channels, hslMat)
        Imgproc.cvtColor(hslMat, srcMat, Imgproc.COLOR_HLS2BGR)

        // Step 3: 曲线工具高光压缩（修正参数类型）
        val lut = Mat(256, 1, CvType.CV_8UC3).apply {
            for (i in 0 until 256) {
                val adjusted = when {
                    i > 200 -> (i * 0.85 + 30).toInt() // 高光压缩
                    else -> i
                }
                // 修正：使用 doubleArray 传递多通道值 [1,6](@ref)
                put(i, 0, adjusted.toDouble(), adjusted.toDouble(), adjusted.toDouble())
            }
        }
        Core.LUT(srcMat, lut, srcMat)

        // Step 4: 颗粒生成（修正参数类型）
        val noise = Mat(srcMat.size(), CvType.CV_8UC3)
        Core.randn(noise, 0.0, 15.0) // 使用 double 类型参数 [5,6](@ref)
        Core.addWeighted(srcMat, 0.85, noise, 0.15, 0.0, srcMat)

        // Step 5: 转换回Bitmap
        val resultBitmap = Bitmap.createBitmap(originalBitmap.width, originalBitmap.height,
            Bitmap.Config.ARGB_8888)
        Imgproc.cvtColor(srcMat, srcMat, Imgproc.COLOR_BGR2RGBA)
        Utils.matToBitmap(srcMat, resultBitmap)

        srcMat.release()
        hslMat.release()
        return resultBitmap
    }

    private fun adjustImageHierarchy(originalBitmap: Bitmap): Bitmap {
        // Step 1: 初始化 OpenCV 矩阵
        val srcMat = Mat()
        Utils.bitmapToMat(originalBitmap, srcMat)
        Imgproc.cvtColor(srcMat, srcMat, Imgproc.COLOR_RGBA2BGR)

        // Step 2: 转换为 HSV 色彩空间
        val hsvMat = Mat()
        Imgproc.cvtColor(srcMat, hsvMat, Imgproc.COLOR_BGR2HSV)

        // Step 3: 分离 HSV 通道
        val hsvChannels = ArrayList<Mat>()
        Core.split(hsvMat, hsvChannels)
        val valueChannel = hsvChannels[2]

        // --- 层次感控制算法 ---
        // 3.1 降低对比度（-15~-25对应线性压缩）
        valueChannel.convertTo(valueChannel, CvType.CV_32F)
        Core.multiply(valueChannel, Scalar(0.8), valueChannel) // 对比度降低20%

        // 3.2 提升阴影（伽马校正）
        val shadowMask = Mat()
        Core.inRange(valueChannel, Scalar(0.0), Scalar(50.0), shadowMask) // 阴影区域（V < 50）
        val temp = Mat() // 创建临时矩阵存储伽马校正结果
        Core.pow(valueChannel, 0.85, temp) // 伽马校正（γ=0.85对应阴影提升+20）
        temp.copyTo(valueChannel, shadowMask) // 仅对阴影区域应用校正[8](@ref)

        // 3.3 抑制高光（像素截断）
        val highlightMask = Mat()
        Core.inRange(valueChannel, Scalar(200.0), Scalar(255.0), highlightMask)
        Core.subtract(valueChannel, Scalar(10.0), valueChannel, highlightMask)

        // --- 数据归一化与合并 ---
        valueChannel.convertTo(valueChannel, CvType.CV_8UC1)
        hsvChannels[2] = valueChannel
        Core.merge(hsvChannels, hsvMat)

        // Step 4: 转换回 BGR
        Imgproc.cvtColor(hsvMat, srcMat, Imgproc.COLOR_HSV2BGR)

        // Step 5: 转换回 Bitmap
        val resultBitmap = Bitmap.createBitmap(originalBitmap.width, originalBitmap.height, Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(srcMat, resultBitmap)

        // 释放内存
        srcMat.release()
        hsvMat.release()
        hsvChannels.forEach { it.release() }

        return resultBitmap
    }

    fun adjustBaohedu(originalBitmap: Bitmap): Bitmap {
        // Step 1: 初始化 OpenCV 矩阵
        val srcMat = Mat()
        Utils.bitmapToMat(originalBitmap, srcMat)
        Imgproc.cvtColor(srcMat, srcMat, Imgproc.COLOR_RGBA2BGR) // 转换到BGR格式

        // Step 2: 转换为 HSV 色彩空间
        val hsvMat = Mat()
        Imgproc.cvtColor(srcMat, hsvMat, Imgproc.COLOR_BGR2HSV)

        // Step 3: 分离 HSV 通道
        val hsvChannels = ArrayList<Mat>()
        Core.split(hsvMat, hsvChannels)
        val saturationChannel = hsvChannels[1]

        // Step 4: 降低饱和度至 -30%
        saturationChannel.convertTo(saturationChannel, CvType.CV_32F)
        Core.multiply(saturationChannel, Scalar(0.7), saturationChannel) // 0.7 = 1 - 0.3
        saturationChannel.convertTo(saturationChannel, CvType.CV_8UC1)

        // Step 5: 合并通道并转回 BGR
        hsvChannels[1] = saturationChannel
        Core.merge(hsvChannels, hsvMat)
        Imgproc.cvtColor(hsvMat, srcMat, Imgproc.COLOR_HSV2BGR)

        // Step 6: 转换回 Bitmap
        val resultBitmap = Bitmap.createBitmap(originalBitmap.width, originalBitmap.height, Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(srcMat, resultBitmap)

        // 释放内存
        srcMat.release()
        hsvMat.release()
        hsvChannels.forEach { it.release() }
        return resultBitmap
    }

    fun adjustSheDiao(originalBitmap: Bitmap, hueShift: Int = 8): Bitmap {
        // Step 1: 初始化 OpenCV 矩阵
        val srcMat = Mat()
        Utils.bitmapToMat(originalBitmap, srcMat)
        Imgproc.cvtColor(srcMat, srcMat, Imgproc.COLOR_RGBA2BGR)

        // Step 2: 转换为 HSV 色彩空间
        val hsvMat = Mat()
        Imgproc.cvtColor(srcMat, hsvMat, Imgproc.COLOR_BGR2HSV_FULL) // 使用全范围HSV(0-255)

        // Step 3: 分离通道并调整色相
        val channels = ArrayList<Mat>()
        Core.split(hsvMat, channels)
        val hueChannel = channels[0]

        // 色相右移 +8 度（参数可调节 5-10）
        Core.add(hueChannel, Scalar(hueShift.toDouble()), hueChannel)
        hueChannel.convertTo(hueChannel, CvType.CV_8UC1)

        // Step 4: 补充淡紫色中和（BGR通道混合）
        val purpleMix = Mat(srcMat.size(), srcMat.type(), Scalar(60.0, 30.0, 150.0)) // 淡紫色基准值
        Core.addWeighted(srcMat, 0.7, purpleMix, 0.3, 0.0, srcMat) // 混合比例 7:3

        // Step 5: 合并HSV通道并转换回BGR
        channels[0] = hueChannel
        Core.merge(channels, hsvMat)
        Imgproc.cvtColor(hsvMat, srcMat, Imgproc.COLOR_HSV2BGR_FULL)

        // Step 6: 转换回Bitmap
        val resultBitmap = Bitmap.createBitmap(originalBitmap.width, originalBitmap.height, Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(srcMat, resultBitmap)

        // 释放内存
        srcMat.release()
        hsvMat.release()
        channels.forEach { it.release() }

        return resultBitmap
    }


    fun adjustShewen(originalBitmap: Bitmap): Bitmap {
        // Step 1: 初始化 OpenCV 矩阵
        val srcMat = Mat()
        Utils.bitmapToMat(originalBitmap, srcMat)
        Imgproc.cvtColor(srcMat, srcMat, Imgproc.COLOR_RGBA2BGR)

        // Step 2: 分离 BGR 通道
        val channels = ArrayList<Mat>()
        Core.split(srcMat, channels)
        val blueChannel = channels[0]
        val greenChannel = channels[1]
        val redChannel = channels[2]

        // Step 3: 调整色温至-50（青蓝色调）
        // 红色通道降低25%（-50对应约1/2色温偏移量）
        redChannel.convertTo(redChannel, CvType.CV_32F)
        Core.multiply(redChannel, Scalar(0.75), redChannel)

        // 蓝色通道增强12%
        blueChannel.convertTo(blueChannel, CvType.CV_32F)
        Core.multiply(blueChannel, Scalar(1.12), blueChannel)

        // 绿色通道微调8%增强青色感
        greenChannel.convertTo(greenChannel, CvType.CV_32F)
        Core.multiply(greenChannel, Scalar(1.08), greenChannel)

        // 转换回8位无符号整数
        redChannel.convertTo(redChannel, CvType.CV_8UC1)
        blueChannel.convertTo(blueChannel, CvType.CV_8UC1)
        greenChannel.convertTo(greenChannel, CvType.CV_8UC1)

        // Step 4: 合并通道
        channels[0] = blueChannel
        channels[1] = greenChannel
        channels[2] = redChannel
        Core.merge(channels, srcMat)

        // Step 5: 色相微调强化青蓝色
        val hsvMat = Mat()
        Imgproc.cvtColor(srcMat, hsvMat, Imgproc.COLOR_BGR2HSV)

        // 色相偏移至青蓝区域（Hue 180-200）
        Core.add(hsvMat, Scalar(10.0, 0.0, 0.0), hsvMat)
        Imgproc.cvtColor(hsvMat, srcMat, Imgproc.COLOR_HSV2BGR)

        // Step 6: 转换回Bitmap
        val resultBitmap = Bitmap.createBitmap(originalBitmap.width, originalBitmap.height, Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(srcMat, resultBitmap)

        // 释放内存
        srcMat.release()
        hsvMat.release()
        channels.forEach { it.release() }

        return resultBitmap
    }



    private fun saveBitmap(resultBitmap: Bitmap) {
        try {
            val height = resultBitmap.height
            val outputBitmap =
                Bitmap.createBitmap(resultBitmap.width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(outputBitmap)
            val paint = Paint()
            // 设置画笔去掉透明度
            paint.isAntiAlias = true
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
            paint.alpha = 255
            canvas.drawBitmap(resultBitmap, 0f, 0f, paint)

            // 获取应用的内部存储路径
            val imageFile = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                "${System.currentTimeMillis()}.png"
            )
            // 创建文件输出流
            val fos = FileOutputStream(imageFile)
            // 将 Bitmap 压缩为 JPEG 格式并写入文件流
            outputBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            // 关闭文件流
            fos.close()

            MediaScannerConnection.scanFile(
                context,
                arrayOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath),
                arrayOf("image/png"),
                object : MediaScannerConnection.OnScanCompletedListener {
                    override fun onScanCompleted(path: String?, uri: Uri?) {
                        Log.d("Scan", "扫描完成，URI: $uri")
                    }
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun responseToSelectPic() {
        selectPicFromCameraOrPic(requireActivity(), 1, true, null) { resultCode, data ->
            if (resultCode === RESULT_OK) {
                val resultPhotos: ArrayList<Photo>? =
                    data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS)
                if (resultPhotos != null && resultPhotos.size > 0) {
                    val photo = resultPhotos[0]
                    originPath = photo.path
                    view?.findViewById<ImageView>(R.id.origin_pic)
                        ?.setImageBitmap(BitmapFactory.decodeFile(photo.path))
                }
            }
        }
    }
}