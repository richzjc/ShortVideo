package com.faqun.service

import android.accessibilityservice.AccessibilityService
import android.text.TextUtils
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import kotlinx.coroutines.delay

var isStartFlag = false

class FaQunAccessibilityService : AccessibilityService() {

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
                    chatGroupList = null
                    responseToContact()
                }
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event != null && event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            curClassName = event?.className?.toString()
        }

        event ?: return

        if (!isStartFlag)
            return

        if (!TextUtils.equals(event.packageName, "com.tencent.mm"))
            return

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (TextUtils.equals("com.tencent.mm.ui.LauncherUI", event.className)) {
                chatGroupList = null
                responseToContact()
            }

            if (TextUtils.equals("com.tencent.mm.ui.contact.ChatroomContactUI", event.className))
                responseToQunLiaoList()

            if (TextUtils.equals("com.tencent.mm.ui.chatting.ChattingUI", event.className))
                responseToChat()
        }
    }

    override fun onInterrupt() {
    }

    companion object {
        var instance: FaQunAccessibilityService? = null
        var curClassName: String? = ""
        var chatGroupList: ArrayList<AccessibilityNodeInfo>? = null
    }
}