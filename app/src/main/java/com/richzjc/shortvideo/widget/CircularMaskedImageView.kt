package com.richzjc.shortvideo.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RadialGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class CircularMaskedImageView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val paint = Paint().apply {
        isAntiAlias = true
    }
    private var bitmap: Bitmap? = null
    private var maskBitmap: Bitmap? = null

    fun setImageBitmap(bitmap: Bitmap) {
        this.bitmap = bitmap
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        bitmap?.let {
            if (maskBitmap == null) {
                createMask(it.width, it.height)
            }
            maskBitmap?.let { mask ->
                canvas.drawBitmap(mask, 0f, 0f, paint)
                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                canvas.drawBitmap(it, 0f, 0f, paint)
                paint.xfermode = null
            }
        }
    }

    private fun createMask(width: Int, height: Int) {
        maskBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val maskCanvas = Canvas(maskBitmap!!)
        val radius = min(width, height) / 2f
        val centerX = width / 2f
        val centerY = height / 2f
        val gradient = RadialGradient(centerX, centerY, radius, intArrayOf(Color.TRANSPARENT, Color.BLACK), floatArrayOf(0.8f, 1f), Shader.TileMode.CLAMP)
        paint.shader = gradient
        maskCanvas.drawCircle(centerX, centerY, radius, paint)
        paint.shader = null
    }
}
