package com.richzjc.shortvideo.fragment.autoVideo.fangan.interpreter

import kotlin.math.sin

//先快， 后慢
fun calculateSin(frame: Int, totalFrames: Int, maxValue : Float): Float {
    val progress = frame.toFloat() / totalFrames
    val sineValue = sin(progress * Math.PI / 2).toFloat() // 0 → π/2区间
    return (sineValue * maxValue)
}


//先慢，后快
fun calculateCos(frame: Int, totalFrames: Int, maxValue : Float): Float {
    val progress = frame.toFloat() / totalFrames
    val sineValue = sin(progress * Math.PI / 2).toFloat() // 0 → π/2区间
    return (sineValue * maxValue)
}


// 越来越快（y = x²）
fun calculatex2(frame: Int, totalFrames: Int, maxValue :Float): Float {
    val progress = frame.toFloat() / totalFrames // 归一化到[0,1]
    val quadraticValue = progress * progress     // 应用二次函数
    return (quadraticValue * maxValue)       // 映射到0-255
}

