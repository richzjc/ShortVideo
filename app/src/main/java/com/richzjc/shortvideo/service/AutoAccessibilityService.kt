package com.faqun.service

import android.accessibilityservice.AccessibilityService
import android.text.TextUtils
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.richzjc.shortvideo.fragment.AutoFragment.Companion.isStartFlag
import com.richzjc.shortvideo.service.responseToClick
import com.richzjc.shortvideo.util.requestData
import kotlinx.coroutines.delay


class AutoAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    fun startAccessibilityService() {
        if (TextUtils.equals(curClassName, "com.tencent.mm.ui.LauncherUI")) {
            isStartFlag = true
            requestData {
                val random = (1..2).random()
                delay(random * 1000L)
                if (isStartFlag) {
                    responseToClick()
                }
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event != null && event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if(!TextUtils.equals(curClassName, event?.className.toString()))
                logId(rootInActiveWindow)
            curClassName = event?.className?.toString()
        }

        event ?: return

        if (!isStartFlag)
            return

        if (!TextUtils.equals(event.packageName, "com.tencent.mm"))
            return

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (TextUtils.equals("com.tencent.mm.ui.LauncherUI", event.className)) {
                responseToClick()
            }
        }
    }

    private fun logId(rootInActiveWindow: AccessibilityNodeInfo?) {
        rootInActiveWindow ?: return
        Log.e("short", "className = ${rootInActiveWindow.className.toString()}")
        if(rootInActiveWindow.childCount > 0){
            (0 until rootInActiveWindow.childCount)?.forEach {
                logId(rootInActiveWindow.getChild(it))
            }
        }
    }

    override fun onInterrupt() {
    }

    companion object {
        var instance: AutoAccessibilityService? = null
        var curClassName: String? = ""
    }
}