package com.richzjc.shortvideo.callback

import android.os.Parcelable
import androidx.fragment.app.FragmentActivity
import com.kronos.router.BindRouter
import com.kronos.router.RouterCallback
import com.kronos.router.RouterContext
import com.kronos.router.RouterThenCallback

@BindRouter(urls = ["wscn://wallstreetcn.com/show/user/privacy/dialog"], isRunnable = true)
class UserPrivacyCallback : RouterCallback {
    override fun run(context: RouterContext) {
        val entity = context?.extras?.getParcelable<Parcelable>("then")
        (entity as? RouterThenCallback)?.routerThenCallback()
    }
}