package com.richzjc.shortvideo.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.huantansheng.easyphotos.EasyPhotos
import com.huantansheng.easyphotos.models.album.entity.Photo
import com.richzjc.shortvideo.R
import com.richzjc.shortvideo.dialog.selectPicFromCameraOrPic
import com.richzjc.shortvideo.fragment.picVideo.genPic
import com.richzjc.shortvideo.fragment.picVideo.genPicVideo
import com.richzjc.shortvideo.util.MToastHelper
import com.richzjc.shortvideo.util.QDUtil
import com.richzjc.shortvideo.util.ResourceUtils
import com.richzjc.shortvideo.util.ScreenUtils
import com.richzjc.shortvideo.util.ShapeDrawable
import com.yalantis.ucrop.UCrop
import java.io.File
import kotlin.concurrent.thread

class AutoFragment : Fragment() {
    private val originPathList = ArrayList<String>()
    private var status: TextView? = null
    private var handlePic: ImageView? = null
    private var pairList: List<Pair<String, Int>>? = null
    var uCropLauncher: ActivityResultLauncher<Intent>? = null
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
        return inflater.inflate(R.layout.pic_video_fragment_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val generate_video = view.findViewById<Button>(R.id.generate_video)
        val generate_pic = view.findViewById<Button>(R.id.generate_pic)
        val select_pic = view.findViewById<Button>(R.id.select_pic)
        handlePic = view.findViewById(R.id.handle_pic)
        status = view.findViewById(R.id.status)

        generate_video.background = btnDrawable
        generate_pic.background = btnDrawable
        select_pic.background = btnDrawable

        select_pic?.setOnClickListener {
            MToastHelper.showToast("请先删除 picVideo 文件")
            responseToSelectVideo()
        }

        generate_pic?.setOnClickListener {
            if (originPathList == null || originPathList.size <= 1) {
                MToastHelper.showToast("至少选择两张图片")
                return@setOnClickListener
            } else {
                thread {
                    pairList = genPic(requireContext(), originPathList, status, handlePic)
                }
            }
        }

        generate_video?.setOnClickListener {
            thread {
                if (pairList == null || pairList!!.size <= 0) {
                    Handler(Looper.getMainLooper()).post {
                        MToastHelper.showToast("请先生成图片")
                    }
                } else {
                    genPicVideo(requireContext(), status, pairList!!)
                }
            }
        }
    }

    private fun responseToSelectVideo() {
        val outputFile =
            File(QDUtil.getShareImageCache(context).absolutePath, "picVideo")
        if(outputFile.exists())
            outputFile.delete()
        selectPicFromCameraOrPic(requireActivity(), 200, true, null) { resultCode, data ->
            if (resultCode === RESULT_OK) {
                originPathList.clear()
                val resultPhotos: ArrayList<Photo>? =
                    data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS)
                if (resultPhotos != null && resultPhotos.size >= 1) {
                    index = 0
                    photos = resultPhotos
                    handleImagePath(photos, index)
                }
            }
        }
    }

    private var index = 0
    private var photos: ArrayList<Photo>? = null

    private val callback by lazy {
        ActivityResultCallback<ActivityResult> { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let { intent ->
                    val resultUri = UCrop.getOutput(intent)
                    resultUri?.let { uri ->
                        // 处理裁剪后的图像
                        originPathList.add(uri.path!!)
                    }
                }
            } else if (result.resultCode == UCrop.RESULT_ERROR) {
                result.data?.let { intent ->
                    val cropError = UCrop.getError(intent)
                    cropError?.let {
                        MToastHelper.showToast("裁剪错误:${cropError.message}")
                    }
                }
            }
            index = index + 1
            handleImagePath(photos, index)
        }
    }

    private fun handleImagePath(resultPhotos: ArrayList<Photo>?, index: Int) {
        resultPhotos ?: return

        if (resultPhotos.size <= 0)
            return

        if (index >= resultPhotos!!.size)
            return

        val outputFile =
            File(QDUtil.getShareImageCache(context).absolutePath, "picVideo")
        if (!outputFile.exists())
            outputFile.mkdirs()

        val path = resultPhotos[index]

        // 注册ActivityResultLauncher
        if (uCropLauncher == null) {
            uCropLauncher =
                requireActivity().registerForActivityResult(
                    ActivityResultContracts.StartActivityForResult(),
                    callback
                )
        }

        // 启动 UCrop Activity
        val file = File(path.path)
        val name = file.name
        val outFile = File(outputFile, name)
        val sourceUri = Uri.fromFile(file)
        val destinationUri = Uri.fromFile(outFile)
        val options = UCrop.Options().apply {
            setFreeStyleCropEnabled(true)
            setToolbarTitle("自由裁剪")
            setToolbarColor(ContextCompat.getColor(requireActivity(), R.color.white))
            setStatusBarColor(ContextCompat.getColor(requireActivity(), R.color.white))
        }

        val uCropIntent = UCrop.of(sourceUri, destinationUri)
            .withOptions(options)
            .getIntent(requireActivity())
        uCropLauncher?.launch(uCropIntent)
    }
}