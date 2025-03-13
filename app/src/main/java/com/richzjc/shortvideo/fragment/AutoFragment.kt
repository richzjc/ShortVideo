package com.richzjc.shortvideo.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.richzjc.shortvideo.R
import com.richzjc.shortvideo.fragment.autoVideo.responseToGetAudioFileDuration
import com.richzjc.shortvideo.fragment.autoVideo.responseToGetPianTouFileDuration
import com.richzjc.shortvideo.fragment.autoVideo.responseToHandlePic
import com.richzjc.shortvideo.fragment.autoVideo.responseToSelectAudioFile
import com.richzjc.shortvideo.fragment.autoVideo.responseToSelectPianTouFile
import com.richzjc.shortvideo.fragment.autoVideo.responseToSelectPicFile
import com.richzjc.shortvideo.util.MToastHelper
import com.richzjc.shortvideo.util.ResourceUtils
import com.richzjc.shortvideo.util.ScreenUtils
import com.richzjc.shortvideo.util.ShapeDrawable
import com.richzjc.shortvideo.util.requestData
import java.io.File

class AutoFragment : Fragment() {
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
        return inflater.inflate(R.layout.auto_video_fragment_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val select_pic = view.findViewById<Button>(R.id.select_pic)
        status = view.findViewById(R.id.status)
        select_pic.background = btnDrawable

        select_pic?.setOnClickListener {
            MToastHelper.showToast("先到图片编辑页面获取读写权限")
            isStartFlag = !isStartFlag
            if (isStartFlag)
                select_pic.text = "暂停"
            else
                select_pic.text = "开始"
            if (isStartFlag) {
                requestData {
                    responseToStart()
                }
            }

        }
    }

    private suspend fun responseToStart() {
        //TODO 第一步，选择音频文件， 计算出需要多少张图片
        updateStatusText("获取音频文件", status)
        audioFile = responseToSelectAudioFile()
        audioFile ?: return
        if (!isStartFlag) return
        audioFileDuration = responseToGetAudioFileDuration(audioFile!!)
        if (audioFileDuration <= 0)
            return

        if (!isStartFlag) return
        updateStatusText("音频时长为：${audioFileDuration}秒", status)

        pianTouFile = responseToSelectPianTouFile()
        pianTouFile ?: return
        if (!isStartFlag) return
        pianTouFileDuration = responseToGetPianTouFileDuration(pianTouFile!!)
        if (pianTouFileDuration <= 0)
            return

        //TODO 第二步，选择图片文件
        if (!isStartFlag) return
        updateStatusText("选择图片文件", status)
        picList = responseToSelectPicFile(audioFileDuration, pianTouFileDuration)
        if (picList == null || picList!!.isEmpty())
            return
        //TODO 第三步，处理图片
        updateStatusText("开始处理图片文件", status)
        if (!isStartFlag) return
        responseToHandlePic(requireContext(), picList!!, audioFileDuration, pianTouFileDuration)
        //TODO 第四步，将处理图片，生成视频
        //TODO 第五步，拼接片头视频
        //TODO 第六步，合并音频文件, 并且删除之前的图片文件
        //TODO 第七步，启动微信
        //TODO 第八步，跳转到我的页面
        //TODO 第九步，点击视频号
        //TODO 第十步，点击发表视频
        //TODO 第十一步， 点击从相册选择视频
        //TODO 第十二步， 点击图片和视频
        //TODO 第十三步， 点击所有视频
        //TODO 第十四步， 选择第一条视频
        //TODO 第十五步， 点击下一步
        //TODO 第十六步， 点击完成按钮
        //TODO 第十七步， 输入话题
        //TODO 第十八步， 点击原创声明
        //TODO 第十九步， 勾选复选框
        //TODO 第二十步， 点击声明原创
        //TODO 第二十一步，
    }

    companion object {
        var isStartFlag = false
        var status: TextView? = null
        var audioFile: File? = null
        var pianTouFile: File? = null
        var audioFileDuration: Long = 0L
        var pianTouFileDuration: Long = 0L
        var picList: List<File>? = null

        fun updateStatusText(statusText: String?, statusTV: TextView?) {
            if (Looper.myLooper() != Looper.getMainLooper())
                Handler(Looper.getMainLooper()).post {
                    statusTV?.text = statusText ?: ""
                }
            else
                statusTV?.text = statusText ?: ""
        }
    }
}