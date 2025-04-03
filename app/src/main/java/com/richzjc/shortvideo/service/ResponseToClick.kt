package com.richzjc.shortvideo.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityService.GestureResultCallback
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.content.Intent
import android.graphics.Path
import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import com.richzjc.shortvideo.MainActivity
import com.richzjc.shortvideo.UtilsContextManager
import com.richzjc.shortvideo.fragment.AutoFragment
import com.richzjc.shortvideo.util.requestData
import kotlinx.coroutines.delay

private val locate1 = mutableListOf(
    "946,2308,946,2308,1"
)

private val locateList = mutableListOf(
    "946,2308",
    "367,1190",
    "309,911",
    "108,2241",
    "536,200",
    "484,485",
    "214,310",
    "885,2310",
    "971,2177",
    "294,2231",
    "605,1528",
    "886,2041",
    "340,1938",
    "549,2101",
    "290,966",//点击封面
    "975,2064",
    "542,200",
    "428,644",
    "215,316",
    "939,2328",
    "948,2319",
    "552,1655",
    "566,2167",
    "335,210"
)

private val locateList2 = mutableListOf(
    "982,2307",
    "512,365",
    "541,2106",
    "394,1145"
)

private val themes = mutableListOf("经典歌曲", "热歌推荐", "太好听了", "句句入心", "好歌推荐")

private val IS_NORMAL = 0
private val IS_LAST = 1
private val IS_FABIAO = 2
private val IS_SEARCH_ACTIVITY = 3

fun AccessibilityService.responseToClick() {
    requestData {
        locateList.forEachIndexed { index, it ->
            delay(3000L)
            val arr = it.split(",")
            val isLast = index == locateList.size - 1
            if (isLast) {
                simulateClick(arr[0].toFloat(), arr[1].toFloat(), IS_SEARCH_ACTIVITY)
            } else {
                simulateClick(arr[0].toFloat(), arr[1].toFloat(), IS_NORMAL)
            }
        }
    }
}


private suspend fun AccessibilityService.responseToClickSearch() {
    locateList2.forEachIndexed { index, it ->
        delay(4000L)
        val arr = it.split(",")
        val isLast = index == locateList2.size - 1
        if (isLast) {
            simulateClick(arr[0].toFloat(), arr[1].toFloat(), IS_LAST)
        } else {
            simulateClick(arr[0].toFloat(), arr[1].toFloat(), IS_NORMAL)
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
            if (isLast == IS_SEARCH_ACTIVITY) {
                responseToSetText(
                    recursionEditNode(rootInActiveWindow, x, y),
                    "美女",
                    IS_SEARCH_ACTIVITY
                )
            } else if (isLast == IS_LAST) {
                responseToSetText(
                    recursionEditNode(rootInActiveWindow, x, y),
                    "千万不能让老婆看到，看到就后悔了#美女#少妇#完美身材#性感#经典歌曲",
                    IS_LAST
                )
            } else if (isLast == IS_FABIAO) {
                //TODO 循环
                requestData {
                    delay(60 * 1000L)
                    val intent = Intent(
                        UtilsContextManager.getInstance().application,
                        MainActivity::class.java
                    )
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


fun AccessibilityService.recursionEditNode(
    listNodes: AccessibilityNodeInfo,
    x: Float,
    y: Float
): AccessibilityNodeInfo? {
    val className: String = listNodes.className.toString() // 控件类名
    if (TextUtils.equals(className, "android.widget.EditText")) {
        val rect = Rect()
        listNodes.getBoundsInScreen(rect)
        if (x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom)
            return listNodes
    }

    if (listNodes != null && listNodes.childCount > 0) {
        (0 until listNodes.childCount).forEach {
            val node = listNodes.getChild(it)
            val className: String = node.className.toString() // 控件类名
            if (TextUtils.equals(className, "android.widget.EditText")){
                val rect = Rect()
                listNodes.getBoundsInScreen(rect)
                if (x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom)
                    return node
                else{
                    val nodel = recursionEditNode(node, x, y)
                    if (nodel != null)
                        return nodel
                }
            } else {
                val nodel = recursionEditNode(node, x, y)
                if (nodel != null)
                    return nodel
            }
        }
    }
    return null
}

private fun AccessibilityService.responseToSetText(
    editNodeInfo: AccessibilityNodeInfo?,
    faqunText: String,
    flag: Int
) {
    requestData {
        var realText = faqunText
        if (TextUtils.isEmpty(realText))
            realText = "#AI #美女"
        var startIndex = 0
        while (startIndex <= 10) {
            val arguments = Bundle()
            arguments.putCharSequence(
                AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                realText
            )
            editNodeInfo?.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
            delay(500L)
            startIndex += 1
        }

        if (flag == IS_LAST) {
            //    "938,220"
            simulateClick(938f, 220f, IS_FABIAO)
        } else if (flag == IS_SEARCH_ACTIVITY)
            responseToClickSearch()
    }
}
