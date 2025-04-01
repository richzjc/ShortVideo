package com.richzjc.shortvideo.fragment.autoVideo.fangan.interpreter

import kotlin.math.cos
import kotlin.math.sin

//先快， 后慢
fun calculateSin(frame: Int, totalFrames: Int, maxValue : Float): Float {
    val progress = frame.toFloat() / totalFrames
    val sineValue = sin(progress * Math.PI / 2).toFloat() // 0 → π/2区间
    val value = (sineValue * maxValue)
    if(value < 1)
        return 0f
    else
        return value
}


//先慢，后快
fun calculateCos(frame: Int, totalFrames: Int, maxValue : Float): Float {
    val progress = frame.toFloat() / totalFrames
    val sineValue = cos(progress * Math.PI / 2).toFloat() // 0 → π/2区间
    val value = (sineValue * maxValue)
    if(value < 1)
        return 0f
    else
        return value
}


// 越来越快（y = x²）
fun calculatex2(frame: Int, totalFrames: Int, maxValue :Float): Float {
    val progress = frame.toFloat() / totalFrames // 归一化到[0,1]
    val quadraticValue = progress * progress     // 应用二次函数
    val value = (quadraticValue * maxValue)       // 映射到0-255
    if(value < 1)
        return 0f
    else
        return value
}

