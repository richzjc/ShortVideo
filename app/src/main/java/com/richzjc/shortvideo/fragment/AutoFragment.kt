package com.richzjc.shortvideo.fragment

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.faqun.service.AutoAccessibilityService
import com.huantansheng.easyphotos.EasyPhotos
import com.huantansheng.easyphotos.models.album.entity.Photo
import com.richzjc.shortvideo.R
import com.richzjc.shortvideo.UtilsContextManager
import com.richzjc.shortvideo.dialog.selectPicFromCameraOrPic
import com.richzjc.shortvideo.fragment.autoVideo.genHandleVideo
import com.richzjc.shortvideo.fragment.autoVideo.responseToGetAudioFileDuration
import com.richzjc.shortvideo.fragment.autoVideo.responseToHandlePic
import com.richzjc.shortvideo.fragment.autoVideo.responseToMergeAudio
import com.richzjc.shortvideo.fragment.autoVideo.responseToPinJieVideo
import com.richzjc.shortvideo.fragment.autoVideo.responseToSelectAudioFile
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

    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        // 获取AccessibilityManager实例
        val accessibilityManager =
            context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager


        // 检查无障碍服务是否开启
        if (accessibilityManager.isEnabled) {
            // 获取已启用的无障碍服务列表
            val enabledServices =
                accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)


            // 遍历已启用服务以查找目标服务
            for (serviceInfo in enabledServices) {
                if (serviceInfo.id.contains("AutoAccessibilityService")) {
                    return true // 目标服务已开启
                }
            }
        }
        return false // 目标服务未开启
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val select_pic = view.findViewById<Button>(R.id.select_pic)
        status = view.findViewById(R.id.status)
        val openAssit = view.findViewById<Button>(R.id.open_assit)
        val select_audio = view.findViewById<Button>(R.id.select_audio)
        select_pic.background = btnDrawable
        openAssit.background = btnDrawable
        select_audio.background = btnDrawable
        select_audio.visibility = View.GONE

        openAssit.setOnClickListener {
            if (!isAccessibilityServiceEnabled(requireContext())) {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            } else {
                MToastHelper.showToast("已经开启辅助功能权限")
            }
        }

        select_audio.setOnClickListener {

        }

        select_pic?.setOnClickListener {
//            if (!isAccessibilityServiceEnabled(requireContext())) {
//                MToastHelper.showToast("请开启辅助功能权限")
//                return@setOnClickListener
//            }

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


    companion object {
        var isStartFlag = false
        var status: TextView? = null
        var audioFile: File? = null
        var audioFileDuration: Long = 0L
        var picList: List<File>? = null

        fun updateStatusText(statusText: String?, statusTV: TextView?) {
            if (Looper.myLooper() != Looper.getMainLooper())
                Handler(Looper.getMainLooper()).post {
                    statusTV?.text = statusText ?: ""
                }
            else
                statusTV?.text = statusText ?: ""
        }


        suspend fun responseToStart() {
            //TODO 第一步，选择音频文件， 计算出需要多少张图片
            updateStatusText("获取音频文件", status)
            audioFile = responseToSelectAudioFile(status)
            audioFile ?: return
            if (!isStartFlag) return
            audioFileDuration = responseToGetAudioFileDuration(audioFile!!)
            if (audioFileDuration <= 0)
                return

            audioFileDuration = 3000L

            if (!isStartFlag) return
            updateStatusText("音频时长为：${audioFileDuration}秒", status)

            //TODO 第二步，选择图片文件
            if (!isStartFlag) return
            updateStatusText("选择图片文件", status)
            picList = responseToSelectPicFile(audioFileDuration)
            if (picList == null || picList!!.isEmpty())
                return
            //TODO 第三步，处理图片
            if (!isStartFlag) return
            updateStatusText("开始处理图片文件", status)
            val lastIndex = audioFile!!.name.lastIndexOf(".")
            val fileName = audioFile!!.name.substring(0, lastIndex)
            responseToHandlePic(
                UtilsContextManager.getInstance().application,
                picList!!,
                audioFileDuration,
                fileName,
                status
            )
            //TODO 第四步，将处理图片，生成视频
            if (!isStartFlag) return
            val genNoVideoFlag =
                genHandleVideo(UtilsContextManager.getInstance().application, status)
            if (!genNoVideoFlag)
                return
            //TODO 第五步，添加歌词
            if (!isStartFlag) return
            val pinJieVideoFlag = responseToPinJieVideo(
                UtilsContextManager.getInstance().application,
                audioFile!!,
                status
            )
            if (!pinJieVideoFlag)
                return
            //TODO 第六步，合并音频文件
            if (!isStartFlag) return
            val mergeAudioVideoFlag = responseToMergeAudio(
                UtilsContextManager.getInstance().application,
                audioFile!!,
                status
            )
            if (!mergeAudioVideoFlag)
                return

//            //TODO 第七步，启动微信
//            if (!isStartFlag) return
//            val intent = Intent()
//            intent.setClassName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI")
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//            UtilsContextManager.getInstance().application.startActivity(intent)
//            AutoAccessibilityService.instance?.startAccessibilityService()
        }
    }
}