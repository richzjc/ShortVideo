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
        isStartFlag = true
        requestData {
            delay(3000L)
            if (isStartFlag) {
                responseToClick()
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event != null && event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            curClassName = event?.className?.toString()
        }
    }
    

    override fun onInterrupt() {
    }

    companion object {
        var instance: AutoAccessibilityService? = null
        var curClassName: String? = ""
    }
}