package com.richzjc.shortvideo.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.LinearLayout
import android.widget.TextView
import com.richzjc.shortvideo.R

class OverLayLinearLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
   private val locateTv by lazy {
       findViewById<TextView>(R.id.locate)
   }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event?.action == MotionEvent.ACTION_DOWN){
            locateTv?.text = "x = ${event.rawX}; y  = ${event.rawY}"
        }
        return super.onTouchEvent(event)
    }
}