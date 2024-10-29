package com.richzjc.shortvideo.util

import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

const val SUCCESS: Int = 200


fun requestData(throwableCallback: ThrowableCallback? = null, sus: suspend () -> Unit): Job {
    return MainScope().launch {
        try {
            sus()
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            throwableCallback?.throwableCallback(throwable)
        }
    }
}


data class RequestResult<T>(val data: T?, val code: Int)


interface ThrowableCallback {
    fun throwableCallback(throwable: Throwable?)
}