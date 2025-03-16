package com.richzjc.shortvideo.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityService.GestureResultCallback
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.content.Intent
import android.graphics.Path
import android.os.Bundle
import android.text.TextUtils
import android.view.accessibility.AccessibilityNodeInfo
import com.richzjc.shortvideo.MainActivity
import com.richzjc.shortvideo.UtilsContextManager
import com.richzjc.shortvideo.fragment.AutoFragment
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
    "657,2333",
    "329,1038",
    "1091,2296",
    "612,181",
    "522,1178",
    "236,324",
    "1057,2572",
    "1076,2578",
    "506,1259"
)

private val fabiaoLocate = "1069,215"
private val IS_NORMAL = 0
private val IS_LAST = 1
private val IS_FABIAO = 2

fun AccessibilityService.responseToClick() {
    requestData {
        locateList.forEachIndexed { index, it ->
            delay(3000L)
            val arr = it.split(",")
            val isLast = index == locateList.size - 1
            if (isLast) {
                simulateClick(arr[0].toFloat(), arr[1].toFloat(), IS_LAST)
            } else {
                simulateClick(arr[0].toFloat(), arr[1].toFloat(), IS_NORMAL)
            }

        }
    }
}

private fun AccessibilityService.simulateClick(x: Float, y: Float, isLast: Int) {
    val path = Path()
    path.moveTo(x, y) // 移动到点击的起点（通常是点击位置）
    path.lineTo(x, y) // 再次画线到点击位置，形成一个点（实际上不需要移动，但为了完整性）
    val stroke = StrokeDescription(path, 0, 1) // 创建笔画描述，0为延迟时间，1为持续时间（毫秒）
    val gesture = GestureDescription.Builder().addStroke(stroke).build() // 创建手势描述并构建手势对象
    dispatchGesture(gesture, object : GestureResultCallback() {
        // 派发手势并监听结果回调
        override fun onCompleted(gesture: GestureDescription) {
            super.onCompleted(gesture) // 完成时的回调（可选）
            if (isLast == IS_LAST) {
                responseToSetText(recursionEditNode(rootInActiveWindow), "")
            } else if (isLast == IS_FABIAO) {
                //TODO 循环
                requestData {
                    delay(60  * 1000L)
                    val intent = Intent(UtilsContextManager.getInstance().application, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    UtilsContextManager.getInstance().application.startActivity(intent)
                    delay(2 * 1000L)
                    AutoFragment.responseToStart()
                }
            }
        }

        override fun onCancelled(gesture: GestureDescription) {
            super.onCancelled(gesture) // 取消时的回调（可选）
        }
    }, null)
}

private fun checkContainsEditText(contactNodes: List<AccessibilityNodeInfo>): Boolean {
    if (contactNodes == null || contactNodes.size <= 0)
        return false
    contactNodes.forEach {
        val className = it.className.toString()
        if (TextUtils.equals(className, "android.widget.EditText"))
            return true
    }
    return false
}


fun AccessibilityService.recursionEditNode(listNodes: AccessibilityNodeInfo): AccessibilityNodeInfo? {
    if (listNodes != null && listNodes.childCount > 0) {
        (0 until listNodes.childCount).forEach {
            val node = listNodes.getChild(it)
            val className: String = node.className.toString() // 控件类名
            if (TextUtils.equals(className, "android.widget.EditText"))
                return node
            else {
                val nodel = recursionEditNode(node)
                if (nodel != null)
                    return nodel
            }
        }
    }
    return null
}

private fun AccessibilityService.responseToSetText(
    editNodeInfo: AccessibilityNodeInfo?,
    faqunText: String
) {
    requestData {
        var realText = faqunText
        if (TextUtils.isEmpty(realText))
            realText = "#AI #美女"
        var contactNodes = rootInActiveWindow.findAccessibilityNodeInfosByText(realText)
        while (!checkContainsEditText(contactNodes)) {
            val arguments = Bundle()
            arguments.putCharSequence(
                AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                realText
            )
            editNodeInfo?.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)

            delay(300L)
            contactNodes = rootInActiveWindow.findAccessibilityNodeInfosByText(realText)
        }

        val arr = fabiaoLocate.split(",")
        simulateClick(arr[0].toFloat(), arr[1].toFloat(), IS_FABIAO)
    }
}
