package com.richzjc.shortvideo.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.richzjc.shortvideo.R
import com.richzjc.shortvideo.util.ScreenUtils

class TestView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var upperImage = BitmapFactory.decodeResource(resources, R.mipmap.test)
    private var lowerImage = BitmapFactory.decodeResource(resources, R.mipmap.img)
    private val featherRadius = 30f // 羽化半径，可以根据需求调整

    init {
        upperImage = Bitmap.createScaledBitmap(upperImage, (ScreenUtils.getScreenWidth() * 0.5f).toInt(), (ScreenUtils.getScreenWidth() * 0.5f).toInt(), true)
        lowerImage = Bitmap.createScaledBitmap(lowerImage, ScreenUtils.getScreenWidth(), ScreenUtils.getScreenWidth(), true)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(lowerImage.width, lowerImage.height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//
//        // 创建一个图层来绘制上层图片和蒙版
//        val saveLayer = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), paint)
//        // 绘制下层图片
//        canvas.drawBitmap(lowerImage, 0f, 0f, paint)
//         设置羽化效果
//        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)

        paint.maskFilter = BlurMaskFilter(featherRadius, BlurMaskFilter.Blur.NORMAL)

//         绘制上层图片
        canvas.drawBitmap(
            upperImage,
            (width - upperImage.width) / 2f,
            (height - upperImage.height) / 2f,
            paint
        )



//        // 初始化蒙版路径为圆形
//        val maskPath = Path()
//        val centerX = width / 2f
//        val centerY = height / 2f
//        val radius = Math.min(upperImage.width, upperImage.height) * 0.2f
//        maskPath.addCircle(centerX, centerY, radius, Path.Direction.CCW)
//        // 绘制蒙版
//        canvas.drawPath(maskPath, paint)

        // 清除 xfermode 和蒙版效果
        paint.xfermode = null
        paint.maskFilter = null

//        // 恢复画布
//        canvas.restoreToCount(saveLayer)
    }
}