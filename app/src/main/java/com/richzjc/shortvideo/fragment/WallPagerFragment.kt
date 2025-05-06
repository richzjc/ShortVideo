package com.richzjc.shortvideo.fragment

import android.app.Activity.RESULT_OK
import android.app.WallpaperManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.huantansheng.easyphotos.EasyPhotos
import com.huantansheng.easyphotos.models.album.entity.Photo
import com.richzjc.shortvideo.R
import com.richzjc.shortvideo.dialog.selectPicFromCameraOrPic
import com.richzjc.shortvideo.util.MToastHelper
import com.richzjc.shortvideo.util.QDUtil
import com.richzjc.shortvideo.util.ResourceUtils
import com.richzjc.shortvideo.util.ScreenUtils
import com.richzjc.shortvideo.util.ShapeDrawable
import com.richzjc.shortvideo.util.SharedPrefsUtil
import com.richzjc.shortvideo.util.requestData
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.math.abs


class WallPagerFragment : Fragment() {
    private var originPath: String? = ""

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
        return inflater.inflate(R.layout.video_fragment_wallpager, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val setWallPager = view.findViewById<View>(R.id.set_wallpager)
        val selectPicView = view.findViewById<View>(R.id.select_pic)
        val edit = view.findViewById<EditText>(R.id.edit)
        edit?.text = SpannableStringBuilder(SharedPrefsUtil.getString(context, "target"))
        selectPicView.background = btnDrawable
        setWallPager.background = btnDrawable
        selectPicView.setOnClickListener {
            responseToSelectPic()
        }

        setWallPager.setOnClickListener {
            if (TextUtils.isEmpty(originPath)) {
                MToastHelper.showToast("请选择图片")
            } else {
                setWallPaper()
            }
        }
    }

    private fun setWallPaper() {
        requestData {
            val bitmap = BitmapFactory.decodeFile(originPath)
            var resultBitmap = adjustShewen(bitmap)
            resultBitmap = adjustSheDiao(resultBitmap)
            resultBitmap = adjustBaohedu(resultBitmap)
            resultBitmap = adjustImageHierarchy(resultBitmap)
            resultBitmap = applyFilmEffect(resultBitmap)
            var outputBitmap = Bitmap.createBitmap(
                ScreenUtils.getScreenWidth(),
                ScreenUtils.getScreenHeight(),
                Bitmap.Config.ARGB_8888
            )
            val paint = Paint()
            // 设置画笔去掉透明度
            paint.isAntiAlias = true
            paint.alpha = 255
            val canvas = Canvas(outputBitmap)
            canvas.drawBitmap(
                resultBitmap,
                (ScreenUtils.getScreenWidth() - resultBitmap.width) / 2f,
                (ScreenUtils.getScreenHeight() - resultBitmap.height) / 2f,
                paint
            )
            val edit = view?.findViewById<EditText>(R.id.edit)
            SharedPrefsUtil.saveString(context, "target", edit?.text?.toString()?.trim() ?: "")
            if (!TextUtils.isEmpty(edit?.text?.trim())) {
                paint.color = Color.parseColor("#ffffff")
                paint.textSize = 80f
                val format = SimpleDateFormat("yyyy-MM-dd")
                val date = format.format(Calendar.getInstance().time)
                val realText = "${date}"
                val rect2 = Rect()
                paint.getTextBounds(realText, 0, realText.length, rect2)
                canvas.drawText(
                    realText,
                    (ScreenUtils.getScreenWidth() - abs(rect2.right - rect2.left)) / 2f,
                    ScreenUtils.getScreenHeight() * 0.4f,
                    paint
                )

                val text = edit?.text?.trim()
                val arr = text!!.split("\n")
                var start = ScreenUtils.getScreenHeight() * 0.4f
                arr.forEach {
                    paint.color = Color.parseColor("#ffffff")
                    paint.textSize = 80f
                    val rect2 = Rect()
                    paint.getTextBounds(it, 0, it.length, rect2)

                    start += (abs(rect2.bottom - rect2.top) + ScreenUtils.dip2px(10f))
                    canvas.drawText(
                        it,
                        (ScreenUtils.getScreenWidth() - abs(rect2.right - rect2.left)) / 2f,
                        start,
                        paint
                    )
                }

            }
            try {
                MToastHelper.showToast("正在设置中")
                val wallpaperManager = WallpaperManager.getInstance(context)
                // 关键参数：FLAG_LOCK 表示设置锁屏壁纸
                wallpaperManager.setBitmap(outputBitmap, null, true, WallpaperManager.FLAG_LOCK)
                wallpaperManager.setBitmap(outputBitmap, null, true, WallpaperManager.FLAG_SYSTEM)
                MToastHelper.showToast("设置成功")
            } catch (e: Exception) {
                e.printStackTrace()
                MToastHelper.showToast("设置失败")
            }
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