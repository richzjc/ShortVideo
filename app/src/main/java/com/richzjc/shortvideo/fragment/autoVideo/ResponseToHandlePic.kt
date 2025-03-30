package com.richzjc.shortvideo.fragment.autoVideo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.Log
import android.widget.TextView
import com.richzjc.shortvideo.fragment.AutoFragment
import com.richzjc.shortvideo.fragment.autoVideo.fangan.fangan1
import com.richzjc.shortvideo.fragment.autoVideo.fangan.fangan10
import com.richzjc.shortvideo.fragment.autoVideo.fangan.fangan2
import com.richzjc.shortvideo.fragment.autoVideo.fangan.fangan3
import com.richzjc.shortvideo.fragment.autoVideo.fangan.fangan4
import com.richzjc.shortvideo.fragment.autoVideo.fangan.fangan5
import com.richzjc.shortvideo.fragment.autoVideo.fangan.fangan6
import com.richzjc.shortvideo.fragment.autoVideo.fangan.fangan7
import com.richzjc.shortvideo.fragment.autoVideo.fangan.fangan8
import com.richzjc.shortvideo.fragment.autoVideo.fangan.fangan9
import kotlinx.coroutines.delay
import java.io.File

suspend fun responseToHandlePic(
    context: Context,
    picList: List<File>,
    audioFileDuration: Long,
    status: TextView?
) {
    try {
        delay(1000L)
        val file1 = File(context.externalCacheDir, "imageHandle")
        if (!file1.exists())
            file1.mkdirs()

        if (file1 != null && file1.exists()) {
            val listFiles = file1.listFiles()
            listFiles?.forEach {
                if (it.exists()) {
                    val delResult = it.delete()
                    Log.d("short", "delResult = ${delResult}")
                }
            }
        }

        val totalPicCount = (audioFileDuration / 33f).toInt()
        val paint = Paint()
        // 设置画笔去掉透明度
        paint.isAntiAlias = true
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
        paint.alpha = 255

        var picStartIndex = 0
        val fangAnList = mutableListOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
        while (file1.listFiles().size < totalPicCount) {
            if (picStartIndex >= picList.size)
                picStartIndex = 0

            var curBitmap = BitmapFactory.decodeFile(picList.get(picStartIndex).absolutePath)
            curBitmap = Bitmap.createScaledBitmap(curBitmap, 1080, 1920, true)
            var preBitmap: Bitmap? = null
            val fileList = file1.listFiles()
            if (fileList.isNotEmpty()) {
                val bfile = File(file1, "${fileList.size}.png")
                preBitmap = BitmapFactory.decodeFile(bfile.absolutePath)
                preBitmap = Bitmap.createScaledBitmap(preBitmap, 1080, 1920, true)
            } else {
                var outputBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(outputBitmap)
                canvas.drawBitmap(curBitmap, 0f, 0f, paint)
                preBitmap = outputBitmap
            }

            if (fangAnList.size <= 0) {
                fangAnList.add(0)
                fangAnList.add(1)
                fangAnList.add(2)
                fangAnList.add(3)
                fangAnList.add(4)
                fangAnList.add(5)
                fangAnList.add(6)
                fangAnList.add(7)
                fangAnList.add(8)
                fangAnList.add(9)
            }

            var index = (0 until fangAnList.size).random()
            val random = fangAnList.get(index)
            fangAnList.remove(random)
            //TODO 这一行是测试代码
            fangan2(file1, preBitmap, curBitmap, status, totalPicCount, paint)

//            if (random == 0) {
//                AutoFragment.updateStatusText("执行方案1", status)
//                fangan1(file1, preBitmap, curBitmap, status, totalPicCount, paint)
//            }else if(random == 1){
//                AutoFragment.updateStatusText("执行方案2", status)
//                fangan2(file1, preBitmap, curBitmap, status, totalPicCount, paint)
//            }else if(random == 2){
//                AutoFragment.updateStatusText("执行方案3", status)
//                fangan3(file1, preBitmap, curBitmap, status, totalPicCount, paint)
//            }else if(random == 3){
//                AutoFragment.updateStatusText("执行方案4", status)
//                fangan4(file1, preBitmap, curBitmap, status, totalPicCount, paint)
//            }else if(random == 4){
//                AutoFragment.updateStatusText("执行方案5", status)
//                fangan5(file1, preBitmap, curBitmap, status, totalPicCount, paint)
//            }else if(random == 5){
//                AutoFragment.updateStatusText("执行方案6", status)
//                fangan6(file1, preBitmap, curBitmap, status, totalPicCount, paint)
//            }else if(random == 6){
//                AutoFragment.updateStatusText("执行方案7", status)
//                fangan7(file1, preBitmap, curBitmap, status, totalPicCount, paint)
//            }else if(random == 7){
//                AutoFragment.updateStatusText("执行方案8", status)
//                fangan8(file1, preBitmap, curBitmap, status, totalPicCount, paint)
//            }else if(random == 8){
//                AutoFragment.updateStatusText("执行方案9", status)
//                fangan9(file1, preBitmap, curBitmap, status, totalPicCount, paint)
//             }else if(random == 9){
//                AutoFragment.updateStatusText("执行方案10", status)
//                fangan10(file1, preBitmap, curBitmap, status, totalPicCount, paint)
//            }
            picStartIndex += 1
        }
    } catch (exception: Exception) {
        exception.printStackTrace()
        Log.e("short", "处理图片异常了： msg = ${exception.message}")
    }
}






