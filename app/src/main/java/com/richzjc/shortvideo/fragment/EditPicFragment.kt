package com.richzjc.shortvideo.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
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
import com.richzjc.shortvideo.util.ResourceUtils
import com.richzjc.shortvideo.util.ScreenUtils
import com.richzjc.shortvideo.util.ShapeDrawable
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Point
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

            handlePic?.setImageBitmap(resultBitmap)
            saveBitmap(bitmap, resultBitmap)
        }
    }

    private fun saveBitmap(originBitmap: Bitmap, resultBitmap : Bitmap){
        try {
            val height = originBitmap.height + resultBitmap.height
            val outputBitmap = Bitmap.createBitmap(originBitmap.width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(outputBitmap)
            val paint = Paint()
            // 设置画笔去掉透明度
            paint.isAntiAlias = true
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
            paint.alpha = 255
            canvas.drawBitmap(originBitmap, 0f, 0f, paint)
            canvas.drawBitmap(resultBitmap, 0f, originBitmap.height * 1f, paint)

            // 获取应用的内部存储路径
            val imageFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "${System.currentTimeMillis()}.png")
            // 创建文件输出流
            val fos = FileOutputStream(imageFile)
            // 将 Bitmap 压缩为 JPEG 格式并写入文件流
            outputBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            // 关闭文件流
            fos.close()
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