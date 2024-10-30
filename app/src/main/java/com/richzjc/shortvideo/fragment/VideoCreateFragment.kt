package com.richzjc.shortvideo.fragment

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.huantansheng.easyphotos.EasyPhotos
import com.huantansheng.easyphotos.models.album.entity.Photo
import com.richzjc.shortvideo.R
import com.richzjc.shortvideo.dialog.selectPicFromCameraOrPic
import com.richzjc.shortvideo.fragment.videoCreate.gennerateVideoNoAudio
import com.richzjc.shortvideo.fragment.videoCreate.pinJiePianTou
import com.richzjc.shortvideo.fragment.videoCreate.processImage
import com.richzjc.shortvideo.fragment.videoCreate.responseHeChengNBA
import com.richzjc.shortvideo.util.MToastHelper
import com.richzjc.shortvideo.util.ResourceUtils
import com.richzjc.shortvideo.util.ScreenUtils
import com.richzjc.shortvideo.util.ShapeDrawable
import kotlin.concurrent.thread
import kotlin.math.min

class VideoCreateFragment : Fragment() {
    private val originPathList = ArrayList<String>()
    private var selectPicPath: String? = ""
    private var handlePic: ImageView? = null
    private var originPic: ImageView? = null
    private var status: TextView? = null
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
        return inflater.inflate(R.layout.video_fragment_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val selectVideo = view.findViewById<Button>(R.id.select_video)
        val selectPicBtn = view.findViewById<Button>(R.id.select_pic_btn)
        val tiqu = view.findViewById<Button>(R.id.tiqu)
        val process = view.findViewById<Button>(R.id.process)
        val hebing = view.findViewById<Button>(R.id.hebing)
        handlePic = view.findViewById(R.id.handle_pic)
        originPic = view.findViewById(R.id.origin_pic)
        status = view.findViewById(R.id.status)

        selectVideo.background = btnDrawable
        selectPicBtn.background = btnDrawable
        tiqu.background = btnDrawable
        process.background = btnDrawable
        hebing.background = btnDrawable

        selectVideo?.setOnClickListener {
            responseToSelectVideo()
        }

        selectPicBtn?.setOnClickListener {
            responseToSelectPic()
        }

        tiqu.setOnClickListener {
            MToastHelper.showToast("需要先删除本地文件")
            thread {
                responseHeChengNBA(requireContext(), originPathList, status)
            }
        }

        process.setOnClickListener {
            MToastHelper.showToast("需要先删除本地文件")
            thread {
                processImage(requireContext(), status, selectPicPath, handlePic, originPic)
            }
        }

        hebing.setOnClickListener {
            MToastHelper.showToast("需要先删除本地文件")
            thread {
                originPathList?.get(0)?.also {
                    gennerateVideoNoAudio(it, requireContext(), status, 0)
                }
            }
        }
    }

    private fun responseToSelectVideo() {
        selectPicFromCameraOrPic(requireActivity(), 1, false, null) { resultCode, data ->
            if (resultCode === RESULT_OK) {
                originPathList.clear()
                val resultPhotos: ArrayList<Photo>? =
                    data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS)
                if (resultPhotos != null && resultPhotos.size >= 1) {
                    val endSize = min(1, resultPhotos.size)
                    (0 until endSize)?.forEach {
                        originPathList.add(resultPhotos[it].path)
                    }
                }
            }
        }
    }

    private fun responseToSelectPic() {
        selectPicFromCameraOrPic(requireActivity(), 1, true, null) { resultCode, data ->
            if (resultCode === RESULT_OK) {
                val resultPhotos: ArrayList<Photo>? =
                    data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS)
                if (resultPhotos != null && resultPhotos.size >= 1) {
                    val endSize = min(1, resultPhotos.size)
                    (0 until endSize)?.forEach {
                        selectPicPath = resultPhotos[it].path
                    }
                }
            }
        }
    }
}