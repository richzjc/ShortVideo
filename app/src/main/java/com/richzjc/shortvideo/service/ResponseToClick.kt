package com.richzjc.shortvideo.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityService.GestureResultCallback
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.graphics.Path
import com.richzjc.shortvideo.util.requestData
import kotlinx.coroutines.delay

private val locateList = mutableListOf(
    "1050,2586",
    "600,1311",
    "383,839",
    "122,2497",
    "607,167",
    "479,492",
    "250,316",
    "1024,2591",
    "1073,2452",
    "933,2442",
    "330,2149",
    "657,2333"
)

private val fabiaoLocate = "1069,215"

fun AccessibilityService.responseToClick() {
    requestData {
        locateList.forEach {
            delay(3000L)
            val arr = it.split(",")
            simulateClick(arr[0].toFloat(), arr[1].toFloat())
        }
    }
}

private fun AccessibilityService.simulateClick(x: Float, y: Float) {
    val path = Path()
    path.moveTo(x, y) // 移动到点击的起点（通常是点击位置）
    path.lineTo(x, y) // 再次画线到点击位置，形成一个点（实际上不需要移动，但为了完整性）
    val stroke = StrokeDescription(path, 0, 1) // 创建笔画描述，0为延迟时间，1为持续时间（毫秒）
    val gesture = GestureDescription.Builder().addStroke(stroke).build() // 创建手势描述并构建手势对象
    dispatchGesture(gesture, object : GestureResultCallback() {
        // 派发手势并监听结果回调
        override fun onCompleted(gesture: GestureDescription) {
            super.onCompleted(gesture) // 完成时的回调（可选）
        }

        override fun onCancelled(gesture: GestureDescription) {
            super.onCancelled(gesture) // 取消时的回调（可选）
        }
    }, null)
}