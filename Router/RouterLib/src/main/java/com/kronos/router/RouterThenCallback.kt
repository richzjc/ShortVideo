package com.kronos.router

interface RouterThenCallback {
    //路由匹配成功后， 后续想继续做的事情， 扩展了这个功能主要是为了实现
    //三方登录， 支付， 闪验等，当隐私协议同意后才能初始化。
    fun routerThenCallback()
}