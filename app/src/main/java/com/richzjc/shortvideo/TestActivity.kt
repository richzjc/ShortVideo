package com.richzjc.shortvideo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import kotlin.math.min

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_activity_test)

//        val imageView = findViewById<ImageView>(R.id.circularMaskedImageView)
//        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.test)
//        val featheredBitmap = makeEdgesOpaque(bitmap)
//
//        // 显示羽化处理后的图片
//        imageView.setImageBitmap(featheredBitmap)
    }


    fun makeEdgesOpaque(originalBitmap: Bitmap): Bitmap {
        val width = originalBitmap.width
        val height = originalBitmap.height
        val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outputBitmap)
        val paint = Paint()

        // 设置画笔去掉透明度
        paint.isAntiAlias = true
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
        paint.alpha = 255

        // 绘制原始 Bitmap 到新的 Bitmap 上
        canvas.drawBitmap(originalBitmap, 0f, 0f, paint)

        // 处理边缘
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (x < 88 || y < 88) {
                    val alpha = (((min(x, y) + 1) / 88.0) * 255.0).toInt()
                    val pixel = outputBitmap.getPixel(x, y)
                    val newPixel = Color.argb(
                        alpha, // 设置新的透明度为不透明
                        Color.red(pixel),
                        Color.green(pixel),
                        Color.blue(pixel)
                    )
                    outputBitmap.setPixel(x, y, newPixel)
                }else if(x > width - 88 || y > height - 88){
                    val alpha = (((min(width - x, height - y) + 1) / 88.0) * 255.0).toInt()
                    val pixel = outputBitmap.getPixel(x, y)
                    val newPixel = Color.argb(
                        alpha, // 设置新的透明度为不透明
                        Color.red(pixel),
                        Color.green(pixel),
                        Color.blue(pixel)
                    )
                    outputBitmap.setPixel(x, y, newPixel)
                }
            }
        }

        return outputBitmap
    }

}