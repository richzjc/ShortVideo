package com.faqun.service

import android.accessibilityservice.AccessibilityService
import android.text.TextUtils
import android.view.accessibility.AccessibilityEvent
import com.richzjc.shortvideo.fragment.AutoFragment.Companion.isStartFlag
import com.richzjc.shortvideo.service.responseToClick
import com.richzjc.shortvideo.util.requestData
import kotlinx.coroutines.delay


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
                    responseToClick()
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
                responseToClick()
            }
        }
    }

    override fun onInterrupt() {
    }

    companion object {
        var instance: FaQunAccessibilityService? = null
        var curClassName: String? = ""
    }
}