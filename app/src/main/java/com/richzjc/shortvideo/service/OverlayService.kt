package com.richzjc.shortvideo.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.richzjc.shortvideo.MainActivity
import com.richzjc.shortvideo.R
import com.richzjc.shortvideo.fragment.AutoFragment.Companion.isStartFlag


class OverlayService : Service() {
    private var windowManager: WindowManager? = null
    private var floatView: View? = null
    override fun onCreate() {
        super.onCreate()

        // 1. 初始化WindowManager
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager


        // 2. 创建悬浮窗布局参数
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )


        // 3. 设置初始位置（右上角）
        params.gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL

        // 4. 加载布局
        floatView = LayoutInflater.from(this).inflate(R.layout.view_float_window, null)


        // 5. 绑定返回按钮事件
        val btnBack: TextView = floatView!!.findViewById(R.id.back)
        btnBack.setOnClickListener { v: View? ->
            isStartFlag = false
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            // 移除悬浮窗并停止服务
            if (windowManager != null && floatView != null) {
                windowManager?.removeView(floatView)
            }
            stopSelf()
        }

        // 6. 添加视图到窗口
        if (Settings.canDrawOverlays(this)) {
            windowManager!!.addView(floatView, params)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}